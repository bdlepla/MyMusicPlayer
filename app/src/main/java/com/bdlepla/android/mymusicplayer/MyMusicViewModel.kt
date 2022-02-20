package com.bdlepla.android.mymusicplayer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.bdlepla.android.mymusicplayer.business.AlbumInfo
import com.bdlepla.android.mymusicplayer.business.ArtistInfo
import com.bdlepla.android.mymusicplayer.business.SongInfo
import com.bdlepla.android.mymusicplayer.repository.Repository
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
@Inject constructor(application: Application): AndroidViewModel(application) {

    private val _allSongs = MutableStateFlow<List<SongInfo>>(emptyList())
    val allSongs : StateFlow<List<SongInfo>>
        get() = _allSongs.asStateFlow()

    private val _allArtists = MutableStateFlow<List<ArtistInfo>>(emptyList())
    val allArtists : StateFlow<List<ArtistInfo>>
        get() = _allArtists.asStateFlow()

    private val _allAlbums = MutableStateFlow<List<AlbumInfo>>(emptyList())
    val allAlbums : StateFlow<List<AlbumInfo>>
        get() = _allAlbums.asStateFlow()

    private val _currentlyPlaying = MutableStateFlow<SongInfo?>(null)
    val currentlyPlaying : StateFlow<SongInfo?>
        get() = _currentlyPlaying.asStateFlow()

    fun setCurrentlyPlaying(songInfo: SongInfo) {
        _currentlyPlaying.value = songInfo
    }

    private val _isPaused = MutableStateFlow<Boolean>(false)
    val isPaused : StateFlow<Boolean>
        get() = _isPaused.asStateFlow()

    fun togglePlayPause() {
        _isPaused.value = !_isPaused.value
    }

    var currentAllSongs: List<SongInfo> = emptyList()
    var songsByArtist:Map<ArtistInfo, List<SongInfo>> = emptyMap()
    var songsByAlbum:Map<AlbumInfo, List<SongInfo>> = emptyMap()
    var albumsByArtist:Map<ArtistInfo, List<AlbumInfo>> = emptyMap()

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
            .groupBy { ArtistInfo(it.artist, it.artistId) }
            .toSortedMap(compareBy{it.name}) // sort by artist name

        // - songs by album
        songsByAlbum = allSongs
            .groupBy { AlbumInfo(it.album, it.albumId, it.albumYear, it.albumArt) }
            .toSortedMap(compareBy{it.name}) // sort by album name

        // - songs by genre

        // - albums by artist
        // TODO: Sort by album year
        albumsByArtist = allSongs
            .groupBy({k -> ArtistInfo(k.artist, k.artistId)},{v->AlbumInfo(v.album, v.albumId, v.albumYear, v.albumArt) })
            .mapValues { kv -> kv.value.distinct().sortedBy{it.albumYear} }
            .toSortedMap(compareBy{it.name}) // sort by artist name

    // - artists by genre
        // - albums by genre
        // - songs by favorite
    }


}