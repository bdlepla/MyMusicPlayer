package com.bdlepla.android.mymusicplayer.business

import android.app.Application
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileWriter
import java.nio.file.Path
import kotlin.io.path.*

class PlaylistManager(private val application: Application) {
    private var _currentPlaylist:List<SongInfo>? = null
    val currentPlaylist: List<SongInfo>?
        get() = _currentPlaylist

    fun setPlaylist(songs:List<SongInfo>) {
        _currentPlaylist = songs
    }

    private var playLists: MutableMap<String, MutableList<String>> = loadPlaylists()
    private val appStoragePath
        get() = Path(application.getExternalFilesDir(null).toString())
    private val mediaPath: Path
        get() = Path(Environment.getExternalStorageDirectory().path+"/Music")

    private fun getFullFilename(name:String) = "$appStoragePath/$name.m3u"

    @OptIn(ExperimentalPathApi::class)
    private fun loadPlaylists():MutableMap<String, MutableList<String>> =
        appStoragePath
            .walk(PathWalkOption.INCLUDE_DIRECTORIES)
            .map{it.toFile().nameWithoutExtension to it.toString()}
            .filter{it.second.endsWith(".m3u")}
            .map{it to loadPlaylistSongs(it.second)}
            .associate{it.first.first to it.second.toMutableList()}.toMutableMap()

    private fun loadPlaylistSongs(m3uName:String):List<String> {
        Log.d("PlaylistManager","Loading $m3uName")
        return File(m3uName)
            .readLines()
            .asSequence()
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .filter { it[0] != '#' }
            .filter {!it.contains("/Music/")}
            .onEach { Log.d("PlaylistManager", it) }
            .toList()
    }
    private fun savePlaylistSongs(m3uname:String, songNames:List<String>) {
        if (File(m3uname).exists()){
            Log.d("PlaylistManager", "deleting $m3uname")
            File(m3uname).delete()
        }
        Log.d("PlaylistManager", "creating $m3uname")
        FileWriter(m3uname).use { fw ->
            songNames.forEach { songName ->
                fw.write(songName+"\n")
                Log.d("PlaylistManager", "writing $songName")
            }
        }
    }

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

    fun addSongsToPlaylist(playListInfo: PlaylistInfo, songInfos: List<SongInfo>) {
        val songs = songInfos.map{ Path(it.mediaUri).relativeToOrSelf(mediaPath).toString()}
        val name = playListInfo.name
        if (playLists.containsKey(name)){
            val playlistSongNames = playLists[name]
            playlistSongNames!!.addAll(songs)
            playLists[name] = playlistSongNames
            val fileName = getFullFilename(name)
            savePlaylistSongs(fileName, playlistSongNames)
        }
    }

    fun removeSongFromPlaylist(playListInfo: PlaylistInfo, songInfo: SongInfo) {
        val song = Path(songInfo.mediaUri).relativeToOrSelf(mediaPath).toString()
        val name = playListInfo.name
        if (playLists.containsKey(name)){
            val playlistSongNames = playLists[name]
            playlistSongNames!!.remove(song)
            playLists[name] = playlistSongNames
            val fileName = getFullFilename(name)
            savePlaylistSongs(fileName, playlistSongNames)
        }
    }

    fun addNewPlaylist(name: String) {
        val songs = mutableListOf<String>()
        playLists[name]= songs
        val m3uname = getFullFilename(name)
        savePlaylistSongs(m3uname, songs)
    }

    fun removePlaylist(playListInfo: PlaylistInfo){
        playLists.remove(playListInfo.name)
        val fileName = getFullFilename(playListInfo.name)
        if (File(fileName).exists()){
            Log.d("PlaylistManager", "deleting $fileName")
            File(fileName).delete()
        }
    }

    // support the current playlist; save it so that upon restart, can pick up where left off

}


