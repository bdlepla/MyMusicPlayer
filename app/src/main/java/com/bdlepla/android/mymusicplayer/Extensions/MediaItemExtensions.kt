package com.bdlepla.android.mymusicplayer.repository

import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import com.bdlepla.android.mymusicplayer.business.SongInfo

@UnstableApi
private fun SongInfo.toMediaItem(folderType: Int = MediaMetadata.FOLDER_TYPE_NONE): MediaItem {
    val metadata =
        MediaMetadata.Builder()
            .setAlbumTitle(album)
            .setTitle(title)
            .setArtist(artist)
            .setGenre(genre)
            .setFolderType(folderType)
            .setIsPlayable(true)
            .setArtworkUri(albumArt?.toUri())
            .build()
    return MediaItem.Builder()
        .setMediaId(songId.toString())
        .setMediaMetadata(metadata)
        .setUri(data?.toUri())
        .build()
}