package com.bdlepla.android.mymusicplayer

import android.app.Application
import android.content.ComponentName
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.bdlepla.android.mymusicplayer.Extensions.toSongInfo
import com.bdlepla.android.mymusicplayer.business.AlbumInfo
import com.bdlepla.android.mymusicplayer.business.ArtistInfo
import com.bdlepla.android.mymusicplayer.business.SongInfo
import com.bdlepla.android.mymusicplayer.business.isDifferentFrom
import com.bdlepla.android.mymusicplayer.repository.Repository
import com.bdlepla.android.mymusicplayer.ui.PlayService
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class MyMusicViewModel
@Inject constructor(application: Application): AndroidViewModel(application), Player.Listener {

    private var controllerFuture: ListenableFuture<MediaController>
    private val mediaController: MediaController?
        get() = if (controllerFuture.isDone) controllerFuture.get() else null

    init {
        val context = getApplication<MyMusicApplication>().applicationContext
        val componentName = ComponentName(context, PlayService::class.java)
        val sessionToken = SessionToken(context, componentName)
        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture.addListener(::setupControllerListeners, MoreExecutors.directExecutor())
    }

    private fun setupControllerListeners() {
        val controller = mediaController ?: return
        controller.addListener(this)
    }

    private val _allSongs = MutableStateFlow<List<SongInfo>>(emptyList())
    val allSongs : StateFlow<List<SongInfo>>
        get() = _allSongs.asStateFlow()

    private val _allArtists = MutableStateFlow<List<ArtistInfo>>(emptyList())
    val allArtists : StateFlow<List<ArtistInfo>>
        get() = _allArtists.asStateFlow()

    private val _allAlbums = MutableStateFlow<List<AlbumInfo>>(emptyList())
    val allAlbums : StateFlow<List<AlbumInfo>>
        get() = _allAlbums.asStateFlow()

    // Region - Currently Playing -- the song currently playing
    private val _currentlyPlaying = MutableStateFlow<SongInfo?>(null)
    val currentlyPlaying : StateFlow<SongInfo?>
        get() = _currentlyPlaying.asStateFlow()

    fun setCurrentlyPlaying(songInfo: SongInfo) {
        _currentlyPlaying.value = songInfo
        mediaController?.setMediaItem(songInfo.toMediaItem())
        mediaController?.prepare()
        if (!_isPaused.value) mediaController?.play()
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        super.onMediaItemTransition(mediaItem, reason)
        if (Player.MEDIA_ITEM_TRANSITION_REASON_PLAYLIST_CHANGED == reason) {
            if (mediaItem != null ) {
                if (_currentlyPlaying.value.isDifferentFrom(mediaItem)) {
                    _currentlyPlaying.value = mediaItem.toSongInfo()
                }
            }
        }
    }
    // endRegion - Currently Playing


    // Region - Is Paused - true if not playing; false if playing
    private val _isPaused = MutableStateFlow<Boolean>(false)
    val isPaused : StateFlow<Boolean>
        get() = _isPaused.asStateFlow()

    fun togglePlayPause() {
        _isPaused.value = !_isPaused.value
        if (_currentlyPlaying.value != null) {
            if (_isPaused.value) {mediaController?.pause()}
            else {mediaController?.play()}
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        _isPaused.value = !isPlaying
    }
    // end Region - Is Paused

    private var currentAllSongs: List<SongInfo> = emptyList()
    private var songsByArtist:Map<ArtistInfo, List<SongInfo>> = emptyMap()
    private var songsByAlbum:Map<AlbumInfo, List<SongInfo>> = emptyMap()
    private var albumsByArtist:Map<ArtistInfo, List<AlbumInfo>> = emptyMap()

    private val uiDispatcher = Dispatchers.Main
    private val uiScope = CoroutineScope(uiDispatcher)
    private val ioDispatcher = Dispatchers.IO // + mViewModelJob + mHandler

    fun getAllSongsOnDevice() {
        uiScope.launch {
            withContext(ioDispatcher) {
                val songs = getAllSongs()
                withContext(uiDispatcher) {
                    _allSongs.value = songs
                    _allArtists.value = songsByArtist.keys.toList()
                    _allAlbums.value = songsByAlbum.keys.toList()
                }
            }
        }
    }

    private fun getAllSongs():List<SongInfo> {
        val allSongs = Repository.getAllSongs(getApplication())
        currentAllSongs = allSongs.sortedBy{it.title}
        buildData(allSongs)
        return currentAllSongs
    }

    private fun buildData(allSongs:List<SongInfo>) {
        // TODO: take all songs and divide into the following:
        // - songs by artist
        songsByArtist = allSongs
            .groupBy { ArtistInfo(it.artist) }
            .toSortedMap(compareBy{it.name}) // sort by artist name

        // - songs by album
        songsByAlbum = allSongs
            .groupBy { AlbumInfo(it.album, it.albumYear, it.albumArt) }
            .toSortedMap(compareBy{it.name}) // sort by album name

        // - songs by genre

        // - albums by artist
        // TODO: Sort by album year
        albumsByArtist = allSongs
            .groupBy({k -> ArtistInfo(k.artist)},{v->AlbumInfo(v.album, v.albumYear, v.albumArt) })
            .mapValues { kv -> kv.value.distinct().sortedBy{it.albumYear} }
            .toSortedMap(compareBy{it.name}) // sort by artist name

        // - artists by genre
        // - albums by genre
        // - songs by favorite
    }
}