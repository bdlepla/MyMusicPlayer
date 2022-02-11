package com.bdlepla.android.mymusicplayer.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PlaylistItemDao {
    @Query("SELECT * FROM playlistItem")
    fun getAll(): List<PlaylistItem>

    @Query("SELECT * FROM playlistItem WHERE id IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<PlaylistItem>

    @Query("SELECT * FROM playlistItem WHERE playlistId = :playlistId")
    fun findByPlaylistId(playlistId: Int): List<PlaylistItem>

    @Insert
    fun insertAll(vararg playlist: Playlist)

    @Delete
    fun delete(playlist: Playlist)
}