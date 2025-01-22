package com.bdlepla.android.mymusicplayer

import android.app.Application
import android.content.ComponentName
import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import com.bdlepla.android.mymusicplayer.business.AlbumInfo
import com.bdlepla.android.mymusicplayer.business.ArtistInfo
import com.bdlepla.android.mymusicplayer.business.CurrentPlayingStats
import com.bdlepla.android.mymusicplayer.business.PlaylistInfo
import com.bdlepla.android.mymusicplayer.business.PlaylistManager
import com.bdlepla.android.mymusicplayer.business.SongInfo
import com.bdlepla.android.mymusicplayer.business.albumId
import com.bdlepla.android.mymusicplayer.business.albumName
import com.bdlepla.android.mymusicplayer.business.artistId
import com.bdlepla.android.mymusicplayer.business.artistName
import com.bdlepla.android.mymusicplayer.extensions.any
import com.bdlepla.android.mymusicplayer.extensions.forSorting
import com.bdlepla.android.mymusicplayer.extensions.random
import com.bdlepla.android.mymusicplayer.repository.ALBUM_ID
import com.bdlepla.android.mymusicplayer.repository.ARTIST_ID
import com.bdlepla.android.mymusicplayer.repository.ITEM_ID
import com.bdlepla.android.mymusicplayer.service.PlayService
import com.danrusu.pods4k.immutableArrays.ImmutableArray
import com.danrusu.pods4k.immutableArrays.asList
import com.danrusu.pods4k.immutableArrays.emptyImmutableArray
import com.danrusu.pods4k.immutableArrays.indexOf
import com.danrusu.pods4k.immutableArrays.multiplicativeSpecializations.map
import com.danrusu.pods4k.immutableArrays.toImmutableArray
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastStateListener
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyMusicViewModel
@Inject constructor(application: Application): AndroidViewModel(application) {
    private val playerListener = PlayerListener()
    private val castStateListener = PlayerCastStateListener()
    private lateinit var browserFuture: ListenableFuture<MediaBrowser>
    val browser: MediaBrowser?
        get() = if (browserFuture.isDone) browserFuture.get() else null
    private val playlistManager:PlaylistManager = PlaylistManager(application)

    // Initialize the Cast context. This is required so that the media route button can be
    // created in the AppBar
    private val castContext:CastContext = CastContext.getSharedInstance(application)
        .also { it.addCastStateListener(castStateListener) }

   init {
       initializeBrowser(application.applicationContext)
   }

    companion object {
        private const val POSITION_UPDATE_INTERVAL_MILLIS = 100L
    }

    override fun onCleared() {
        super.onCleared()
        castContext.removeCastStateListener(castStateListener)
        browser?.removeListener(playerListener)
        browser?.release()
    }

    private fun initializeBrowser(context: Context) {
        val componentName = ComponentName(context, PlayService::class.java)
        val sessionToken = SessionToken(context, componentName)
        browserFuture = MediaBrowser.Builder(context, sessionToken).buildAsync()
        browserFuture.addListener({ setBrowser(context) }, MoreExecutors.directExecutor())
    }

    private fun setBrowser(context: Context) {
        val b = browser ?: return
        b.addListener(playerListener)
        checkPlaybackPositionAsync(b)
        viewModelScope.launch { loadSongs(b, context) }
    }

    private fun checkPlaybackPositionAsync(browser:MediaBrowser) =
        viewModelScope.launch {
            while (isActive) {
                val currPositionInMs = browser.currentPosition
                val currPosition = currPositionInMs.toInt() / 1000
                val maxPosition = browser.duration.toInt() / 1000
                _currentlyPlayingStats.value = CurrentPlayingStats(_currentlyPlaying,
                    currPosition, maxPosition)
                delay(POSITION_UPDATE_INTERVAL_MILLIS)
            }
        }

    private fun loadSongs(browser: MediaBrowser, context: Context) {
        fun doLoadSongs(browser: MediaBrowser, page:Int, pageSize:Int) {
            val childrenFuture = browser.getChildren(ITEM_ID, page, pageSize, null)

            childrenFuture.addListener( {
                    val childrenResult = childrenFuture.get()
                    val children = childrenResult.value!!
                    if (children.size > 0) {
                        val songs = children.map { SongInfo(it) }
                        addSongs(songs)
                        doLoadSongs(browser, page+1, pageSize)
                    }
                    else { loadAlbums(browser, context) }
                }, ContextCompat.getMainExecutor(context))
        }
        doLoadSongs(browser, 0, 300)
    }

    private fun loadAlbums(browser: MediaBrowser, context: Context) {
        fun doLoadAlbums(browser: MediaBrowser, page:Int, pageSize:Int) {
            val childrenFuture = browser.getChildren(ALBUM_ID, page, pageSize, null)

            childrenFuture.addListener( {
                val childrenResult = childrenFuture.get()
                val children = childrenResult.value!!.toImmutableArray() // OK; Do not change
                if (children.any()) {
                    val albums = children.map {
                        val albumName = it.mediaMetadata.albumName
                        val albumYear = it.mediaMetadata.releaseYear ?: 0
                        val albumId = it.mediaMetadata.albumId
                        val artistId = it.mediaMetadata.artistId
                        val albumArt = it.mediaMetadata.artworkUri.toString()
                        AlbumInfo(albumName, albumYear, albumId, artistId, albumArt)
                    }
                    addAlbums(albums)
                    doLoadAlbums(browser, page+1, pageSize)
                }
                else { loadArtists(browser, context) }
            }, ContextCompat.getMainExecutor(context))
        }
        doLoadAlbums(browser, 0, Int.MAX_VALUE)
    }

    private fun loadArtists(browser: MediaBrowser, context: Context) {
        fun doLoadArtists(browser: MediaBrowser, page:Int, pageSize:Int) {
            val childrenFuture = browser.getChildren(ARTIST_ID, page, pageSize, null)

            childrenFuture.addListener( {
                val childrenResult = childrenFuture.get()
                val children = childrenResult.value!!.toImmutableArray(); // OK, do not change
                if (children.any()) {
                    val artists = children.map {
                        val artistName = it.mediaMetadata.artistName
                        val artistId = it.mediaMetadata.artistId
                        val songsForArtist = immutableSongCollection.filter{si -> si.artistId == artistId}
                        val randomSong = songsForArtist.random()
                        ArtistInfo(artistName, artistId, randomSong.albumArt)
                    }
                    addArtists(artists)
                    doLoadArtists(browser, page+1, pageSize)
                }
                else { checkIfServiceIsPlayingAtAppStartup() }
            }, ContextCompat.getMainExecutor(context))
        }
        doLoadArtists(browser, 0, Int.MAX_VALUE)
    }

    private fun checkIfServiceIsPlayingAtAppStartup() {
        val b = browser ?: return
        if (b.isPlaying) {
            val mediaItem = b.currentMediaItem ?: return
            _currentlyPlaying = mediaItem.mediaMetadata.toSongInfo() ?: return
            _isPaused.value = false
        }
        // might need to figure out how to get current playlist at startup from serice
    }

    inner class PlayerCastStateListener: CastStateListener
    {
        override fun onCastStateChanged(castState: Int) {
            _castState.value = castState
        }
    }

    inner class PlayerListener: Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            _isPaused.value = !isPlaying
        }

        override fun onShuffleModeEnabledChanged(shuffleModeEnabled: Boolean) {
            super.onShuffleModeEnabledChanged(shuffleModeEnabled)
            _shuffling.value = shuffleModeEnabled
        }

        override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
            super.onMediaMetadataChanged(mediaMetadata)
            val item = mediaMetadata.toSongInfo() ?: return
            _currentlyPlaying = item
        }

        // for changes in playlist
        private val window = Timeline.Window()
        override fun onTimelineChanged(timeline: Timeline, reason: Int) {
            super.onTimelineChanged(timeline, reason)

            val songInfos =
                (0..<timeline.windowCount).mapNotNull {
                    timeline.getWindow(it, window)
                    window.mediaItem.mediaMetadata.toSongInfo()
                }.distinctBy { it.songId }
            _currentSongList.value = songInfos.toImmutableArray() // OK, do not change
        }
    }

    private fun MediaMetadata.toSongInfo(): SongInfo? =
        songCollection.firstOrNull { si -> si.artist == artist && si.title == title }

    private val _castState = MutableStateFlow(0)
    val castState: StateFlow<Int>
        get() = _castState.asStateFlow()

    private val _allSongs = MutableStateFlow<ImmutableArray<SongInfo>>(emptyImmutableArray())
    val allSongs : StateFlow<ImmutableArray<SongInfo>>
        get() = _allSongs.asStateFlow()

    private val _allArtists = MutableStateFlow<ImmutableArray<ArtistInfo>>(emptyImmutableArray())
    val allArtists : StateFlow<ImmutableArray<ArtistInfo>>
        get() = _allArtists.asStateFlow()

    private val _allAlbums = MutableStateFlow<ImmutableArray<AlbumInfo>>(emptyImmutableArray())
    val allAlbums : StateFlow<ImmutableArray<AlbumInfo>>
        get() = _allAlbums.asStateFlow()

    private val _allPlaylists = MutableStateFlow<ImmutableArray<PlaylistInfo>>(emptyImmutableArray())
    val allPlaylists : StateFlow<ImmutableArray<PlaylistInfo>>
        get() = _allPlaylists.asStateFlow()

    //region Currently Playing - the song currently playing
    private var _currentlyPlaying:SongInfo? = null

    private val _currentlyPlayingStats = MutableStateFlow<CurrentPlayingStats?>(null)
    val currentlyPlayingStats : StateFlow<CurrentPlayingStats?>
        get() = _currentlyPlayingStats.asStateFlow()

    private val _shuffling = MutableStateFlow(false)
