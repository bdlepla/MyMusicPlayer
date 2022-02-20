package com.bdlepla.android.mymusicplayer.repository

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Size
import androidx.core.database.getStringOrNull
import com.bdlepla.android.mymusicplayer.business.SongInfo
import java.io.File
import java.io.FileOutputStream

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
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val nameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val albumColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumArtistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ARTIST)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
            val yearColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)
            val trackColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val artistIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)

            while (cursor.moveToNext()) {
                val songId = cursor.getLong(idColumn)
                val name = cursor.getString(nameColumn)
                val album = cursor.getString(albumColumn)
                val albumArtist = cursor.getStringOrNull(albumArtistColumn)
                val artist = albumArtist ?: cursor.getString(artistColumn)
                val data = cursor.getString(dataColumn)
                val year = cursor.getInt(yearColumn)
                val track = cursor.getInt(trackColumn)
                val albumId = cursor.getLong(albumIdColumn)
                val artistId = cursor.getLong(artistIdColumn)
                val albumArt = getAlbumArt(context, albumId)
                //Log.e("id :$id", " data :$data")
                //Log.e("album :$album", " Artist :$artist")
                //Log.e("data :", data)
                val songInfo = SongInfo(name, artist, album, year, track, songId, albumId, artistId, albumArt)
                ret.add(songInfo)
            }
        }
        return ret
    }

    private fun getAlbumArt(context:Context, albumId:Long): String? {
        val albumArtUri = ContentUris.withAppendedId(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, albumId)
         try {
            val bitmap = context.contentResolver.loadThumbnail(albumArtUri, Size(128, 128), null)
            val art = File(context.cacheDir, "albumart$albumId.jpg")
                .also {
                    if (!it.exists()) {
                        it.createNewFile()
                        FileOutputStream(it).use { fos ->
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos)
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