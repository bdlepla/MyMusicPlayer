package com.bdlepla.android.mymusicplayer.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Artist::class, Album::class, Song::class, Playlist::class, PlaylistItem::class], version = 1)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun artistDao(): ArtistDao
    abstract fun albumDao(): AlbumDao
    abstract fun songDao(): SongDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun playlistItemDao(): PlaylistItemDao
}