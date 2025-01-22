package com.bdlepla.android.mymusicplayer.business

import android.content.Context
import android.content.ContextWrapper
import android.os.Environment
import com.bdlepla.android.mymusicplayer.extensions.any
import com.bdlepla.android.mymusicplayer.extensions.random
import com.danrusu.pods4k.immutableArrays.ImmutableArray
import com.danrusu.pods4k.immutableArrays.emptyImmutableArray
import com.danrusu.pods4k.immutableArrays.multiplicativeSpecializations.map
import com.danrusu.pods4k.immutableArrays.toImmutableArray
import com.danrusu.pods4k.immutableArrays.toMutableList
import java.io.File
import java.io.FileWriter
import java.nio.file.Path
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path
import kotlin.io.path.PathWalkOption
import kotlin.io.path.relativeToOrSelf
import kotlin.io.path.walk

open class PlaylistBase(private val context: Context) {
    protected val appStoragePath
        get() = Path(context.getExternalFilesDir(null).toString())
}
open class PlaylistReader(context:Context) :PlaylistBase(context) {
    @OptIn(ExperimentalPathApi::class)
    fun loadPlaylists():MutableMap<String, MutableList<String>> =
        appStoragePath
            .walk(PathWalkOption.INCLUDE_DIRECTORIES)
            .map { it.toFile().nameWithoutExtension to it.toString() }
            .filter { it.second.endsWith(".m3u") }
            .map { it to loadPlaylistSongs(it.second) }
            .associate { it.first.first to it.second.toMutableList() }
            .toMutableMap()

    private fun loadPlaylistSongs(m3uName:String):ImmutableArray<String> {
        return File(m3uName)
            .readLines()
            .toImmutableArray() // OK; Do not change
            .map { it.trim() }
            .filter { it.isNotEmpty() && it[0] != '#' && !it.contains("/Music/") }
    }
}

class PlaylistManager(context: ContextWrapper) : PlaylistReader(context) {
    private var playLists: MutableMap<String, MutableList<String>> = loadPlaylists()

    private val mediaPath: Path
        get() = Path(Environment.getExternalStorageDirectory().path+"/Music")

    private fun getFullFilename(name:String) = "$appStoragePath/$name.m3u"


    private fun savePlaylistSongs(m3uname:String, songNames:ImmutableArray<String>) {
        if (File(m3uname).exists()){
            File(m3uname).delete()
        }
        FileWriter(m3uname).use { fw ->
            songNames.forEach { songName ->
                fw.write(songName+"\n")
            }
        }
    }

    fun updatePlaylistInfo(songs:ImmutableArray<SongInfo>):ImmutableArray<PlaylistInfo> =
        playLists.map {
            val playListSongs = it.value.mapNotNull { playListSong ->
                songs.firstOrNull { songInCollection ->
                    songInCollection.mediaUri.endsWith(playListSong)
                }
            }.toImmutableArray() // OK, Do not change
            var artworkString:String? = null
            if (playListSongs.any()){
                val songsForArtwork = playListSongs.random()
                artworkString = songsForArtwork.albumArt
            }

            PlaylistInfo(it.key, playListSongs, artworkString)
        }.toImmutableArray() // OK; Do not change

    fun addSongsToPlaylist(playListInfo: PlaylistInfo, songInfos: ImmutableArray<SongInfo>) {
        val songs = songInfos.map { Path(it.mediaUri).relativeToOrSelf(mediaPath).toString() }
        val name = playListInfo.name
        if (playLists.containsKey(name)) {
            val playlistSongNames = playLists[name]
            playlistSongNames!!.addAll(songs.asIterable())
            playLists[name] = playlistSongNames
            val fileName = getFullFilename(name)
            savePlaylistSongs(fileName, playlistSongNames.toImmutableArray()) // OK; Do not change
        }
    }

    fun removeSongFromPlaylist(playListInfo: PlaylistInfo, songInfo: SongInfo) {
        val song = Path(songInfo.mediaUri).relativeToOrSelf(mediaPath).toString()
        val name = playListInfo.name
        if (playLists.containsKey(name)) {
            val playlistSongNames = playLists[name]
            playlistSongNames!!.remove(song)
            playLists[name] = playlistSongNames
            val fileName = getFullFilename(name)
            savePlaylistSongs(fileName, playlistSongNames.toImmutableArray()) // OK; Do not change
        }
    }

    fun addNewPlaylist(name: String) {
        playLists[name] = mutableListOf()
        val m3uname = getFullFilename(name)
        savePlaylistSongs(m3uname, emptyImmutableArray())
    }

    fun removePlaylist(playListInfo: PlaylistInfo){
        playLists.remove(playListInfo.name)
        val fileName = getFullFilename(playListInfo.name)
        if (File(fileName).exists()) {
            File(fileName).delete()
        }
    }

    // support the current playlist; save it so that upon restart, can pick up where left off

}


