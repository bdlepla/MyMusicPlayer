package com.mpatric.mp3agic

import com.mpatric.mp3agic.NotSupportedException

interface ID3v1 {
    val version: String?
    var track: String?
    var artist: String?
    var title: String?
    var album: String?
    var year: String?
    var genre: Int
    var genreDescription: String?
    var comment: String?

    @Throws(NotSupportedException::class)
    fun toBytes(): ByteArray?
}