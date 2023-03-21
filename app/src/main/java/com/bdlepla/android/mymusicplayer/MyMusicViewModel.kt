package com.bdlepla.android.mymusicplayer

import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaBrowser
import androidx.media3.session.SessionToken
import com.bdlepla.android.mymusicplayer.extensions.forSorting
import com.bdlepla.android.mymusicplayer.business.*
import com.bdlepla.android.mymusicplayer.repository.ALBUM_ID
import com.bdlepla.android.mymusicplayer.repository.ARTIST_ID
import com.bdlepla.android.mymusicplayer.repository.ITEM_ID
import com.bdlepla.android.mymusicplayer.service.PlayService
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MyMusicViewModel
@Inject constructor(application: Application): AndroidViewModel(application) {
    private val playerListener = PlayerListener()
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var browserFuture: ListenableFuture<MediaBrowser>
    val browser: MediaBrowser?
        get() = if (browserFuture.isDone) browserFuture.get() else null
    private val playlistManager:PlaylistManager = PlaylistManager(application)
    @Suppress("PrivatePropertyName")
    private val POSITION_UPDATE_INTERVAL_MILLIS = 100L
   init {
        initializeBrowser(application.applicationContext)
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
        loadSongs(b, context)
        checkPlaybackPosition(b)
    }

    private fun checkPlaybackPosition(browser: MediaBrowser): Boolean = handler.postDelayed({
        val currPosition = browser.currentPosition.toInt() / 1000
        val maxPosition = browser.duration.toInt() / 1000
        _currentlyPlayingStats.value =  CurrentPlayingStats(
            _currentlyPlaying,
            currPosition,
            maxPosition
        )
        checkPlaybackPosition(browser)
    }, POSITION_UPDATE_INTERVAL_MILLIS)

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
                else {
                        loadAlbums(browser, context)
                    }
                }, ContextCompat.getMainExecutor(context))
        }
        doLoadSongs(browser, 0, 300)
    }

    private fun loadAlbums(browser: MediaBrowser, context: Context) {
        fun doLoadAlbums(browser: MediaBrowser, page:Int, pageSize:Int) {
            val childrenFuture = browser.getChildren(ALBUM_ID, page, pageSize, null)

            childrenFuture.addListener( {
                val childrenResult = childrenFuture.get()
                val children = childrenResult.value!!
                if (children.size > 0) {
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
                else {
                    loadArtists(browser, context)
                }
            }, ContextCompat.getMainExecutor(context))
        }
        doLoadAlbums(browser, 0, Int.MAX_VALUE)
    }

    private fun loadArtists(browser: MediaBrowser, context: Context) {
        fun doLoadArtists(browser: MediaBrowser, page:Int, pageSize:Int) {
            val childrenFuture = browser.getChildren(ARTIST_ID, page, pageSize, null)

            childrenFuture.addListener( {
                val childrenResult = childrenFuture.get()
                val children = childrenResult.value!!
                if (children.size > 0) {
                    val artists = children.map {
                        val artistName = it.mediaMetadata.artistName
                        val artistId = it.mediaMetadata.artistId
                        val songsForArtist = songCollection.filter{si -> si.artistId == artistId}
                        val randomSong = songsForArtist.random()
                        val albumForRandomSong = allAlbums.value.find{ai->ai.albumId == randomSong.albumId}
                        ArtistInfo(artistName, artistId, albumForRandomSong)
                    }
                    addArtists(artists)
                    doLoadArtists(browser, page+1, pageSize)
                }
            }, ContextCompat.getMainExecutor(context))
        }
        doLoadArtists(browser, 0, Int.MAX_VALUE)
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
            //val b = browser ?: return
        }
    }

    private fun MediaMetadata.toSongInfo(): SongInfo? =
        songCollection.firstOrNull { si ->
            si.artist == this.artist && si.title == this.title }

    private val _allSongs = MutableStateFlow<List<SongInfo>>(emptyList())
    val allSongs : StateFlow<List<SongInfo>>
        get() = _allSongs.asStateFlow()

    private val _allArtists = MutableStateFlow<List<ArtistInfo>>(emptyList())
    val allArtists : StateFlow<List<ArtistInfo>>
        get() = _allArtists.asStateFlow()

    private val _allAlbums = MutableStateFlow<List<AlbumInfo>>(emptyList())
    val allAlbums : StateFlow<List<AlbumInfo>>
        get() = _allAlbums.asStateFlow()

    private val _allPlaylists = MutableStateFlow<List<PlaylistInfo>>(emptyList())
    val allPlaylists : StateFlow<List<PlaylistInfo>>
        get() = _allPlaylists.asStateFlow()

    //region Currently Playing - the song currently playing
    private var _currentlyPlaying:SongInfo? = null

    private val _currentlyPlayingStats = MutableStateFlow<CurrentPlayingStats?>(null)
    val currentlyPlayingStats : StateFlow<CurrentPlayingStats?>
        get() = _currentlyPlayingStats.asStateFlow()

    private val _shuffling = MutableStateFlow(false)
