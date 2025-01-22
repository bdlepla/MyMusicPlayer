package com.bdlepla.android.mymusicplayer.business

import com.danrusu.pods4k.immutableArrays.ImmutableArray
import com.danrusu.pods4k.immutableArrays.emptyImmutableArray


data class AlbumInfo(val name: String,
                     val albumYear: Int,
                     val albumId: Long,
                     val artistId:Long,
                     val albumArt: String?=null) {

    fun addSongs(songs: ImmutableArray<SongInfo>) {
        immutableSongs = songs
    }

    private var immutableSongs:ImmutableArray<SongInfo> = emptyImmutableArray()
    val songs
        get() = immutableSongs // might need to work to remove songs

    override fun equals(other: Any?): Boolean {
        val otherAlbum = other as? AlbumInfo ?: return false
        return otherAlbum.albumId == albumId
    }

    override fun hashCode(): Int = albumId.hashCode()
}