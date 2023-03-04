package com.bdlepla.android.mymusicplayer.business

import android.os.Environment
import java.io.File
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path
import kotlin.io.path.PathWalkOption
import kotlin.io.path.walk

class PlaylistManager {
    private var _currentPlaylist:List<SongInfo>? = null
    val currentPlaylist: List<SongInfo>?
        get() = _currentPlaylist

    fun setPlaylist(songs:List<SongInfo>) {
        _currentPlaylist = songs
    }

    private var playLists: Map<String, List<String>> = loadPlaylists()

    @OptIn(ExperimentalPathApi::class)
    private fun loadPlaylists():Map<String, List<String>> =
        Path(Environment.getExternalStorageDirectory().toString()+"/Music")
            .walk(PathWalkOption.INCLUDE_DIRECTORIES)
            .map{it.toFile().nameWithoutExtension to it.toString()}
            .filter{it.second.endsWith(".m3u")}
            .map{it to loadPlaylistSongs(it.second)}
            .associate{it.first.first to it.second}

    private fun loadPlaylistSongs(m3uName:String):List<String> =
        File(m3uName)
            .readLines()
            .map{it.trim()}
            .filter{it.isNotEmpty() }
            .filter{it[0] != '#'}

    fun updatePlaylistInfo(songs:List<SongInfo>):List<PlaylistInfo> =
        playLists.map {
            val playListSongs = it.value.mapNotNull { playListSong ->
                songs.firstOrNull { songInCollection ->
                    songInCollection.mediaUri.endsWith(playListSong)
                }
            }
            var artworkString:String? = null
            if (playListSongs.any()){
                val songsForArtwork = playListSongs.random()
                artworkString = songsForArtwork.albumArt
            }

            PlaylistInfo(it.key, playListSongs, artworkString)
        }

     // add and remove playlists
    // add and remove songs from a playlist
    // support the current playlist; save it so that upon restart, can pick up where left off

}

