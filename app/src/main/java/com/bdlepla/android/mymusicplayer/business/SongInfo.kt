package com.bdlepla.android.mymusicplayer.business

import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.bdlepla.android.mymusicplayer.repository.ALBUM
import com.bdlepla.android.mymusicplayer.repository.ALBUM_ID
import com.bdlepla.android.mymusicplayer.repository.ARTIST
import com.bdlepla.android.mymusicplayer.repository.ARTIST_ID
import com.bdlepla.android.mymusicplayer.repository.DURATION
import com.bdlepla.android.mymusicplayer.repository.MEDIA_URI
import com.bdlepla.android.mymusicplayer.repository.TRACK_NUMBER

class SongInfo(private val mediaItem: MediaItem) {
    fun toMediaItem() = mediaItem
    private val mediaMetadata = mediaItem.mediaMetadata
    val title = mediaMetadata.title.toString()
    val trackNumber = mediaMetadata.track
    val artist = mediaMetadata.artistName
    val album = mediaMetadata.albumName
    val albumYear = mediaMetadata.releaseYear ?: 0
    val albumArt = mediaMetadata.artworkUri.toString()
    val duration = mediaMetadata.duration

    //val genre = mediaMetadata.genreName
    val mediaUri = mediaMetadata.mediaUri
    private val mediaId = mediaItem.mediaId
    val songId = if (mediaId.startsWith("[item]")) {
        mediaId.substring(6).toLongOrNull() ?: 0L
    } else {
        0L
    }
    val albumId = mediaMetadata.albumId
    val artistId = mediaMetadata.artistId
    //val genreId = mediaMetadata.genreId

    override fun equals(other: Any?): Boolean {
        val otherSong = other as? SongInfo ?: return false
        return otherSong.songId == songId
    }

    override fun hashCode(): Int = songId.hashCode()
}

val MediaMetadata.track: Int
    get() = trackNumber ?: (extras?.getInt(TRACK_NUMBER) ?: 0)

val MediaMetadata.artistName:String
    get() {
        return artist?.toString()
            ?: (extras?.getString(ARTIST) ?: "")
    }

val MediaMetadata.albumName:String
    get() {
        return albumTitle?.toString()
            ?: (extras?.getString(ALBUM) ?: "")
    }

val MediaMetadata.duration:Int
    get() = extras?.getInt(DURATION) ?: 0

//val MediaMetadata.genreName:String
//    get() {
//        return if (genre != null) genre.toString()
//        else extras?.getString(GENRE) ?: ""
//    }
//

val MediaMetadata.mediaUri:String
    get() = extras?.getString(MEDIA_URI) ?: ""

val MediaMetadata.albumId:Long
    get() = extras?.getLong(ALBUM_ID) ?: 0L

val MediaMetadata.artistId:Long
    get() = extras?.getLong(ARTIST_ID) ?: 0L

//val MediaMetadata.genreId:Long
//    get() = extras?.getLong(GENRE_ID) ?: throw NoSuchFieldException(GENRE_ID)
