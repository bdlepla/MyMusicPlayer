package com.bdlepla.android.mymusicplayer

import com.bdlepla.android.mymusicplayer.business.AlbumInfo
import com.bdlepla.android.mymusicplayer.business.ArtistInfo
import com.bdlepla.android.mymusicplayer.business.ISongInfo

class PreviewSongInfo(override val title: String,
                      override val artist: String,
                      override val album: String,
                      override val albumArt: String?=null,
                      override val songId: Long=title.hashCode().toLong(),
                      override val albumYear: Int=2000,
                      override val artistId: Long=songId,
                      override val albumId: Long=songId,
                      override val trackNumber: Int=1,
) : ISongInfo

object SampleData {
    private val songData = listOf(
        listOf("Speed of Sound", "Coldplay", "X & Y"),
        listOf("Someone Like You", "Adele", "21"),
        listOf("Carry on my Wayward Son", "Kansas", "Leftoverture"),
        listOf("Mr. Blue Sky", "ELO", "Greatest Hits")
    )
    val Songs = songData.map {
        PreviewSongInfo(it[0], it[1], it[2])
    }

    val Artists = Songs.map{ ArtistInfo(it.artist, it.artistId) }
    val Albums = Songs.map{AlbumInfo(it.album, it.albumYear, it.albumId, it.artistId, it.albumArt)}
}