package com.bdlepla.android.mymusicplayer

import android.app.Application
import android.content.ComponentName
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.bdlepla.android.mymusicplayer.Extensions.forSorting
import com.bdlepla.android.mymusicplayer.business.*
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

    private var currentPlayingSongs:List<ISongInfo> = mutableListOf()

    private val _allSongs = MutableStateFlow<List<SongInfo>>(emptyList())
    val allSongs : StateFlow<List<SongInfo>>
        get() = _allSongs.asStateFlow()

    private val _allArtists = MutableStateFlow<List<ArtistInfo>>(emptyList())
    val allArtists : StateFlow<List<ArtistInfo>>
        get() = _allArtists.asStateFlow()

    private val _allAlbums = MutableStateFlow<List<AlbumInfo>>(emptyList())
    val allAlbums : StateFlow<List<AlbumInfo>>
        get() = _allAlbums.asStateFlow()

    //region Currently Playing - the song currently playing
    private val _currentlyPlaying = MutableStateFlow<SongInfo?>(null)
    val currentlyPlaying : StateFlow<SongInfo?>
        get() = _currentlyPlaying.asStateFlow()

    fun setCurrentlyPlaying(iSongInfo: ISongInfo) {
        if (iSongInfo !is SongInfo) return
        val idx = currentPlayingSongs.indexOf(iSongInfo)
        if (idx == -1) return
        mediaController?.seekToDefaultPosition(idx)
        mediaController?.prepare()
        mediaController?.play()
    }

    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
        super.onMediaMetadataChanged(mediaMetadata)
        val songInfo = currentAllSongs
            .find{it.data == mediaMetadata.mediaUri.toString()} ?: return
        if (!_currentlyPlaying.value.isDifferentFrom(songInfo.toMediaItem())) return
        _currentlyPlaying.value = songInfo

//        // Set the proper album artwork on the media session, so it can be shown in the
//        // locked screen and in other places.
//        if (mediaMetadata.description.iconBitmap == null &&
//            mediaMetadata.description.iconUri != null) {
//            String albumUri = metadata.description.iconUri.toString();
//            mediaController?.setMeta
//
//
        }




    //endregion Currently Playing

    fun setPlaylist(iSongs:List<ISongInfo>, shuffle:Boolean) {
        val songs = iSongs.map {it as SongInfo}
        currentPlayingSongs = songs
        mediaController?.setMediaItems(songs.map{it.toMediaItem()})
        mediaController?.shuffleModeEnabled = shuffle
        mediaController?.prepare()
    }

    fun playNext() {
        if (mediaController == null) return
        if (mediaController?.hasNextMediaItem() != true) return
        mediaController?.seekToNextMediaItem()
    }

    //region Is Paused - true if not playing; false if playing
    private val _isPaused = MutableStateFlow<Boolean>(false)
    val isPaused : StateFlow<Boolean>
        get() = _isPaused.asStateFlow()

    fun togglePlayPause() {
        _isPaused.value = !_isPaused.value
        if (_currentlyPlaying.value == null) return
        if (_isPaused.value) {mediaController?.pause()}
        else {mediaController?.play()}
    }

    fun play() {
        _isPaused.value = false
        mediaController?.play()
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        _isPaused.value = !isPlaying
    }
    //endregion  Is Paused

    private var currentAllSongs: List<SongInfo> = emptyList()
    private var songsByArtist:Map<ArtistInfo, List<SongInfo>> = emptyMap()
    private var songsByAlbum:Map<AlbumInfo, List<SongInfo>> = emptyMap()
    private var albumsByArtist:Map<ArtistInfo, List<AlbumInfo>> = emptyMap()
    private var songsByGenre:Map<GenreInfo, List<SongInfo>> = emptyMap()

    private val uiDispatcher = Dispatchers.Main
    private val uiScope = CoroutineScope(uiDispatcher)
    private val ioDispatcher = Dispatchers.IO

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

    fun toggleRepeat() {
        mediaController?.repeatMode = Player.REPEAT_MODE_ALL
    }

    private fun getAllSongs():List<SongInfo> {
        val allSongs = Repository.getAllSongs(getApplication())
        currentAllSongs = allSongs.sortedBy{it.title.forSorting()}
        buildData(allSongs)
        return currentAllSongs
    }

    private fun buildData(allSongs:List<SongInfo>) {
        // TODO: take all songs and divide into the following:

        // - albums by artist
        albumsByArtist = allSongs
            .groupBy({k -> ArtistInfo(k.artist, k.artistId)},{v->AlbumInfo(v.album, v.albumYear, v.albumId, v.artistId, v.albumArt) })
            .mapValues { kv -> kv.value.distinct().sortedBy{it.albumYear} }
            .toSortedMap(compareBy{it.name.forSorting()}) // sort by artist name

        // - songs by artist
        songsByArtist = allSongs
            .groupBy { ArtistInfo(it.artist, it.artistId, ) }
            .toSortedMap(compareBy{it.name.forSorting()}) // sort by artist name

        // - songs by album
        songsByAlbum = allSongs
            .groupBy { AlbumInfo(it.album, it.albumYear, it.albumId, it.artistId, it.albumArt) }
            .toSortedMap(compareBy{it.name.forSorting()}) // sort by album name



        // - songs by genre
        songsByGenre = allSongs
            .groupBy { k -> GenreInfo(k.genre, k.genreId) }
            .toSortedMap(compareBy { it.genreName.forSorting() })

        // - albums by genre
        // - songs by favorite
    }
}