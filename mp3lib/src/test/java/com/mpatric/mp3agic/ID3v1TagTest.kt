package com.mpatric.mp3agic

import com.mpatric.mp3agic.BufferTools.copyBuffer
import com.mpatric.mp3agic.BufferTools.stringToByteBuffer
import com.mpatric.mp3agic.NoSuchTagException
import org.junit.Assert
import org.junit.Test

class ID3v1TagTest {
    @Test(expected = NoSuchTagException::class)
    @Throws(NoSuchTagException::class)
    fun shouldThrowExceptionForTagBufferTooShort() {
        val buffer = ByteArray(ID3v1Tag.TAG_LENGTH - 1)
        ID3v1Tag(buffer)
    }

    @Test(expected = NoSuchTagException::class)
    @Throws(NoSuchTagException::class)
    fun shouldThrowExceptionForTagBufferTooLong() {
        val buffer = ByteArray(ID3v1Tag.TAG_LENGTH + 1)
        ID3v1Tag(buffer)
    }

    @Test
    @Throws(Exception::class)
    fun shouldExtractMaximumLengthFieldsFromValid10Tag() {
        val buffer = stringToByteBuffer(VALID_TAG, 0, VALID_TAG.length)
        buffer[buffer.size - 1] = -0x6D // 0x93 as a signed byte
        val id3v1tag = ID3v1Tag(buffer)
        Assert.assertEquals("TITLE1234567890123456789012345", id3v1tag.title)
        Assert.assertEquals("ARTIST123456789012345678901234", id3v1tag.artist)
        Assert.assertEquals("ALBUM1234567890123456789012345", id3v1tag.album)
        Assert.assertEquals("2001", id3v1tag.year)
        Assert.assertEquals("COMMENT12345678901234567890123", id3v1tag.comment)
        Assert.assertEquals(null, id3v1tag.track)
        Assert.assertEquals(0x93, id3v1tag.genre)
        Assert.assertEquals("Synthpop", id3v1tag.genreDescription)
    }

    @Test
    @Throws(Exception::class)
    fun shouldExtractMaximumLengthFieldsFromValid11Tag() {
        val buffer = stringToByteBuffer(VALID_TAG, 0, VALID_TAG.length)
        buffer[buffer.size - 3] = 0x00
        buffer[buffer.size - 2] = 0x01
        buffer[buffer.size - 1] = 0x0D
        val id3v1tag = ID3v1Tag(buffer)
        Assert.assertEquals("TITLE1234567890123456789012345", id3v1tag.title)
        Assert.assertEquals("ARTIST123456789012345678901234", id3v1tag.artist)
        Assert.assertEquals("ALBUM1234567890123456789012345", id3v1tag.album)
        Assert.assertEquals("2001", id3v1tag.year)
        Assert.assertEquals("COMMENT123456789012345678901", id3v1tag.comment)
        Assert.assertEquals("1", id3v1tag.track)
        Assert.assertEquals(0x0d, id3v1tag.genre)
        Assert.assertEquals("Pop", id3v1tag.genreDescription)
    }

    @Test
    @Throws(Exception::class)
    fun shouldExtractTrimmedFieldsFromValid11TagWithWhitespace() {
        val buffer =
            stringToByteBuffer(VALID_TAG_WITH_WHITESPACE, 0, VALID_TAG_WITH_WHITESPACE.length)
        buffer[buffer.size - 3] = 0x00
        buffer[buffer.size - 2] = 0x01
        buffer[buffer.size - 1] = 0x0D
        val id3v1tag = ID3v1Tag(buffer)
        Assert.assertEquals("TITLE", id3v1tag.title)
        Assert.assertEquals("ARTIST", id3v1tag.artist)
        Assert.assertEquals("ALBUM", id3v1tag.album)
        Assert.assertEquals("2001", id3v1tag.year)
        Assert.assertEquals("COMMENT", id3v1tag.comment)
        Assert.assertEquals("1", id3v1tag.track)
        Assert.assertEquals(0x0d, id3v1tag.genre)
        Assert.assertEquals("Pop", id3v1tag.genreDescription)
    }

    @Test
    @Throws(Exception::class)
    fun shouldExtractTrimmedFieldsFromValid11TagWithNullspace() {
        val buffer =
            stringToByteBuffer(VALID_TAG_WITH_WHITESPACE, 0, VALID_TAG_WITH_WHITESPACE.length)
        TestHelper.replaceSpacesWithNulls(buffer)
        buffer[buffer.size - 3] = 0x00
        buffer[buffer.size - 2] = 0x01
        buffer[buffer.size - 1] = 0x0D
        val id3v1tag = ID3v1Tag(buffer)
        Assert.assertEquals("TITLE", id3v1tag.title)
        Assert.assertEquals("ARTIST", id3v1tag.artist)
        Assert.assertEquals("ALBUM", id3v1tag.album)
        Assert.assertEquals("2001", id3v1tag.year)
        Assert.assertEquals("COMMENT", id3v1tag.comment)
        Assert.assertEquals("1", id3v1tag.track)
        Assert.assertEquals(0x0d, id3v1tag.genre)
        Assert.assertEquals("Pop", id3v1tag.genreDescription)
    }

