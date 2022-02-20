package com.bdlepla.android.mymusicplayer.business

data class SongInfo(
    val title: String,
    val artist: String,
    val album: String,
    val albumYear: Int,
    val trackNumber: Int,
    val songId: Long,
    val albumId: Long,
    val artistId: Long,
    val albumArt: String?)