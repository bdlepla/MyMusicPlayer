package com.bdlepla.android.mymusicplayer.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PlaylistDao {
    @Query("SELECT * FROM playlist")
    fun getAll(): List<Playlist>

    @Query("SELECT * FROM playlist WHERE id IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<Playlist>

    @Query("SELECT * FROM playlist WHERE name LIKE :name LIMIT 1")
    fun findByName(name: String): Playlist

    @Insert
    fun insertAll(vararg playlist: Playlist)

    @Delete
    fun delete(playlist: Playlist)
}