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
import androidx.media3.session.CommandButton
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionCommand
import androidx.media3.session.SessionError
import androidx.media3.session.SessionResult
import com.bdlepla.android.mymusicplayer.MyMusicPlayerSettings
import com.bdlepla.android.mymusicplayer.business.PlaylistInfo
import com.bdlepla.android.mymusicplayer.business.SongInfo
import com.bdlepla.android.mymusicplayer.datastore.MyMusicPlayerSettingsDataStore
import com.bdlepla.android.mymusicplayer.repository.ITEM_ID
import com.danrusu.pods4k.immutableArrays.asList
import com.danrusu.pods4k.immutableArrays.emptyImmutableArray
import com.danrusu.pods4k.immutableArrays.toImmutableArray
import com.google.android.gms.cast.framework.CastContext
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.math.abs


class PlayService: MediaLibraryService() {
    private lateinit var mediaLibrarySession: MediaLibrarySession
    private var isReleasing = false
    private var customCommands: List<CommandButton> = emptyList()
    private var customLayout: ImmutableList<CommandButton> = ImmutableList.of()

    private val librarySessionCallback = CustomMediaLibrarySessionCallback()
    private val playerListener = PlayerListener()
    private val musicDataStore: MyMusicPlayerSettingsDataStore by lazy { MyMusicPlayerSettingsDataStore(this) }
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var httpServer: LocalHttpServer? = null
    private val serverPort = 8080
    private val handler:Handler by lazy { Handler(currentPlayer.applicationLooper) }
    private val myPlayerAudioAttributesBuilder = AudioAttributes.Builder()
        .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
        .setUsage(C.USAGE_MEDIA)

