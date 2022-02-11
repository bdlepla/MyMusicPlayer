package com.bdlepla.android.mymusicplayer.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Song (
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo val name: String?,
    @ColumnInfo val artistId: Int,
    @ColumnInfo val albumId: Int,
)
