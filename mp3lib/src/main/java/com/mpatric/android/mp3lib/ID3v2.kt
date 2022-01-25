package com.mpatric.mp3agic

import java.util.*

interface ID3v2 : ID3v1 {

    var footer: Boolean
    var unsynchronisation: Boolean
    var bPM: Int
    var grouping: String?
    var key: String?
    var date: String?
    var composer: String?
    var publisher: String?
    var originalArtist: String?
    var albumArtist: String?
    var copyright: String?
    var artistUrl: String?
    var commercialUrl: String?
    var copyrightUrl: String?
    var audiofileUrl: String?
    var audioSourceUrl: String?
    var radiostationUrl: String?
    var paymentUrl: String?
    var publisherUrl: String?
    var url: String?
    var partOfSet: String?
    var isCompilation: Boolean
    var chapters: ArrayList<ID3v2ChapterFrameData>?
    var chapterTOC: ArrayList<ID3v2ChapterTOCFrameData>?
    var encoder: String?
    val albumImage: ByteArray?
    fun setAlbumImage(albumImage: ByteArray?, mimeType: String?)
    fun setAlbumImage(
        albumImage: ByteArray?,
        mimeType: String?,
        imageType: Byte,
        imageDescription: String?
    )

    fun clearAlbumImage()
    val albumImageMimeType: String?
    var wmpRating: Int
    var itunesComment: String?
    var lyrics: String?

    val dataLength: Int
    val length: Int
    val obsoleteFormat: Boolean
    val frameSets: Map<String?, ID3v2FrameSet?>?
    fun clearFrameSet(id: String?)
    fun setPadding(padding: Boolean)
    fun getPadding():Boolean
}