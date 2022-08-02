package com.bdlepla.android.mymusicplayer.business

data class AlbumInfo(val name: String,
                     val albumYear: Int,
                     val albumId: Long,
                     val artistId:Long,
                     val albumArt: String?=null) {

    override fun equals(other: Any?): Boolean {
        val otherAlbum = other as? AlbumInfo ?: return false
        return otherAlbum.albumId == albumId
    }

    override fun hashCode(): Int = albumId.hashCode()
}