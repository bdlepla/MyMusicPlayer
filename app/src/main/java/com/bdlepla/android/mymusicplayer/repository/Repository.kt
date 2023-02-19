package com.bdlepla.android.mymusicplayer.repository

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import android.util.Size
import androidx.core.database.getStringOrNull
import androidx.core.net.toUri
import androidx.media.utils.MediaConstants
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.bdlepla.android.mymusicplayer.service.MediaItemTree.ITEM_PREFIX
import java.io.File
import java.io.FileOutputStream

object Repository {

    fun getAllSongs(context: Context):List<MediaItem> {
        val ret:MutableList<MediaItem> = mutableListOf()
        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.AudioColumns._ID,
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.AudioColumns.ALBUM,
            MediaStore.Audio.AudioColumns.ARTIST,
            MediaStore.Audio.AudioColumns.ALBUM_ARTIST,
            MediaStore.Audio.AudioColumns.DATA,
            MediaStore.Audio.AudioColumns.YEAR,
            MediaStore.Audio.AudioColumns.TRACK,
            MediaStore.Audio.AudioColumns.ALBUM_ID,
            MediaStore.Audio.AudioColumns.ARTIST_ID,
            MediaStore.Audio.AudioColumns.GENRE,
            MediaStore.Audio.AudioColumns.GENRE_ID,
            MediaStore.Audio.AudioColumns.DURATION
        )
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val selectionArgs = null
        val sortOrder =  "year ASC, track ASC"

        val query = context.contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        query?.use { cursor ->
            val songIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val albumColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumArtistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ARTIST)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val yearColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)
            val trackColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val artistIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)
            val genreColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.GENRE)
            val genreIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.GENRE_ID)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

            while (cursor.moveToNext()) {
                val data = cursor.getString(dataColumn)
                if (!data.endsWith(".mp3")){
                    Log.i("Repository.getAllSongs", "ignoring $data")
                    continue
                }

                val songId = cursor.getInt(songIdColumn)
                val title = cursor.getString(titleColumn)
                val album = cursor.getString(albumColumn)
                val albumArtist = cursor.getStringOrNull(albumArtistColumn)
                val artist = albumArtist ?: cursor.getString(artistColumn)
                val artistId = cursor.getLong(artistIdColumn)

                val year = cursor.getInt(yearColumn)
                val track = cursor.getInt(trackColumn)
                val albumId = cursor.getLong(albumIdColumn)
                val albumArt = getAlbumArt(context, albumId)
                val genre = cursor.getString(genreColumn)
                val genreId = cursor.getLong(genreIdColumn)
                val durationInSeconds = cursor.getInt(durationColumn) / 1000

                val bundle =  Bundle().apply {
                    putString(ALBUM, album)
                    putLong(ALBUM_ID, albumId)
                    putString(ARTIST, artist)
                    putLong(ARTIST_ID, artistId)
                    putString(GENRE, genre)
                    putLong(GENRE_ID, genreId)
                    putString(MEDIA_URI, data)
                    putInt(TRACK_NUMBER, track)
                    putInt(DURATION_ID, durationInSeconds)
                    putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, title)
                    putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, artist)
                    putString(MediaMetadataCompat.METADATA_KEY_ART_URI, albumArt)
                    putLong(MediaConstants.METADATA_KEY_IS_EXPLICIT,
                        MediaConstants.METADATA_VALUE_ATTRIBUTE_PRESENT)
                    putLong(
                        MediaDescriptionCompat.EXTRA_DOWNLOAD_STATUS,
                        MediaDescriptionCompat.STATUS_DOWNLOADED)
                }

                val metadata =
                    MediaMetadata.Builder()
                        .setTitle(title)
                        .setArtist(artist)
                        .setAlbumTitle(album)
                        .setAlbumArtist(artist)
                        .setArtworkUri(albumArt?.toUri())
                        .setGenre(genre)
                        .setFolderType(MediaMetadata.FOLDER_TYPE_NONE)
                        .setIsPlayable(true)
                        .setTrackNumber(track)
                        .setReleaseYear(year)
                        .setExtras(bundle)
                        .build()

                val item = MediaItem.Builder()
                    .setMediaId(ITEM_PREFIX+songId.toString())
                    .setMediaMetadata(metadata)
                    .setUri(data)
                    .build()

                ret.add(item)
            }
        }
        return ret
    }

    private fun getAlbumArt(context: Context, albumId:Long): String? {
        val albumArtUri = ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, albumId)
         try {
           val art = File(context.cacheDir, "albumArt$albumId.webp")
                .also {
                    if (!it.exists()) {
                       it.createNewFile()
                        FileOutputStream(it).use { fos ->
                            context.contentResolver
                                .loadThumbnail(albumArtUri, Size(256, 256), null)
                                .compress(Bitmap.CompressFormat.WEBP_LOSSY, 10, fos)
                            fos.flush()
                        }
                    }
                }
            return art.absolutePath
        } catch(_:Exception) {
            return null
        }
    }
}


const val ALBUM = "album"
const val ARTIST = "artist"
const val GENRE = "genre"
const val MEDIA_URI = "mediaUri"
const val TRACK_NUMBER = "trackNumber"
const val ROOT_ID = "[rootID]"
const val ALBUM_ID = "[albumID]"
const val GENRE_ID = "[genreID]"
const val ITEM_ID = "[itemID]"
const val ARTIST_ID = "[artistID]"
const val DURATION_ID = "[durationId]"