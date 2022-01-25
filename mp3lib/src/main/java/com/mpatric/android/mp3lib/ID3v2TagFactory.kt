package com.mpatric.mp3agic

import com.mpatric.mp3agic.NoSuchTagException
import com.mpatric.mp3agic.UnsupportedTagException

object ID3v2TagFactory {
    @JvmStatic
    @Throws(NoSuchTagException::class, UnsupportedTagException::class, InvalidDataException::class)
    fun createTag(bytes: ByteArray): AbstractID3v2Tag {
        sanityCheckTag(bytes)
        val majorVersion = bytes[AbstractID3v2Tag.MAJOR_VERSION_OFFSET].toInt()
        when (majorVersion) {
            2 -> return createID3v22Tag(bytes)
            3 -> return ID3v23Tag(bytes)
            4 -> return ID3v24Tag(bytes)
        }
        throw UnsupportedTagException("Tag version not supported")
    }

    @Throws(NoSuchTagException::class, UnsupportedTagException::class, InvalidDataException::class)
    private fun createID3v22Tag(bytes: ByteArray): AbstractID3v2Tag {
        var tag = ID3v22Tag(bytes)
        if (tag.frameSets != null && tag.frameSets!!.isEmpty()) {
            tag = ID3v22Tag(bytes, true)
        }
        return tag
    }

    @JvmStatic
    @Throws(NoSuchTagException::class, UnsupportedTagException::class)
    fun sanityCheckTag(bytes: ByteArray) {
        if (bytes.size < AbstractID3v2Tag.HEADER_LENGTH) {
            throw NoSuchTagException("Buffer too short")
        }
        if (AbstractID3v2Tag.TAG != BufferTools.byteBufferToStringIgnoringEncodingIssues(
                bytes,
                0,
                AbstractID3v2Tag.TAG.length
            )
        ) {
            throw NoSuchTagException()
        }
        val majorVersion = bytes[AbstractID3v2Tag.MAJOR_VERSION_OFFSET].toInt()
        if (majorVersion != 2 && majorVersion != 3 && majorVersion != 4) {
            val minorVersion = bytes[AbstractID3v2Tag.MINOR_VERSION_OFFSET].toInt()
            throw UnsupportedTagException("Unsupported version 2.$majorVersion.$minorVersion")
        }
    }
}