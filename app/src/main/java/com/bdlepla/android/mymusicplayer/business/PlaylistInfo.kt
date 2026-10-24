package com.bdlepla.android.mymusicplayer.business

import com.bdlepla.android.mymusicplayer.AutomaticPlaylistConfig
import com.danrusu.pods4k.immutableArrays.ImmutableArray
import com.danrusu.pods4k.immutableArrays.toImmutableArray

data class PlaylistInfo(
    val name: String,
    val songs: ImmutableArray<SongInfo>,
    val artwork: String? = null,
    val automaticConfig: AutomaticPlaylistConfig? = null
) {
    fun evaluateSongs(allSongs: ImmutableArray<SongInfo>): ImmutableArray<SongInfo> {
        val config = automaticConfig ?: return songs
        var filteredSongs = allSongs.asIterable()
        if (config.applyFilter) {
            if (config.attribute == "Favorite") {
                val wantFavorite = config.value.equals("true", ignoreCase = true)
                filteredSongs = filteredSongs.filter { it.isFavorite == wantFavorite }
            }
        }
        var songList = filteredSongs.toList().shuffled()
        if (config.applyLimit) {
            if (config.limitType == "Number of songs") {
                songList = songList.take(config.limitValue)
            } else if (config.limitType == "Minutes") {
                val maxSeconds = config.limitValue * 60
                var totalSeconds = 0
                val limited = mutableListOf<SongInfo>()
                for (song in songList) {
                    limited.add(song)
                    totalSeconds += song.duration
                    if (totalSeconds > maxSeconds) break
                }
                songList = limited
            }
        }
        return songList.toImmutableArray()
    }
}
