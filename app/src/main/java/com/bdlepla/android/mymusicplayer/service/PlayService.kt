package com.bdlepla.android.mymusicplayer.service

import android.app.PendingIntent
import android.app.PendingIntent.FLAG_IMMUTABLE
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.media3.cast.CastPlayer
import androidx.media3.cast.SessionAvailabilityListener
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.util.EventLogger
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionError
import androidx.media3.session.SessionResult
import com.bdlepla.android.mymusicplayer.datastore.MyMusicPlayerSettingsDataStore
import com.google.android.gms.cast.framework.CastContext
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


class PlayService: MediaLibraryService() {
    private lateinit var mediaLibrarySession: MediaLibrarySession

    private val librarySessionCallback = CustomMediaLibrarySessionCallback()
    private val playerListener = PlayerListener()
    private val musicDataStore: MyMusicPlayerSettingsDataStore by lazy { MyMusicPlayerSettingsDataStore(this) }
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val handler:Handler by lazy { Handler(currentPlayer.applicationLooper) }
    private val myPlayerAudioAttributesBuilder = AudioAttributes.Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)

    private val exoPlayer: ExoPlayer by lazy {
        val ret = ExoPlayer.Builder(this).build().apply {
            setAudioAttributes(myPlayerAudioAttributesBuilder.build(), true)
            setHandleAudioBecomingNoisy(true)
            addListener(playerListener)
        }
        ret.addAnalyticsListener(EventLogger("MyMusicPlayer"))
        ret
    }

    private val castPlayer: CastPlayer? by lazy {
        try {
            val castContext = CastContext.getSharedInstance(this)
            CastPlayer(castContext).apply {
                setSessionAvailabilityListener(CastSessionAvailabilityListener())
                addListener(playerListener)
            }
        } catch (e: Exception) {
            // We wouldn't normally catch the generic `Exception` however
            // calling `CastContext.getSharedInstance` can throw various exceptions, all of which
            // indicate that Cast is unavailable.
            // Related internal bug b/68009560.
            Log.i(
                TAG, "Cast is not available on this device. " +
                        "Exception thrown when attempting to obtain CastContext. " + e.message
            )
            null
        }
    }

    private val currentPlayer: ReplaceableForwardingPlayer by lazy {
        ReplaceableForwardingPlayer(exoPlayer)
    }

    private fun initializeMediaSession() {
        mediaLibrarySession =
            with(MediaLibrarySession.Builder(this@PlayService, currentPlayer, librarySessionCallback)) {
                setId(packageName)
                packageManager?.getLaunchIntentForPackage(packageName)?.let { sessionIntent ->
                    setSessionActivity(
                        PendingIntent.getActivity(
                            this@PlayService,
                            0,
                            sessionIntent,
                            FLAG_IMMUTABLE
                        )
                    )
                }
                build()
            }
        setMediaNotificationProvider(CustomMediaNotificationProvider(this@PlayService))
    }


//    private fun setupCustomCommands() {
//        customCommands =
//            listOf(
//                getShuffleCommandButton(
//                    SessionCommand(CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_ON, Bundle.EMPTY)
//                ),
//                getShuffleCommandButton(
//                    SessionCommand(CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_OFF, Bundle.EMPTY)
//                )
//            )
//        customLayout = ImmutableList.of(customCommands[0])
//    }

//    private fun getShuffleCommandButton(sessionCommand: SessionCommand): CommandButton {
//        val isOn = sessionCommand.customAction == CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_ON
//        return CommandButton.Builder()
//            .setDisplayName(if (isOn) getShuffleOnDescription() else getShuffleOffDescription())
//            .setSessionCommand(sessionCommand)
//            //.setIconResId(if (isOn) R.drawable.exo_icon_shuffle_off else R.drawable.exo_icon_shuffle_on)
//            .build()
//    }

