package com.bdlepla.android.mymusicplayer.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AlbumDao {
    @Query("SELECT * FROM album")
    fun getAll(): List<Album>

    @Query("SELECT * FROM album WHERE id IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<Album>

    @Query("SELECT * FROM album WHERE name LIKE :name LIMIT 1")
    fun findByName(name: String): Album

    @Insert
    fun insertAll(vararg album: Album)

    @Delete
    fun delete(album: Album)
}