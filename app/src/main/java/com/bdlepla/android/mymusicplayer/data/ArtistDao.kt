package com.bdlepla.android.mymusicplayer.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ArtistDao {
    @Query("SELECT * FROM artist")
    fun getAll(): List<Artist>

    @Query("SELECT * FROM artist WHERE id IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<Artist>

    @Query("SELECT * FROM artist WHERE name LIKE :name LIMIT 1")
    fun findByName(name: String): Artist

    @Insert
    fun insertAll(vararg artist: Artist)

    @Delete
    fun delete(artist: Artist)
}