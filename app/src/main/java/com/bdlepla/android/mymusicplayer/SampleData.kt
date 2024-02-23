package com.bdlepla.android.mymusicplayer

import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.bdlepla.android.mymusicplayer.business.AlbumInfo
import com.bdlepla.android.mymusicplayer.business.ArtistInfo
import com.bdlepla.android.mymusicplayer.business.PlaylistInfo
import com.bdlepla.android.mymusicplayer.business.SongInfo
import com.bdlepla.android.mymusicplayer.repository.ALBUM_ID
import com.bdlepla.android.mymusicplayer.repository.ARTIST_ID
import com.bdlepla.android.mymusicplayer.repository.MEDIA_URI
import com.bdlepla.android.mymusicplayer.service.MediaItemTree.ITEM_PREFIX

class SampleData {
    private val songData = listOf(
        listOf("Speed of Sound", "Coldplay", "X & Y"),
        listOf("Someone Like You", "Adele", "21"),
        listOf("Carry on my Wayward Son", "Kansas", "Leftoverture"),
        listOf("Mr. Blue Sky", "ELO", "Greatest Hits")
    )
    val songs = songData.mapIndexed { idx, sd -> sd.toSongInfo(idx+1) }
    val artists = songs.map{ ArtistInfo(it.artist, it.artistId) }
    val albums = songs.map{AlbumInfo(it.album, it.albumYear, it.albumId, it.artistId, it.albumArt)}
    val playlists = listOf(
        PlaylistInfo("Beginning", songs.take(2)),
        PlaylistInfo("Ending", songs.drop(2))
    )
}

private fun List<String>.toSongInfo(songId: Int): SongInfo {
    val title = this[0]
    val artist = this[1]
    val album = this[2]

    val bundle = Bundle().apply {
        putLong(ALBUM_ID, songId.toLong())
        putLong(ARTIST_ID, songId.toLong())
        //putLong(GENRE_ID, songId.toLong())
        putString(MEDIA_URI, "")
    }

    val mediaItemMeta = MediaMetadata.Builder()
        .setTitle(title)
        .setAlbumTitle(album)
        .setArtist(artist)
        .setExtras(bundle)
        .build()
    val mediaItem = MediaItem.Builder()
        .setMediaMetadata(mediaItemMeta)
        .setMediaId(ITEM_PREFIX+songId.toString())
        .build()

    return SongInfo(mediaItem)
}