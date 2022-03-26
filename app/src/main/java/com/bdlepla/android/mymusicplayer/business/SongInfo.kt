package com.bdlepla.android.mymusicplayer.business

import androidx.media3.common.MediaItem

interface ISongInfo {
    val title:String
    val artist:String
    val album:String
    val albumArt:String?
    val songId:Long
    val albumYear:Int
    val artistId:Long
    val albumId:Long
    val trackNumber:Int
}

class SongInfo(private val mediaItem: MediaItem):ISongInfo {
    fun toMediaItem() = mediaItem
    override val title = mediaItem.mediaMetadata.title.toString()
    override val trackNumber = mediaItem.mediaMetadata.trackNumber!!
    override val artist = mediaItem.mediaMetadata.artist.toString()
    override val album = mediaItem.mediaMetadata.albumTitle.toString()
    override val albumYear = mediaItem.mediaMetadata.recordingYear!!
    override val albumArt: String? = mediaItem.mediaMetadata.artworkUri.toString()
    val genre = mediaItem.mediaMetadata.genre.toString()
    val data = mediaItem.mediaMetadata.mediaUri.toString()
    val mediaId = mediaItem.mediaId
    override val songId = mediaId.toLong()
    override val albumId = mediaItem.mediaMetadata.extras?.getLong(ALBUM_ID)
        ?: throw NoSuchFieldException(ALBUM_ID)
    override val artistId = mediaItem.mediaMetadata.extras?.getLong(ARTIST_ID)
        ?: throw NoSuchFieldException(ARTIST_ID)
    val genreId = mediaItem.mediaMetadata.extras?.getLong(GENRE_ID)
        ?: throw NoSuchFieldException(GENRE_ID)
}

fun SongInfo?.isDifferentFrom(mediaItem:MediaItem): Boolean {
    return if (this == null) true
    else mediaId != mediaItem.mediaId
}
const val ALBUM_ID = "albumId"
const val ARTIST_ID = "artistId"
const val GENRE_ID = "genreId"
