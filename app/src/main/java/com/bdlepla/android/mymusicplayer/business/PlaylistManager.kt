package com.bdlepla.android.mymusicplayer.business

import android.content.Context
import android.content.ContextWrapper
import android.os.Environment
import com.bdlepla.android.mymusicplayer.extensions.any
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
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.relativeToOrSelf
import kotlin.io.path.walk

open class PlaylistBase(private val context: Context) {
    protected val appStoragePath: Path?
        get() {
            val dir = context.getExternalFilesDir(null) ?: return null
            if (!dir.exists()) {
                dir.mkdirs()
            }
            return Path(dir.absolutePath)
        }
}

open class PlaylistReader(context: Context) : PlaylistBase(context) {
    @OptIn(ExperimentalPathApi::class)
    fun loadPlaylists(): MutableMap<String, MutableList<String>> {
        val path = appStoragePath ?: return mutableMapOf()
        if (!path.exists()) {
            return mutableMapOf()
        }
        return try {
            path.walk(PathWalkOption.INCLUDE_DIRECTORIES)
                .map { it.toFile().nameWithoutExtension to it.toString() }
                .filter { it.second.endsWith(".m3u") }
                .map { it to loadPlaylistSongs(it.second) }
                .associate { it.first.first to it.second.toMutableList() }
                .toMutableMap()
        } catch (_: Exception) {
            mutableMapOf()
        }
    }

    private fun loadPlaylistSongs(m3uName: String): ImmutableArray<String> {
        val file = File(m3uName)
        if (!file.exists()) return emptyImmutableArray()
        return try {
            file.readLines()
                .toImmutableArray() // OK; Do not change
                .map { it.trim() }
                .filter { it.isNotEmpty() && it[0] != '#' && !it.contains("/Music/") }
        } catch (_: Exception) {
            emptyImmutableArray()
        }
    }
}

class PlaylistManager(context: ContextWrapper) : PlaylistReader(context) {
    private var playLists: MutableMap<String, MutableList<String>> = loadPlaylists()

    private val mediaPath: Path
        get() = Path(Environment.getExternalStorageDirectory().path + "/Music")

    private fun getFullFilename(name: String): String? {
        val path = appStoragePath ?: return null
        if (!path.exists()) {
            path.createDirectories()
        }
        return path.resolve("$name.m3u").toString()
    }

    private fun savePlaylistSongs(m3uname: String?, songNames: ImmutableArray<String>) {
        if (m3uname == null) return
        val file = File(m3uname)
        file.parentFile?.mkdirs()
        if (file.exists()) {
            file.delete()
        }
        try {
            FileWriter(file).use { fw ->
                songNames.forEach { songName ->
                    fw.write(songName + "\n")
                }
            }
        } catch (_: Exception) {
            // handle error gracefully
        }
    }

    fun updatePlaylistInfo(songs: ImmutableArray<SongInfo>): ImmutableArray<PlaylistInfo> =
        playLists.map {
            val playListSongs = it.value.mapNotNull { playListSong ->
                songs.firstOrNull { songInCollection ->
                    songInCollection.mediaUri.endsWith(playListSong)
                }
            }.toImmutableArray() // OK, Do not change
            var artworkString: String? = null
            if (playListSongs.any()) {
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

    fun removePlaylist(playListInfo: PlaylistInfo) {
        playLists.remove(playListInfo.name)
        val fileName = getFullFilename(playListInfo.name)
        if (fileName != null) {
            val file = File(fileName)
            if (file.exists()) {
                file.delete()
            }
        }
    }
}
