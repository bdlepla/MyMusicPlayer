package com.bdlepla.android.mymusicplayer

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.bdlepla.android.mymusicplayer.business.AlbumInfo
import com.bdlepla.android.mymusicplayer.business.ArtistInfo
import com.bdlepla.android.mymusicplayer.business.SongInfo

object SampleData {
    private val songData = listOf(
        listOf("Speed of Sound", "Coldplay", "X & Y"),
        listOf("Someone Like You", "Adele", "21"),
        listOf("Carry on my Wayward Son", "Kansas", "Leftoverture"),
        listOf("Mr. Blue Sky", "ELO", "Greatest Hits")
    )
    val Songs = songData.map {
        val metadata =
            MediaMetadata.Builder()
                .setTitle(it[0])
                .setArtist(it[1])
                .setAlbumTitle(it[2])
                .setGenre("Music")
                .setFolderType(MediaMetadata.FOLDER_TYPE_NONE)
                .setIsPlayable(true)
                .build()
        val item =  MediaItem.Builder()
            .setMediaId("1")
            .setMediaMetadata(metadata)
            .build()
        SongInfo(item)
    }


    val Artists = Songs.map{ ArtistInfo(it.artist) }
    val Albums = Songs.map{AlbumInfo(it.album, it.albumYear, it.albumArt)}
}