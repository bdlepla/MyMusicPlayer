package com.bdlepla.android.mymusicplayer.service

import com.bdlepla.android.mymusicplayer.business.PlaylistInfo
import com.bdlepla.android.mymusicplayer.business.SongInfo
import com.danrusu.pods4k.immutableArrays.toList
import com.danrusu.pods4k.immutableArrays.toMutableList

class QueueManager(private val defaultPlaylistProvider: () -> PlaylistInfo) {
    val songInfos = defaultPlaylistProvider().songs.toMutableList()
    var currentIndex = 0
    var currentPosition = 0L

    private fun ensureInitialized() {
        if (songInfos.isEmpty()) {
            val default = defaultPlaylistProvider()
            songInfos.addAll(default.songs.toList())
            currentIndex = 0
            currentPosition = 0L
        }
    }

    fun pushImmediately(playlist: PlaylistInfo) {
        ensureInitialized()
        val toTheEnd = songInfos.drop(currentIndex) // we want current playing song to restart after playlist is finished
        songInfos.clear()
        songInfos.addAll(0, playlist.songs.toList()+toTheEnd)
        currentIndex = 0
        currentPosition = 0L
    }

    fun playAfterCurrentPlaylist(playlist: PlaylistInfo) {
        ensureInitialized()
        songInfos.addAll(playlist.songs.toList())
    }

    fun playAfterCurrentSong(playlist: PlaylistInfo) {
        ensureInitialized()
        val currentRemaining = songInfos.drop(currentIndex)
        songInfos.clear()
        songInfos.addAll(0, currentRemaining.take(1)+playlist.songs.toList()+currentRemaining.drop(1))
        currentIndex = 0
    }

    fun updateCurrentState(index: Int, position: Long) {
        currentIndex = index
        currentPosition = position
    }

    fun setSongs(songs: List<SongInfo>, index: Int, position: Long) {
        songInfos.clear()
        songInfos.addAll(songs)
        currentIndex = index
        currentPosition = position
    }

    fun getSongs(): List<SongInfo> {
        if (isEmpty) {
            setSongs(defaultPlaylistProvider().songs.toList(), 0, 0)
        }
        return songInfos
    }

    val isEmpty: Boolean get() = songInfos.isEmpty()


}
