package com.bdlepla.android.mymusicplayer.business

data class ArtistInfo(val name: String, val artistId: Long, val anAlbum:AlbumInfo?=null) {

    val albums = mutableListOf<AlbumInfo>()
    val albumsByYear
        get () = albums.sortedBy { album -> album.albumYear }

    val songs
        get() = albumsByYear.flatMap{album -> album.songs}

    override fun equals(other: Any?): Boolean {
        val otherArtist = other as? ArtistInfo ?: return false
        return artistId == otherArtist.artistId
    }

    override fun hashCode(): Int = artistId.hashCode()
}