    private val queueManager by lazy {
        QueueManager {
            val allSongs = MediaItemTree.getChildren(ITEM_ID) ?: emptyImmutableArray()
            val songInfos = allSongs.asList().map { SongInfo(it) }.shuffled().toImmutableArray()
            PlaylistInfo("Default Playlist", songInfos)
        }
    }

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
        ReplaceableForwardingPlayer(this, exoPlayer)
    }

    private fun initializeMediaSession() {
        setupCustomCommands()
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


    private fun setupCustomCommands() {
        customCommands =
            listOf(
                getShuffleCommandButton(
                    SessionCommand(CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_ON, Bundle.EMPTY)
                ),
                getShuffleCommandButton(
                    SessionCommand(CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_OFF, Bundle.EMPTY)
                )
            )
        customLayout = ImmutableList.of(customCommands[0])
    }

    private fun getShuffleCommandButton(sessionCommand: SessionCommand): CommandButton {
        val isOn = sessionCommand.customAction == CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_ON
        return CommandButton.Builder()
            .setDisplayName(if (isOn) getShuffleOnDescription() else getShuffleOffDescription())
            .setSessionCommand(sessionCommand)
            .setIconResId(com.bdlepla.android.mymusicplayer.R.drawable.ic_shuffle)
            .build()
    }

    private fun getShuffleOnDescription(): String {
        return "Shuffle on"
    }

    private fun getShuffleOffDescription(): String {
        return "Shuffle off"
    }


    override fun onCreate() {
        super.onCreate()
        MediaItemTree.initialize(this)
        loadSavedQueue()
        startServer()
        if (castPlayer?.isCastSessionAvailable == true) {
            currentPlayer.setNewPlayer(castPlayer!!)
        }
        initializeMediaSession()
    }

    private fun loadSavedQueue() {
        val savedData = musicDataStore.settings
        if (savedData.songIdsCount > 0) {
            val songs = savedData.songIdsList.mapNotNull { songId ->
                MediaItemTree.getItem(MediaItemTree.ITEM_PREFIX + songId.toString())?.let { SongInfo(it) }
            }
            if (songs.isNotEmpty()) {
                queueManager.setSongs(
                    songs,
                    savedData.currentIndex.toInt(),
                    savedData.currentPosition
                )
            }
        }
    }

    override fun onDestroy() {
        isReleasing = true
        stopServer()
        handler.removeCallbacksAndMessages(null)
        scope.cancel()
        releaseMediaSession()
        super.onDestroy()
    }

    private fun releaseMediaSession() {
        if (::mediaLibrarySession.isInitialized) {
            mediaLibrarySession.release()
        }
        exoPlayer.removeListener(playerListener)
        castPlayer?.removeListener(playerListener)
        try {
            currentPlayer.release()
        } catch (e: Exception) {
            Log.e("PlayService", "Error releasing player", e)
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession = mediaLibrarySession

    private var lastSavedPosition = -1L
    private var lastSavedIndex = -1

    private fun checkPlaybackPosition(player: Player): Boolean {
        if (isReleasing) return false
        return handler.postDelayed({
            if (isReleasing) return@postDelayed
            if (player.isPlaying) {
                val currPositionInMs = player.currentPosition
                val currIndex = player.currentMediaItemIndex
                val currentlyPlaying = player.currentMediaItem
                
                if (currentlyPlaying != null) {
                    val indexChanged = currIndex != lastSavedIndex
                    val positionMovedSignificantly = abs(currPositionInMs - lastSavedPosition) > 2000
                    
                    if (indexChanged || positionMovedSignificantly) {
                        queueManager.updateCurrentState(currIndex, currPositionInMs)
                        saveQueue()
                        lastSavedIndex = currIndex
                        lastSavedPosition = currPositionInMs
                    }
                }
            }
            checkPlaybackPosition(player)
        }, POSITION_UPDATE_INTERVAL_MILLIS)
    }

    fun populateMediaOnConnectIfNotPlaying(player:Player) {
        if (player.isPlaying) return
        if (!queueManager.isEmpty) {
            loadQueuedPlaylist(queueManager)
            return
        }
    }

    private fun loadQueuedPlaylist(queueManager:QueueManager) {
        val mediaItems = queueManager.getSongs().map { it.toMediaItem() }
        currentPlayer.setMediaItems(mediaItems, queueManager.currentIndex, queueManager.currentPosition)
        currentPlayer.prepare()
        currentPlayer.play()
        saveQueue()
    }

    private fun saveQueue() {
        val settings = MyMusicPlayerSettings.newBuilder()
            .setCurrentIndex(queueManager.currentIndex.toLong())
            .setCurrentPosition(queueManager.currentPosition)
            .addAllSongIds(queueManager.getSongs().map { it.songId })
            .build()
        scope.launch {
            musicDataStore.saveSettings(settings)
        }
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

    private fun stopServer() {
        httpServer?.stop()
        httpServer = null
    }

    private fun startServer() {
        scope.launch {
            try {
                httpServer = LocalHttpServer(serverPort)
                httpServer?.start()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to start HTTP server", e)
            }
        }
    }

    companion object {
        //private const val SEARCH_QUERY_PREFIX_COMPAT = "androidx://media3-session/playFromSearch"
        //private const val SEARCH_QUERY_PREFIX = "androidx://media3-session/setMediaUri"
        private const val TAG = "PlayService"

        private const val CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_ON = "android.media3.session.demo.SHUFFLE_ON"
        private const val CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_OFF = "android.media3.session.demo.SHUFFLE_OFF"
        const val CUSTOM_COMMAND_PUSH_IMMEDIATELY = "PUSH_IMMEDIATELY"
        const val CUSTOM_COMMAND_PLAY_AFTER_PLAYLIST = "PLAY_AFTER_PLAYLIST"
        const val CUSTOM_COMMAND_PLAY_AFTER_SONG = "PLAY_AFTER_SONG"
        private const val POSITION_UPDATE_INTERVAL_MILLIS = 500L
    }

    private inner class CastSessionAvailabilityListener : SessionAvailabilityListener {

        /**
         * Called when a Cast session has started and the user wishes to control playback on a
         * remote Cast receiver rather than play audio locally.
         */
        override fun onCastSessionAvailable() {
            castPlayer?.let { currentPlayer.setNewPlayer(it) }
        }

        /**
         * Called when a Cast session has ended and the user wishes to control playback locally.
         */
        override fun onCastSessionUnavailable() {
            currentPlayer.setNewPlayer(exoPlayer)
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

//       override fun onPlayerErrorChanged(error: PlaybackException?) {
//           if (error != null) {
//               super.onPlayerError(error)
//                if (currentPlayer.isCommandAvailable(COMMAND_SEEK_TO_NEXT)) {
//                    currentPlayer.seekToNext()
//                    currentPlayer.play()
//                }
//           }
//       }

       override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
           super.onMediaItemTransition(mediaItem, reason)
           if (reason != Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED) {
               queueManager.updateCurrentState(currentPlayer.currentMediaItemIndex, currentPlayer.currentPosition)
               saveQueue()
           }
       }

       override fun onPlaybackStateChanged(playbackState: Int) {
           super.onPlaybackStateChanged(playbackState)
           if (playbackState == Player.STATE_ENDED) {
               queueManager.setSongs(emptyList(), 0, 0)
               loadQueuedPlaylist(queueManager)
           }
       }

       override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
           super.onShuffleModeEnabledChanged(shuffleModeEnabled)
           customLayout = ImmutableList.of(if (shuffleModeEnabled) customCommands[1] else customCommands[0])
           mediaLibrarySession.setCustomLayout(customLayout)
       }
    }

    private inner class CustomMediaLibrarySessionCallback : MediaLibrarySession.Callback {
        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {
            val connectionResult = super.onConnect(session, controller)

            val availableSessionCommands = connectionResult.availableSessionCommands.buildUpon()
            availableSessionCommands.add(SessionCommand(CUSTOM_COMMAND_PUSH_IMMEDIATELY, Bundle.EMPTY))
            availableSessionCommands.add(SessionCommand(CUSTOM_COMMAND_PLAY_AFTER_PLAYLIST, Bundle.EMPTY))
            availableSessionCommands.add(SessionCommand(CUSTOM_COMMAND_PLAY_AFTER_SONG, Bundle.EMPTY))
            availableSessionCommands.add(SessionCommand(CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_ON, Bundle.EMPTY))
            availableSessionCommands.add(SessionCommand(CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_OFF, Bundle.EMPTY))

            val ret = MediaSession.ConnectionResult.accept(
                availableSessionCommands.build(),
                connectionResult.availablePlayerCommands
            )

            populateMediaOnConnectIfNotPlaying(currentPlayer)
            checkPlaybackPosition(currentPlayer)
            return ret
        }

        override fun onPostConnect(session: MediaSession, controller: MediaSession.ControllerInfo) {
            if (customLayout.isNotEmpty() && controller.controllerVersion != 0) {
                // Let Media3 controller (for instance the MediaNotificationProvider) know about the custom
                // layout right after it connected.
                mediaLibrarySession.setCustomLayout(controller, customLayout)
            }
        }

        override fun onCustomCommand(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            customCommand: SessionCommand,
            args: Bundle
        ): ListenableFuture<SessionResult> {
            when (customCommand.customAction) {
                CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_ON -> {
                    // Enable shuffling.
                    currentPlayer.shuffleModeEnabled = true
                }
                CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_OFF -> {
                    // Disable shuffling.
                    currentPlayer.shuffleModeEnabled = false
                }
                CUSTOM_COMMAND_PUSH_IMMEDIATELY -> {
                    val playlist = args.getPlaylistInfo()
                    queueManager.pushImmediately(playlist)
                    saveQueue()
                    loadQueuedPlaylist(queueManager)
                }
                CUSTOM_COMMAND_PLAY_AFTER_PLAYLIST -> {
                    val playlist = args.getPlaylistInfo()
                    queueManager.playAfterCurrentPlaylist(playlist)
                    saveQueue()
                    loadQueuedPlaylist(queueManager)
                }
                CUSTOM_COMMAND_PLAY_AFTER_SONG -> {
                    val playlist = args.getPlaylistInfo()
                    queueManager.playAfterCurrentSong(playlist)
                    saveQueue()
                    loadQueuedPlaylist(queueManager)
                }
            }
            return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
        }

        private fun Bundle.getPlaylistInfo(): PlaylistInfo {
            val name = getString("name") ?: ""
            val mediaIds = getStringArray("mediaIds") ?: emptyArray()
            val songs = mediaIds.mapNotNull { mediaId ->
                MediaItemTree.getItem(mediaId)?.let { SongInfo(it) }
            }.toImmutableArray()
            return PlaylistInfo(name, songs)
        }

        override fun onSearch(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            query: String,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<Void>> {
            val items = MediaItemTree.search(query)
            session.notifySearchResultChanged(browser, query, items.size, params)
            return Futures.immediateFuture(LibraryResult.ofVoid())
        }

        override fun onGetSearchResult(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            query: String,
            page: Int,
            pageSize: Int,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
            val items = MediaItemTree.search(query)
            val start = (page * pageSize).coerceAtMost(items.size)
            val end = (start + pageSize).coerceAtMost(items.size)
            val itemsSubset = ImmutableList.copyOf(items.subList(start, end))
            return Futures.immediateFuture(LibraryResult.ofItemList(itemsSubset, params))
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
            Log.d(TAG, "onGetItem: $mediaId")
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
            val childrenSubset = children.drop(page * pageSize).take(pageSize).asList()
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


