package com.bdlepla.android.mymusicplayer.business

import com.danrusu.pods4k.immutableArrays.ImmutableArray
import com.danrusu.pods4k.immutableArrays.emptyImmutableArray
import com.danrusu.pods4k.immutableArrays.multiplicativeSpecializations.flatMap

data class ArtistInfo(val name: String, val artistId: Long, val albumArt:String?=null) {

    private var immutableAlbums: ImmutableArray<AlbumInfo> = emptyImmutableArray()
    fun addAlbums(albums:ImmutableArray<AlbumInfo>) {
        immutableAlbums = albums
    }

    val albums
        get () = immutableAlbums

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