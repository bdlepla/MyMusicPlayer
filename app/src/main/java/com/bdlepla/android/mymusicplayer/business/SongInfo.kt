package com.bdlepla.android.mymusicplayer.business

import androidx.media3.common.MediaItem

class SongInfo(private val mediaItem: MediaItem) {
    fun toMediaItem() = mediaItem
    val title = mediaItem.mediaMetadata.title.toString()
    val artist = mediaItem.mediaMetadata.artist.toString()
    val album = mediaItem.mediaMetadata.albumTitle.toString()
    val albumYear = mediaItem.mediaMetadata.recordingYear!!
    val albumArt = mediaItem.mediaMetadata.artworkUri.toString()
    val genre = mediaItem.mediaMetadata.genre.toString()
    val data = mediaItem.mediaMetadata.mediaUri.toString()
    val mediaId = mediaItem.mediaId
}

fun SongInfo?.isDifferentFrom(mediaItem:MediaItem): Boolean {
    return if (this == null) true
    else mediaId == mediaItem.mediaId

}