    @Test
    @Throws(Exception::class)
    fun shouldGenerateValidTagBuffer() {
        val id3v1tag = ID3v1Tag()
        id3v1tag.title = "TITLE"
        id3v1tag.artist = "ARTIST"
        id3v1tag.album = "ALBUM"
        id3v1tag.year = "2001"
        id3v1tag.comment = "COMMENT"
        id3v1tag.track = "1"
        id3v1tag.genre = 0x0d
        val expectedBuffer =
            stringToByteBuffer(VALID_TAG_WITH_WHITESPACE, 0, VALID_TAG_WITH_WHITESPACE.length)
        TestHelper.replaceSpacesWithNulls(expectedBuffer)
        expectedBuffer[expectedBuffer.size - 3] = 0x00
        expectedBuffer[expectedBuffer.size - 2] = 0x01
        expectedBuffer[expectedBuffer.size - 1] = 0x0D
        Assert.assertArrayEquals(expectedBuffer, id3v1tag.toBytes())
    }

    @Test
    @Throws(Exception::class)
    fun shouldGenerateValidTagBufferWithHighGenreAndTrackNumber() {
        val id3v1tag = ID3v1Tag()
        id3v1tag.title = "TITLE"
        id3v1tag.artist = "ARTIST"
        id3v1tag.album = "ALBUM"
        id3v1tag.year = "2001"
        id3v1tag.comment = "COMMENT"
        id3v1tag.track = "254"
        id3v1tag.genre = 0x8d
        val expectedBuffer =
            stringToByteBuffer(VALID_TAG_WITH_WHITESPACE, 0, VALID_TAG_WITH_WHITESPACE.length)
        TestHelper.replaceSpacesWithNulls(expectedBuffer)
        expectedBuffer[expectedBuffer.size - 3] = 0x00
        expectedBuffer[expectedBuffer.size - 2] = -0x02 // 254 as a signed byte
        expectedBuffer[expectedBuffer.size - 1] = -0x73 // 0x8D as a signed byte
        Assert.assertArrayEquals(expectedBuffer, id3v1tag.toBytes())
    }

    @Test
    @Throws(Exception::class)
    fun shouldReadTagFieldsFromMp3() {
        val buffer = TestHelper.loadFile("src/test/resources/v1andv23tags.mp3")
        val tagBuffer = copyBuffer(buffer, buffer.size - ID3v1Tag.TAG_LENGTH, ID3v1Tag.TAG_LENGTH)
        val id3tag: ID3v1 = ID3v1Tag(tagBuffer)
        Assert.assertEquals("1", id3tag.track)
        Assert.assertEquals("ARTIST123456789012345678901234", id3tag.artist)
        Assert.assertEquals("TITLE1234567890123456789012345", id3tag.title)
        Assert.assertEquals("ALBUM1234567890123456789012345", id3tag.album)
        Assert.assertEquals("2001", id3tag.year)
        Assert.assertEquals(0x0d, id3tag.genre.toLong())
        Assert.assertEquals("Pop", id3tag.genreDescription)
        Assert.assertEquals("COMMENT123456789012345678901", id3tag.comment)
    }

    @Test
    @Throws(Exception::class)
    fun shouldConvertTagToBytesAndBackToEquivalentTag() {
        val id3tag: ID3v1 = ID3v1Tag()
        id3tag.track = "5"
        id3tag.artist = "ARTIST"
        id3tag.title = "TITLE"
        id3tag.album = "ALBUM"
        id3tag.year = "1997"
        id3tag.genre = 13
        id3tag.comment = "COMMENT"
        val data = id3tag.toBytes()
        val id3tagCopy: ID3v1 = ID3v1Tag(data!!)
        Assert.assertEquals(id3tag, id3tagCopy)
    }

    @Test
    @Throws(Exception::class)
    fun shouldReturnEmptyTrackIfNotSetOn11Tag() {
        val buffer = TestHelper.loadFile("src/test/resources/v1tagwithnotrack.mp3")
        val tagBuffer = copyBuffer(buffer, buffer.size - ID3v1Tag.TAG_LENGTH, ID3v1Tag.TAG_LENGTH)
        val id3tag: ID3v1 = ID3v1Tag(tagBuffer)
        Assert.assertEquals("", id3tag.track)
    }

    companion object {
        private const val VALID_TAG =
            "TAGTITLE1234567890123456789012345ARTIST123456789012345678901234ALBUM12345678901234567890123452001COMMENT123456789012345678901234"
        private const val VALID_TAG_WITH_WHITESPACE =
            "TAGTITLE                         ARTIST                        ALBUM                         2001COMMENT                        "
    }
}