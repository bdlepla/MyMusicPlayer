package com.bdlepla.android.mymusicplayer.repository

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import androidx.core.database.getStringOrNull
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MediaMetadata.PICTURE_TYPE_FRONT_COVER
import com.bdlepla.android.mymusicplayer.business.ALBUM_ID
import com.bdlepla.android.mymusicplayer.business.ARTIST_ID
import com.bdlepla.android.mymusicplayer.business.GENRE_ID
import com.bdlepla.android.mymusicplayer.business.SongInfo
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer

object Repository {

    fun getAllSongs(context: Context):List<SongInfo> {
        val ret:MutableList<SongInfo> = mutableListOf()
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
            MediaStore.Audio.AudioColumns.GENRE_ID
        )
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val selectionArgs = null
        val sortOrder =  "year ASC, track ASC"

        context.contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use { cursor ->
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

                val metadata =
                    MediaMetadata.Builder()
                        .setAlbumTitle(album)
                        .setTitle(title)
                        .setArtist(artist)
                        .setGenre(genre)
                        .setFolderType(MediaMetadata.FOLDER_TYPE_NONE)
                        .setIsPlayable(true)
                        .setArtworkUri(albumArt?.toUri())
                        .setMediaUri(data.toUri())
                        .setTrackNumber(track)
                        .setRecordingYear(year)
                        .setExtras(bundleOf(
                            Pair(ALBUM_ID, albumId),
                            Pair(ARTIST_ID, artistId),
                            Pair(GENRE_ID, genreId)))
                        .build()
                val item =  MediaItem.Builder()
                    .setMediaId(songId.toString())
                    .setMediaMetadata(metadata)
                    .setUri(data)
                    .build()
                val songInfo = SongInfo(item)
                ret.add(songInfo)
            }
        }
        return ret
    }

    private fun getAlbumArt(context: Context, albumId:Long): String? {
        val albumArtUri = ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, albumId)
         try {
           val art = File(context.cacheDir, "albumart$albumId.webp")
                .also {
                    if (!it.exists()) {
                       it.createNewFile()
                        FileOutputStream(it).use { fos ->
                            context.contentResolver
                                .loadThumbnail(albumArtUri, Size(256, 256), null)
                                .compress(Bitmap.CompressFormat.JPEG, 10, fos)
                            fos.flush()
                            // use will automatically close fos
                        }
                    }
                }
            return art.absolutePath
        } catch(_:Exception) {
            return null
        }
    }
}