//    val isShuffling: StateFlow<Boolean>
//        get() = _shuffling.asStateFlow()

    private val  _currentSongList = MutableStateFlow<ImmutableArray<SongInfo>>(emptyImmutableArray())
    val currentSongList : StateFlow<ImmutableArray<SongInfo>>
        get() = _currentSongList.asStateFlow()

    fun setCurrentlyPlaying(songInfo: SongInfo) {
        val c = _currentSongList.value
        val idx = c.indexOf(songInfo)
        if (idx == -1) return
        val b = browser ?: return
        b.seekTo(idx, 0)
        b.play()
    }

    fun setPlaylist(songs: ImmutableArray<SongInfo>) {
        val b = browser ?: return
        b.setMediaItems(songs.map { it.toMediaItem() }.asList())
        b.prepare()
    }

    fun playNext() {
        val b = browser ?: return
        if (b.hasNextMediaItem()) { b.seekToNextMediaItem() }
    }

    //region Is Paused - true if not playing; false if playing
    private val _isPaused = MutableStateFlow(false)
    val isPaused : StateFlow<Boolean>
        get() = _isPaused.asStateFlow()

    fun togglePlayPause() {
        val b = browser ?: return
        _isPaused.value = !_isPaused.value
        if (_currentlyPlaying == null) return
        if (_isPaused.value) { b.pause() }
        else { b.play() }
    }

    fun toggleRepeat() {
        val b = browser ?: return
        b.repeatMode = Player.REPEAT_MODE_ALL
    }

    private val songCollection = mutableListOf<SongInfo>()

    private val immutableSongCollection
        get() = songCollection.toImmutableArray() // OK; Do not change

    private fun addSongs(songs:Iterable<SongInfo>) {
        songCollection.addAll(songs)
        _allPlaylists.value = playlistManager.updatePlaylistInfo(immutableSongCollection)
        _allSongs.value =  immutableSongCollection.sortedBy { it.title.forSorting() }
    }

    private fun addArtists(artists:ImmutableArray<ArtistInfo>) {
        artists.distinctBy { it.artistId }
            .forEach { artist ->
                val artistAlbums = _allAlbums.value.filter { album -> album.artistId == artist.artistId }
                artist.addAlbums(artistAlbums)
            }
        _allArtists.value = artists.sortedBy { it.name.forSorting() }
    }

    private fun addAlbums(albums:ImmutableArray<AlbumInfo>) {
        albums.forEach{album ->
            val songs = immutableSongCollection
                .filter { it.albumId == album.albumId }
                .sortedBy { it.trackNumber }
            album.addSongs(songs)
        }
        _allAlbums.value = albums.sortedBy { it.name.forSorting() }
    }

    fun addSongsToPlaylist(playListInfo: PlaylistInfo, songs: ImmutableArray<SongInfo>) {
        playlistManager.addSongsToPlaylist(playListInfo, songs)
        _allPlaylists.value = playlistManager.updatePlaylistInfo(immutableSongCollection)
    }

    fun removePlaylist(playListInfo: PlaylistInfo) {
        playlistManager.removePlaylist(playListInfo)
        _allPlaylists.value = playlistManager.updatePlaylistInfo(immutableSongCollection)
    }

    fun addNewPlaylist(it: String) {
        playlistManager.addNewPlaylist(it)
        _allPlaylists.value = playlistManager.updatePlaylistInfo(immutableSongCollection)
    }

    fun removeSongFromPlaylist(playListInfo: PlaylistInfo, songInfo: SongInfo) {
        playlistManager.removeSongFromPlaylist(playListInfo, songInfo)
        _allPlaylists.value = playlistManager.updatePlaylistInfo(immutableSongCollection)
    }
}
