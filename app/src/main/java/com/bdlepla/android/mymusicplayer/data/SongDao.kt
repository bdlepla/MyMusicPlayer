package com.bdlepla.android.mymusicplayer.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SongDao {
    @Query("SELECT * FROM song")
    fun getAll(): List<Song>

    @Query("SELECT * FROM song WHERE id IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<Song>

    @Query("SELECT * FROM song WHERE name LIKE :name LIMIT 1")
    fun findByName(name: String): Song

    @Insert
    fun insertAll(vararg song: Song)

    @Delete
    fun delete(song: Song)
}