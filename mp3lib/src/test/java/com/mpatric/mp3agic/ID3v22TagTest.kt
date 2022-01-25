package com.mpatric.mp3agic

import com.mpatric.mp3agic.BufferTools.checkBit
import com.mpatric.mp3agic.BufferTools.setBit
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ID3v22TagTest {
    @Before
    @Throws(Exception::class)
    fun setUp() {
        inputBytes[AbstractID3v2Tag.FLAGS_OFFSET] = 0
        outputBytes[AbstractID3v2Tag.FLAGS_OFFSET] = 0
    }

    @Test
    @Throws(Exception::class)
    fun shouldUnpackAndPackOffUnsynchronizationBit() {
        val id3tag = ID3v22Tag()
        inputBytes[AbstractID3v2Tag.FLAGS_OFFSET] = setBit(ZERO, AbstractID3v2Tag.UNSYNCHRONISATION_BIT, false)
        id3tag.unpackFlags(inputBytes)
        id3tag.packFlags(outputBytes, 0)
        Assert.assertFalse(
            checkBit(
                outputBytes[AbstractID3v2Tag.FLAGS_OFFSET],
                AbstractID3v2Tag.UNSYNCHRONISATION_BIT
            )
        )
    }

    @Test
    @Throws(Exception::class)
    fun shouldUnpackAndPackOnUnsynchronizationBit() {
        val id3tag = ID3v22Tag()
        inputBytes[AbstractID3v2Tag.FLAGS_OFFSET] = setBit(ZERO, AbstractID3v2Tag.UNSYNCHRONISATION_BIT, true)
        id3tag.unpackFlags(inputBytes)
        id3tag.packFlags(outputBytes, 0)
        Assert.assertTrue(
            checkBit(
                outputBytes[AbstractID3v2Tag.FLAGS_OFFSET],
                AbstractID3v2Tag.UNSYNCHRONISATION_BIT
            )
        )
    }

    @Test
    @Throws(Exception::class)
    fun shouldUnpackAndPackOffCompressionBit() {
        val id3tag = ID3v22Tag()
        inputBytes[AbstractID3v2Tag.FLAGS_OFFSET] = setBit(ZERO, AbstractID3v2Tag.COMPRESSION_BIT, false)
        id3tag.unpackFlags(inputBytes)
        id3tag.packFlags(outputBytes, 0)
        Assert.assertFalse(checkBit(outputBytes[AbstractID3v2Tag.FLAGS_OFFSET], AbstractID3v2Tag.COMPRESSION_BIT))
    }

    @Test
    @Throws(Exception::class)
    fun shouldUnpackAndPackOnCompressionBit() {
        val id3tag = ID3v22Tag()
        inputBytes[AbstractID3v2Tag.FLAGS_OFFSET] = setBit(ZERO, AbstractID3v2Tag.COMPRESSION_BIT, true)
        id3tag.unpackFlags(inputBytes)
        id3tag.packFlags(outputBytes, 0)
        Assert.assertTrue(checkBit(outputBytes[AbstractID3v2Tag.FLAGS_OFFSET], AbstractID3v2Tag.COMPRESSION_BIT))
    }

    @Test
    @Throws(Exception::class)
    fun shouldStoreAndRetrieveItunesComment() {
        val id3tag = ID3v22Tag()
        val comment = "COMMENT"
        id3tag.itunesComment = comment
        val bytes = id3tag.toBytes()
        val newId3tag = ID3v22Tag(bytes!!)
        Assert.assertEquals(comment, newId3tag.itunesComment)
    }

    @Test
    @Throws(Exception::class)
    fun shouldStoreAndRetrieveLyrics() {
        val id3tag = ID3v22Tag()
        val lyrics = "La-la-la"
        id3tag.lyrics = lyrics
        val bytes = id3tag.toBytes()
        val newId3tag = ID3v22Tag(bytes!!)
        Assert.assertEquals(lyrics, newId3tag.lyrics)
    }

    @Test
    @Throws(Exception::class)
    fun shouldStoreAndRetrievePublisher() {
        val id3tag = ID3v22Tag()
        val publisher = "PUBLISHER"
        id3tag.publisher = publisher
        val bytes = id3tag.toBytes()
        val newId3tag = ID3v22Tag(bytes!!)
        Assert.assertEquals(publisher, newId3tag.publisher)
    }

    @Test
    @Throws(Exception::class)
    fun shouldStoreAndRetrieveKey() {
        val id3tag = ID3v22Tag()
        val key = "KEY"
        id3tag.key = key
        val bytes = id3tag.toBytes()
        val newId3tag = ID3v22Tag(bytes!!)
        Assert.assertEquals(key, newId3tag.key)
    }

    @Test
    @Throws(Exception::class)
    fun shouldStoreAndRetrieveBPM() {
        val id3tag = ID3v22Tag()
        val bpm = 8 * 44100
        id3tag.bPM = bpm
        val bytes = id3tag.toBytes()
        val newId3tag = ID3v22Tag(bytes!!)
        Assert.assertEquals(bpm.toLong(), newId3tag.bPM.toLong())
    }

    @Test
    @Throws(Exception::class)
    fun shouldStoreAndRetrieveDate() {
        val id3tag = ID3v22Tag()
        val date = "DATE"
        id3tag.date = date
        val bytes = id3tag.toBytes()
        val newId3tag = ID3v22Tag(bytes!!)
        Assert.assertEquals(date, newId3tag.date)
    }

    @Test
    @Throws(Exception::class)
    fun shouldStoreAndRetrieveAlbumArtist() {
        val id3tag = ID3v22Tag()
        val albumArtist = "ALBUMARTIST"
        id3tag.albumArtist = albumArtist
        val bytes = id3tag.toBytes()
        val newId3tag = ID3v22Tag(bytes!!)
        Assert.assertEquals(albumArtist, newId3tag.albumArtist)
    }

    @Test
    @Throws(Exception::class)
    fun shouldStoreAndRetrieveGrouping() {
        val id3tag = ID3v22Tag()
        val grouping = "GROUPING"
        id3tag.grouping = grouping
        val bytes = id3tag.toBytes()
        val newId3tag = ID3v22Tag(bytes!!)
        Assert.assertEquals(grouping, newId3tag.grouping)
    }

    @Test
    @Throws(Exception::class)
    fun shouldStoreAndRetrieveCompilation() {
        val id3tag = ID3v22Tag()
        val compilation = true
        id3tag.isCompilation = compilation
        val bytes = id3tag.toBytes()
        val newId3tag = ID3v22Tag(bytes!!)
        Assert.assertEquals(compilation, newId3tag.isCompilation)
    }

    @Test
    @Throws(Exception::class)
    fun shouldStoreAndRetrievePartOfSet() {
        val id3tag = ID3v22Tag()
        val partOfSet = "PARTOFSET"
        id3tag.partOfSet = partOfSet
        val bytes = id3tag.toBytes()
        val newId3tag = ID3v22Tag(bytes!!)
        Assert.assertEquals(partOfSet, newId3tag.partOfSet)
    }

    companion object {
        private const val ZERO: Byte = 0
        private val inputBytes = ByteArray(AbstractID3v2Tag.FLAGS_OFFSET + 1)
        private val outputBytes = ByteArray(AbstractID3v2Tag.FLAGS_OFFSET + 1)
    }
}