//    val isShuffling: StateFlow<Boolean>
//        get() = _shuffling.asStateFlow()



    fun setCurrentlyPlaying(songInfo: SongInfo) {
        val b = browser ?: return
        val c = playlistManager.currentPlaylist ?: return
        val idx = c.indexOf(songInfo)
        if (idx == -1) return
        b.seekTo(idx, 0)
        b.play()
    }

    fun setPlaylist(songs: List<SongInfo>) {
        val b = browser ?: return
        val mediaItems = songs.map{ it.toMediaItem() }
        playlistManager.setPlaylist(songs)
        b.setMediaItems(mediaItems)
        b.prepare()
    }

    fun playNext() {
        val b = browser ?: return
        if (!b.hasNextMediaItem()) return
        b.seekToNextMediaItem()
    }

    //region Is Paused - true if not playing; false if playing
    private val _isPaused = MutableStateFlow(false)
    val isPaused : StateFlow<Boolean>
        get() = _isPaused.asStateFlow()

    fun togglePlayPause() {
        val b = browser ?: return
        _isPaused.value = !_isPaused.value
        if (_currentlyPlaying == null) return
        if (_isPaused.value) {b.pause()}
        else {b.play()}
    }

    fun play() {
        val b = browser ?: return
        _isPaused.value = false
        b.play()
    }

    fun toggleRepeat() {
        val b = browser ?: return
        b.repeatMode = Player.REPEAT_MODE_ALL
    }

    private val songCollection = mutableListOf<SongInfo>()

    private fun addSongs(songs:List<SongInfo>) {
        songCollection.addAll(songs)
        _allPlaylists.value = playlistManager.updatePlaylistInfo(songCollection)
        _allSongs.value =  songCollection.sortedBy { it.title.forSorting() }
    }

    private fun addArtists(artists:List<ArtistInfo>) {
        _allArtists.value = artists.sortedBy { it.name.forSorting() }
    }

    private fun addAlbums(albums:List<AlbumInfo>) {
        _allAlbums.value = albums.sortedBy { it.name.forSorting() }
    }

    fun addSongsToPlaylist(playListInfo: PlaylistInfo, songs: List<SongInfo>) {
        playlistManager.addSongsToPlaylist(playListInfo, songs)
        _allPlaylists.value = playlistManager.updatePlaylistInfo(songCollection)
    }

    fun removePlaylist(playListInfo: PlaylistInfo) {
        playlistManager.removePlaylist(playListInfo)
        _allPlaylists.value = playlistManager.updatePlaylistInfo(songCollection)
    }

    fun addNewPlaylist(it: String) {
        playlistManager.addNewPlaylist(it)
        _allPlaylists.value = playlistManager.updatePlaylistInfo(songCollection)
    }

    fun removeSongFromPlaylist(playListInfo: PlaylistInfo, songInfo: SongInfo) {
        playlistManager.removeSongFromPlaylist(playListInfo, songInfo)
        _allPlaylists.value = playlistManager.updatePlaylistInfo(songCollection)
    }
}
