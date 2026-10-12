package com.bdlepla.android.mymusicplayer.repository

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import androidx.core.content.edit
import androidx.core.database.getStringOrNull
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.bdlepla.android.mymusicplayer.service.MediaItemTree.ITEM_PREFIX
import com.danrusu.pods4k.immutableArrays.ImmutableArray
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import java.io.File
import java.io.FileOutputStream

object Repository {

    private val _favoriteChanges = MutableSharedFlow<Pair<Long, Boolean>>(extraBufferCapacity = 64)
    val favoriteChanges: SharedFlow<Pair<Long, Boolean>> = _favoriteChanges.asSharedFlow()

    private fun getFavoriteSongIds(context: Context): Set<Long> {
        val prefs = context.getSharedPreferences("favorite_songs_prefs", Context.MODE_PRIVATE)
        val set = prefs.getStringSet("favorite_ids", emptySet()) ?: emptySet()
        return set.mapNotNull { it.toLongOrNull() }.toSet()
    }

    fun getAllAlbumYears(context: Context): Map<Long, Int> {
        return buildMap {
            val collection = MediaStore.Audio.Albums.getContentUri(MediaStore.VOLUME_EXTERNAL)
            val projection = arrayOf(
                MediaStore.Audio.Albums._ID,
                MediaStore.Audio.Albums.FIRST_YEAR
            )

            val selection = null
            val selectionArgs = null
            val sortOrder =  null

            context.contentResolver.query(
                collection,
                projection,
                selection,
                selectionArgs,
                sortOrder
            )?.use { cursor ->
                val songIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums._ID)
                val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.FIRST_YEAR)
                while (cursor.moveToNext()) {
                    val albumId = cursor.getLong(songIdColumn)
                    val year = cursor.getInt(titleColumn)
                    put(albumId, year)
                }
            }
        }
    }

    fun getAllSongs(context: Context): ImmutableArray<MediaItem> {
        val albumYears = getAllAlbumYears(context)
        val favoriteSongIds = getFavoriteSongIds(context)
        val ret = ImmutableArray.Builder<MediaItem>()

        val collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
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
            //MediaStore.Audio.AudioColumns.GENRE,
            //MediaStore.Audio.AudioColumns.GENRE_ID,
            MediaStore.Audio.AudioColumns.DURATION,
            MediaStore.Audio.AudioColumns.IS_FAVORITE
        )
        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"
        val selectionArgs = null
        val sortOrder =  "album ASC, track ASC"

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
            val trackColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val artistIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)
            //val genreColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.GENRE)
            //val genreIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.GENRE_ID)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val isFavoriteColumn = cursor.getColumnIndex(MediaStore.Audio.Media.IS_FAVORITE)

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

                val track = cursor.getInt(trackColumn)
                val albumId = cursor.getLong(albumIdColumn)
                val year = albumYears.getOrDefault(albumId, 0)
                val albumArt = getAlbumArt(context, albumId)
                //val genre = cursor.getString(genreColumn)
                //val genreId = cursor.getLong(genreIdColumn)
                val durationInSeconds = cursor.getInt(durationColumn) / 1000
                val mediaStoreFavorite = isFavoriteColumn != -1 && cursor.getInt(isFavoriteColumn) == 1
                val isFavorite = mediaStoreFavorite || favoriteSongIds.contains(songId.toLong())

                val bundle =  Bundle().apply {
                    putString(ALBUM, album)
                    putLong(ALBUM_ID, albumId)
                    putString(ARTIST, artist)
                    putLong(ARTIST_ID, artistId)
                    ///putString(GENRE, genre)
                    //putLong(GENRE_ID, genreId)
                    putString(MEDIA_URI, data)
                    putInt(TRACK_NUMBER, track)
                    putInt(DURATION, durationInSeconds)
                    putBoolean(IS_FAVORITE, isFavorite)
                }

                val metadata =
                    MediaMetadata.Builder()
                        .setTitle(title)
                        .setArtist(artist)
                        .setAlbumTitle(album)
                        .setAlbumArtist(artist)
                        .setArtworkUri(albumArt?.toUri())
                        //.setGenre(genre)
                        .setIsBrowsable(false)
                        .setMediaType(MediaMetadata.MEDIA_TYPE_MUSIC)
                        .setIsPlayable(true)
                        .setTrackNumber(track)
                        .setReleaseYear(year)
                        .setExtras(bundle)
                        .build()

                val item = MediaItem.Builder()
                    .setMediaId(ITEM_PREFIX+songId.toString())
                    .setMediaMetadata(metadata)
                    .setUri(data)
                    .setMimeType("audio/mpeg")
                    .build()

                ret.add(item)
            }
        }
        return ret.build()
    }

    fun toggleSongFavorite(context: Context, songId: Long, isFavorite: Boolean): Boolean {
        // 1. Persist locally to SharedPreferences so favorites are guaranteed to persist across restarts
        val prefs = context.getSharedPreferences("favorite_songs_prefs", Context.MODE_PRIVATE)
        val currentSet = prefs.getStringSet("favorite_ids", emptySet())?.toMutableSet() ?: mutableSetOf()
        if (isFavorite) {
            currentSet.add(songId.toString())
        } else {
            currentSet.remove(songId.toString())
        }
        prefs.edit { putStringSet("favorite_ids", currentSet) }

        // Emit favorite change event
        _favoriteChanges.tryEmit(songId to isFavorite)

        // 2. Also attempt to persist in MediaStore IS_FAVORITE column
        val uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, songId)
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.IS_FAVORITE, if (isFavorite) 1 else 0)
        }
        return try {
            val updatedRows = context.contentResolver.update(uri, values, null, null)
            updatedRows > 0
        } catch (e: Exception) {
            Log.e("Repository", "Error updating MediaStore IS_FAVORITE for song $songId", e)
            false
        }
    }

    private fun getAlbumArt(context: Context, albumId:Long): String? {
        val fileName = "albumArt$albumId.jpg"
        val file = File(context.cacheDir, fileName)

        // If we already have it in cache, return the content URI immediately
        if (file.exists()) {
            return "content://androidx.media3/$fileName"
        }

        val albumArtUri = ContentUris.withAppendedId(
            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
            albumId
        )

        return try {
            context.contentResolver.loadThumbnail(albumArtUri, Size(256, 256), null).let { bitmap ->
                FileOutputStream(file).use { fos ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fos)
                    fos.flush()
                }
            }
            // Return the provider URI so external apps can read it
            "content://androidx.media3/$fileName"
        } catch (_: Exception) {
            // Fallback: Return a default image from your assets folder
            // if you have one (e.g., assets/default_art.png)
            "content://androidx.media3/default_art.png"
        }
    }
}


const val ALBUM = "album"
const val ARTIST = "artist"
//const val GENRE = "genre"
const val MEDIA_URI = "mediaUri"
const val TRACK_NUMBER = "trackNumber"
const val ROOT_ID = "[rootID]"
const val ALBUM_ID = "[albumID]"
//const val GENRE_ID = "[genreID]"
const val ITEM_ID = "[itemID]"
const val ARTIST_ID = "[artistID]"
const val PLAYLIST_ID = "[playlistID]"
const val DURATION = "[duration]"
const val IS_FAVORITE = "[isFavorite]"