//    private fun getShuffleOnDescription(): String {
//        //getString( R.string.exo_controls_shuffle_on_description)
//        return "Shuffle on"
//    }
//
//    private fun getShuffleOffDescription(): String {
//        //getString(R.string.exo_controls_shuffle_off_description)
//        return "Shuffle off"
//    }

    override fun onCreate() {
        super.onCreate()
        MediaItemTree.initialize(this)
        if (castPlayer?.isCastSessionAvailable == true) {
            currentPlayer.setPlayer(castPlayer!!)
        }
        //setupCustomCommands()
        initializeMediaSession()
    }

    override fun onDestroy() {
        releaseMediaSession()
        super.onDestroy()
    }

    private fun releaseMediaSession() {
        this.mediaLibrarySession.run {
            release()
            if (player.playbackState != Player.STATE_IDLE) {
                player.removeListener(playerListener)
                player.release()
            }
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession = mediaLibrarySession


    private fun checkPlaybackPosition(player: Player): Boolean = handler.postDelayed({
        if (player.isPlaying) {
            val currPositionInMs = player.currentPosition
            val currentlyPlaying = player.currentMediaItem
            if (currentlyPlaying != null) {
                scope.launch {
                    musicDataStore.saveCurrentPlaying(currentlyPlaying.mediaId.substring(6).toLong(), currPositionInMs)
                }
            }
        }
        checkPlaybackPosition(player)
    }, POSITION_UPDATE_INTERVAL_MILLIS)

//    private fun setMediaItemFromSearchQuery(query: String) {
//        // Only accept query with pattern "play [Title]" or "[Title]"
//        // Where [Title]: must be exactly matched
//        // If no media with exact name found, play a random media instead
//        val mediaTitle =
//            if (query.startsWith("play ", ignoreCase = true)) {
//                query.drop(5)
//            } else {
//                query
//            }
//
//        val item = MediaItemTree.getItemFromTitle(mediaTitle) ?: MediaItemTree.getRandomItem()
//        player.setMediaItem(item)
//        player.prepare()
//    }

    companion object {
        //private const val SEARCH_QUERY_PREFIX_COMPAT = "androidx://media3-session/playFromSearch"
        //private const val SEARCH_QUERY_PREFIX = "androidx://media3-session/setMediaUri"
        //private const val TAG = "MusicService"
        private const val CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_ON = "android.media3.session.demo.SHUFFLE_ON"
        private const val CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_OFF = "android.media3.session.demo.SHUFFLE_OFF"
        private const val POSITION_UPDATE_INTERVAL_MILLIS = 500L
    }

    private inner class CastSessionAvailabilityListener : SessionAvailabilityListener {

        /**
         * Called when a Cast session has started and the user wishes to control playback on a
         * remote Cast receiver rather than play audio locally.
         */
        override fun onCastSessionAvailable() {
            currentPlayer.setPlayer(castPlayer!!)
        }

        /**
         * Called when a Cast session has ended and the user wishes to control playback locally.
         */
        override fun onCastSessionUnavailable() {
            currentPlayer.setPlayer(exoPlayer)
        }
    }

   private inner class PlayerListener: Player.Listener {
//        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
//            super.onMediaMetadataChanged(mediaMetadata)
////            val uri = mediaMetadata.artworkUri?.toString() ?: return
////            val fis = FileInputStream(uri)
////            val buffer = fis.readBytes()
////            val newMetadata = MediaMetadata.Builder()
////                .populate(mediaMetadata)
////                .setArtworkData(buffer, MediaMetadata.PICTURE_TYPE_FRONT_COVER)
////                .build()
//            //sessionCompat.setMetadata(newMetadata)
//
//        }
    }

    private inner class CustomMediaLibrarySessionCallback : MediaLibrarySession.Callback {
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {
            val connectionResult = super.onConnect(session, controller)

            val availableSessionCommands = connectionResult.availableSessionCommands.buildUpon()
//            customCommands.forEach { commandButton ->
//                // Add custom command to available session commaMediaLibrarySessionnds.
//                // these are cached to be used as a lookup when the user presses it
//                commandButton.sessionCommand?.let { availableSessionCommands.add(it) }
//            }

            val ret = MediaSession.ConnectionResult.accept(
                availableSessionCommands.build(),
                connectionResult.availablePlayerCommands
            )

            checkPlaybackPosition(currentPlayer)
            return ret
        }


        override fun onPostConnect(session: MediaSession, controller: MediaSession.ControllerInfo) {
//            if (!customLayout.isEmpty() && controller.controllerVersion != 0) {
//                // Let Media3 controller (for instance the MediaNotificationProvider) know about the custom
//                // layout right after it connected.
//                ignoreFuture(mediaLibrarySession.setCustomLayout(controller, customLayout))
//            }
        }

        override fun onCustomCommand(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            customCommand: SessionCommand,
            args: Bundle
        ): ListenableFuture<SessionResult> {
            if (CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_ON == customCommand.customAction) {
                // Enable shuffling.
                currentPlayer.shuffleModeEnabled = true
                // Change the custom layout to contain the `Disable shuffling` command.
                //customLayout = ImmutableList.of(customCommands[1])
                // Send the updated custom layout to controllers.
                //session.setCustomLayout(customLayout)
            } else if (CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_OFF == customCommand.customAction) {
                // Disable shuffling.
                currentPlayer.shuffleModeEnabled = false
                // Change the custom layout to contain the `Enable shuffling` command.
                //customLayout = ImmutableList.of(customCommands[0])
                // Send the updated custom layout to controllers.
                //session.setCustomLayout(customLayout)
            }
            return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
        }

        override fun onGetLibraryRoot(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<MediaItem>> {
            val rootItem = MediaItemTree.getRootItem()
            return Futures.immediateFuture(LibraryResult.ofItem(rootItem, params))
        }

        override fun onGetItem(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            mediaId: String
        ): ListenableFuture<LibraryResult<MediaItem>> {
            val item = MediaItemTree.getItem(mediaId)
                    ?: return Futures.immediateFuture(
                        LibraryResult.ofError(SessionError.ERROR_BAD_VALUE)
                    )
            return Futures.immediateFuture(LibraryResult.ofItem(item, null))
        }

        override fun onSubscribe(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            parentId: String,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<Void>> {
            val children =
                MediaItemTree.getChildren(parentId)
                    ?: return Futures.immediateFuture(
                        LibraryResult.ofError(SessionError.ERROR_BAD_VALUE)
                    )
            session.notifyChildrenChanged(browser, parentId, children.size, params)
            return Futures.immediateFuture(LibraryResult.ofVoid())
        }

        override fun onGetChildren(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            parentId: String,
            page: Int,
            pageSize: Int,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
            val children =
                MediaItemTree.getChildren(parentId)
                    ?: return Futures.immediateFuture(
                        LibraryResult.ofError(SessionError.ERROR_BAD_VALUE)
                    )
            val childrenSubset = children.drop(page * pageSize).take(pageSize)
            return Futures.immediateFuture(LibraryResult.ofItemList(childrenSubset, params))
        }

        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: List<MediaItem>
        ): ListenableFuture<List<MediaItem>> {
            val updatedMediaItems: List<MediaItem> =
                mediaItems.map { mediaItem ->
                    if (mediaItem.requestMetadata.searchQuery != null)
                        getMediaItemFromSearchQuery(mediaItem.requestMetadata.searchQuery!!)
                    else MediaItemTree.getItem(mediaItem.mediaId) ?: mediaItem
                }
            return Futures.immediateFuture(updatedMediaItems)
        }

        private fun getMediaItemFromSearchQuery(query: String): MediaItem {
            // Only accept query with pattern "play [Title]" or "[Title]"
            // Where [Title]: must be exactly matched
            // If no media with exact name found, play a random media instead
            val mediaTitle =
                if (query.startsWith("play ", ignoreCase = true)) {
                    query.drop(5)
                } else {
                    query
                }

            return MediaItemTree.getItemFromTitle(mediaTitle) ?: MediaItemTree.getRandomItem()
        }
    }
}


//private fun ignoreFuture(customLayout: ListenableFuture<SessionResult>) {
//    /* Do nothing. */
//}


private const val TAG = "PlayService"