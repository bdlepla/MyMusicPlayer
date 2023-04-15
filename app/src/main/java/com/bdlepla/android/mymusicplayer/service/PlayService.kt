package com.bdlepla.android.mymusicplayer.service

import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.app.TaskStackBuilder
import androidx.media3.common.*
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.*
import com.bdlepla.android.mymusicplayer.MainActivity
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture


class PlayService: MediaLibraryService() {
    private lateinit var player: Player
    private lateinit var mediaLibrarySession: MediaLibrarySession

    private val librarySessionCallback = CustomMediaLibrarySessionCallback()
    private val playerListener = PlayerListener()

    private val uAmpAudioAttributes = AudioAttributes.Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)
        .build()

    private val exoPlayer: ExoPlayer by lazy {
        ExoPlayer.Builder(this).build().apply {
            setAudioAttributes(uAmpAudioAttributes, true)
            setHandleAudioBecomingNoisy(true)
        }
    }

    private fun initializeSessionAndPlayer() {
        MediaItemTree.initialize(applicationContext)

        val parentScreenIntent = Intent(this, MainActivity::class.java)

        //val intent = Intent(this, PlayerActivity::class.java)

        val pendingIntent = TaskStackBuilder.create(this).run {
            addNextIntent(parentScreenIntent)
            //addNextIntent(intent)

            val immutableFlag = if (Build.VERSION.SDK_INT >= 23) FLAG_IMMUTABLE else 0
            getPendingIntent(0, immutableFlag or FLAG_UPDATE_CURRENT)
        }

        player = exoPlayer
        player.addListener(playerListener)
        mediaLibrarySession =
            MediaLibrarySession.Builder(this, player, librarySessionCallback).build()
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
        //setupCustomCommands()
        initializeSessionAndPlayer()
    }

    override fun onDestroy() {
        mediaLibrarySession.release()
        player.release()
        super.onDestroy()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession {
        return mediaLibrarySession
    }

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
        private const val CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_ON =
            "android.media3.session.demo.SHUFFLE_ON"
        private const val CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_OFF =
            "android.media3.session.demo.SHUFFLE_OFF"
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
//                // Add custom command to available session commands.
//                // these are cached to be used as a lookup when the user presses it
//                commandButton.sessionCommand?.let { availableSessionCommands.add(it) }
//            }
            return MediaSession.ConnectionResult.accept(
                availableSessionCommands.build(),
                connectionResult.availablePlayerCommands
            )
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
                player.shuffleModeEnabled = true
                // Change the custom layout to contain the `Disable shuffling` command.
                //customLayout = ImmutableList.of(customCommands[1])
                // Send the updated custom layout to controllers.
                //session.setCustomLayout(customLayout)
            } else if (CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_OFF == customCommand.customAction) {
                // Disable shuffling.
                player.shuffleModeEnabled = false
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
                        LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
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
                        LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
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
                        LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
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


