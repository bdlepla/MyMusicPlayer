package com.mpatric.mp3agic

import org.junit.Assert
import org.junit.Test

class ID3v24TagTest {
    @Test
    @Throws(Exception::class)
    fun shouldStoreAndRetrieveRecordingTime() {
        val id3tag = ID3v24Tag()
        val recordingTime = "01/01/2011 00:00:00"
        id3tag.recordingTime = recordingTime
        val bytes = id3tag.toBytes()
        val newId3tag = ID3v24Tag(bytes!!)
        Assert.assertEquals(recordingTime, newId3tag.recordingTime)
    }

    @Test
    @Throws(Exception::class)
    fun shouldSetGenreDescription() {
        val id3tag = ID3v24Tag()
        val genreDescription = "?????"
        id3tag.genreDescription = genreDescription
        val bytes = id3tag.toBytes()
        val newId3tag = ID3v24Tag(bytes!!)
        Assert.assertTrue(genreDescription, newId3tag.frameSets!!.containsKey(AbstractID3v2Tag.ID_GENRE))
        val frames = newId3tag.frameSets!![AbstractID3v2Tag.ID_GENRE]!!
            .frames
        Assert.assertEquals(1, frames.size.toLong())
        val frameData = ID3v2TextFrameData(id3tag.unsynchronisation, frames[0].data)
        Assert.assertEquals(genreDescription, frameData.text.toString())
    }
}