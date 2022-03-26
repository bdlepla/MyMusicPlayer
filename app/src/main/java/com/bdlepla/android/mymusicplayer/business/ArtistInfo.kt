package com.bdlepla.android.mymusicplayer.business

data class ArtistInfo(val name: String, val artistId: Long) {
    override fun equals(other: Any?): Boolean {
        val otherArtist = other as? ArtistInfo ?: return false
        return artistId == otherArtist.artistId
    }

    override fun hashCode(): Int = artistId.hashCode()
}