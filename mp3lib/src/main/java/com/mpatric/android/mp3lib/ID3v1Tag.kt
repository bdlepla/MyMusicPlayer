package com.mpatric.mp3agic

import java.io.UnsupportedEncodingException
import java.util.*

class ID3v1Tag : ID3v1 {
    override var track: String? = null
    override var artist: String? = null
    override var title: String? = null
    override var album: String? = null
    override var year: String? = null
    override var genre = -1
    override var comment: String? = null

    constructor() {}
    constructor(bytes: ByteArray) {
        unpackTag(bytes)
    }

    @Throws(NoSuchTagException::class)
    private fun unpackTag(bytes: ByteArray) {
        sanityCheckTag(bytes)
        title = BufferTools.trimStringRight(
            BufferTools.byteBufferToStringIgnoringEncodingIssues(
                bytes,
                TITLE_OFFSET,
                TITLE_LENGTH
            )
        )
        artist = BufferTools.trimStringRight(
            BufferTools.byteBufferToStringIgnoringEncodingIssues(
                bytes,
                ARTIST_OFFSET,
                ARTIST_LENGTH
            )
        )
        album = BufferTools.trimStringRight(
            BufferTools.byteBufferToStringIgnoringEncodingIssues(
                bytes,
                ALBUM_OFFSET,
                ALBUM_LENGTH
            )
        )
        year = BufferTools.trimStringRight(
            BufferTools.byteBufferToStringIgnoringEncodingIssues(
                bytes,
                YEAR_OFFSET,
                YEAR_LENGTH
            )
        )
        val genreByte = bytes[GENRE_OFFSET]
        genre = genreByte.toInt() and 0xff
        if (genre == 0xFF) {
            genre = -1
        }
        if (bytes[TRACK_MARKER_OFFSET] != 0.toByte()) {
            comment = BufferTools.trimStringRight(
                BufferTools.byteBufferToStringIgnoringEncodingIssues(
                    bytes,
                    COMMENT_OFFSET,
                    COMMENT_LENGTH_V1_0
                )
            )
            track = null
        } else {
            comment = BufferTools.trimStringRight(
                BufferTools.byteBufferToStringIgnoringEncodingIssues(
                    bytes,
                    COMMENT_OFFSET,
                    COMMENT_LENGTH_V1_1
                )
            )
            val trackInt: Int = bytes[TRACK_OFFSET].toInt()
            track = if (trackInt == 0) {
                ""
            } else {
                Integer.toString(trackInt)
            }
        }
    }

    @Throws(NoSuchTagException::class)
    private fun sanityCheckTag(bytes: ByteArray) {
        if (bytes.size != TAG_LENGTH) {
            throw NoSuchTagException("Buffer length wrong")
        }
        if (TAG != BufferTools.byteBufferToStringIgnoringEncodingIssues(
                bytes,
                0,
                TAG.length
            )
        ) {
            throw NoSuchTagException()
        }
    }

    override fun toBytes(): ByteArray {
        val bytes = ByteArray(TAG_LENGTH)
        packTag(bytes)
        return bytes
    }

    fun toBytes(bytes: ByteArray) {
        packTag(bytes)
    }

    fun packTag(bytes: ByteArray) {
        Arrays.fill(bytes, 0.toByte())
        try {
            BufferTools.stringIntoByteBuffer(TAG, 0, 3, bytes, 0)
        } catch (e: UnsupportedEncodingException) {
        }
        packField(bytes, title, TITLE_LENGTH, TITLE_OFFSET)
        packField(bytes, artist, ARTIST_LENGTH, ARTIST_OFFSET)
        packField(bytes, album, ALBUM_LENGTH, ALBUM_OFFSET)
        packField(bytes, year, YEAR_LENGTH, YEAR_OFFSET)
        if (genre < 128) {
            bytes[GENRE_OFFSET] = genre.toByte()
        } else {
            bytes[GENRE_OFFSET] = (genre - 256).toByte()
        }
        if (track == null) {
            packField(bytes, comment, COMMENT_LENGTH_V1_0, COMMENT_OFFSET)
        } else {
            packField(bytes, comment, COMMENT_LENGTH_V1_1, COMMENT_OFFSET)
            val trackTemp = numericsOnly(track!!)
            if (trackTemp.length > 0) {
                val trackInt = trackTemp.toInt()
                if (trackInt < 128) {
                    bytes[TRACK_OFFSET] = trackInt.toByte()
                } else {
                    bytes[TRACK_OFFSET] = (trackInt - 256).toByte()
                }
            }
        }
    }

    private fun packField(bytes: ByteArray, value: String?, maxLength: Int, offset: Int) {
        if (value != null) {
            try {
                BufferTools.stringIntoByteBuffer(
                    value,
                    0,
                    Math.min(value.length, maxLength),
                    bytes,
                    offset
                )
            } catch (e: UnsupportedEncodingException) {
            }
        }
    }

    private fun numericsOnly(s: String): String {
        val stringBuffer = StringBuilder()
        for (i in 0 until s.length) {
            val ch = s[i]
            if (ch >= '0' && ch <= '9') {
                stringBuffer.append(ch)
            } else {
                break
            }
        }
        return stringBuffer.toString()
    }

    override val version: String
        get() = if (track == null) {
            VERSION_0
        } else {
            VERSION_1
        }
    override var genreDescription:String?
        get() = try {
            ID3v1Genres.GENRES.get(genre)
        } catch (e: ArrayIndexOutOfBoundsException) {
            "Unknown"
        }
        set(value){}

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + if (album == null) 0 else album.hashCode()
        result = prime * result + if (artist == null) 0 else artist.hashCode()
        result = prime * result + if (comment == null) 0 else comment.hashCode()
        result = prime * result + genre
        result = prime * result + if (title == null) 0 else title.hashCode()
        result = prime * result + if (track == null) 0 else track.hashCode()
        result = prime * result + if (year == null) 0 else year.hashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (javaClass != other.javaClass) return false
        val other2 = other as ID3v1Tag
        if (album == null) {
            if (other2.album != null) return false
        } else if (album != other2.album) return false
        if (artist == null) {
            if (other2.artist != null) return false
        } else if (artist != other2.artist) return false
        if (comment == null) {
            if (other2.comment != null) return false
        } else if (comment != other2.comment) return false
        if (genre != other2.genre) return false
        if (title == null) {
            if (other2.title != null) return false
        } else if (title != other2.title) return false
        if (track == null) {
            if (other2.track != null) return false
        } else if (track != other2.track) return false
        if (year == null) {
            if (other2.year != null) return false
        } else if (year != other2.year) return false
        return true
    }

    companion object {
        const val TAG_LENGTH = 128
        private const val VERSION_0 = "0"
        private const val VERSION_1 = "1"
        private const val TAG = "TAG"
        private const val TITLE_OFFSET = 3
        private const val TITLE_LENGTH = 30
        private const val ARTIST_OFFSET = 33
        private const val ARTIST_LENGTH = 30
        private const val ALBUM_OFFSET = 63
        private const val ALBUM_LENGTH = 30
        private const val YEAR_OFFSET = 93
        private const val YEAR_LENGTH = 4
        private const val COMMENT_OFFSET = 97
        private const val COMMENT_LENGTH_V1_0 = 30
        private const val COMMENT_LENGTH_V1_1 = 28
        private const val TRACK_MARKER_OFFSET = 125
        private const val TRACK_OFFSET = 126
        private const val GENRE_OFFSET = 127
    }
}