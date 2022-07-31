package com.bdlepla.android.mymusicplayer.business

data class CurrentPlayingStats(
    val currentPlaying: SongInfo?,
    val currentPosition:Int,
    val maxPosition:Int,
)
