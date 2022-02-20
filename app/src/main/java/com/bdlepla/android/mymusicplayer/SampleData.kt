package com.bdlepla.android.mymusicplayer

import com.bdlepla.android.mymusicplayer.business.AlbumInfo
import com.bdlepla.android.mymusicplayer.business.ArtistInfo
import com.bdlepla.android.mymusicplayer.business.SongInfo

object SampleData {
    val Songs = listOf(
        SongInfo("Speed of Sound", "Coldplay", "X & Y", 2000, 1, 1, 1, 1, null),
        SongInfo("Someone Like You", "Adele", "21", 2000, 1, 1, 1, 1, null),
        SongInfo("Carry on my Wayward Son", "Kansas", "Leftoverture", 2000, 1, 1, 1, 1, null),
        SongInfo("Mr. Blue Sky", "ELO", "Greatest Hits", 2000, 1, 1, 1, 1, null)
    )

    val Artists = Songs.map{ ArtistInfo(it.artist, it.artistId) }
    val Albums = Songs.map{AlbumInfo(it.album, it.albumId, it.albumYear, it.albumArt)}
}