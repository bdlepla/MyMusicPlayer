package com.mpatric.mp3agic

import junit.framework.Assert.assertEquals
import org.junit.Assert
import org.junit.Assert.assertArrayEquals
import org.junit.Test

class ID3v2ObseletePictureFrameDataTest {
    @Test
    @Throws(Exception::class)
    fun shouldConsiderTwoEquivalentObjectsEqual() {
        val frameData1 = ID3v2ObseletePictureFrameData(
            false, TEST_MIME_TYPE, 0.toByte(), EncodedText(
                1.toByte(), TEST_DESCRIPTION
            ), DUMMY_IMAGE_DATA
        )
        val frameData2 = ID3v2ObseletePictureFrameData(
            false, TEST_MIME_TYPE, 0.toByte(), EncodedText(
                1.toByte(), TEST_DESCRIPTION
            ), DUMMY_IMAGE_DATA
        )
        Assert.assertEquals(frameData1, frameData2)
    }

    @Test
    @Throws(Exception::class)
    fun shouldReadFrameData() {
        val bytes = byteArrayOf(
            0x00,
            'P'.toByte(),
            'N'.toByte(),
            'G'.toByte(),
            0x01,
            'D'.toByte(),
            'E'.toByte(),
            'S'.toByte(),
            'C'.toByte(),
            'R'.toByte(),
            'I'.toByte(),
            'P'.toByte(),
            'T'.toByte(),
            'I'.toByte(),
            'O'.toByte(),
            'N'.toByte(),
            0x00,
            1,
            2,
            3,
            4,
            5
        )
        val frameData = ID3v2ObseletePictureFrameData(false, bytes)
        assertEquals(TEST_MIME_TYPE, frameData.mimeType)
        assertEquals(1.toByte(), frameData.pictureType)
        Assert.assertEquals(EncodedText(0.toByte(), TEST_DESCRIPTION), frameData.description)
        assertArrayEquals(DUMMY_IMAGE_DATA, frameData.imageData)
    }

    @Test
    @Throws(Exception::class)
    fun shouldReadFrameDataWithUnicodeDescription() {
        val bytes = byteArrayOf(
            0x01,
            'P'.toByte(),
            'N'.toByte(),
            'G'.toByte(),
            0x01,
            0xff.toByte(),
            0xfe.toByte(),
            0xb3.toByte(),
            0x03,
            0xb5.toByte(),
            0x03,
            0xb9.toByte(),
            0x03,
            0xac.toByte(),
            0x03,
            0,
            0,
            1,
            2,
            3,
            4,
            5
        )
        val frameData = ID3v2ObseletePictureFrameData(false, bytes)
        assertEquals(TEST_MIME_TYPE, frameData.mimeType)
        assertEquals(1.toByte(), frameData.pictureType)
        Assert.assertEquals(
            EncodedText(EncodedText.TEXT_ENCODING_UTF_16, TEST_DESCRIPTION_UNICODE),
            frameData.description
        )
        assertArrayEquals(DUMMY_IMAGE_DATA, frameData.imageData)
    }

    companion object {
        private const val TEST_MIME_TYPE = "image/png"
        private const val TEST_DESCRIPTION = "DESCRIPTION"
        private const val TEST_DESCRIPTION_UNICODE = "\u03B3\u03B5\u03B9\u03AC"
        private val DUMMY_IMAGE_DATA = byteArrayOf(1, 2, 3, 4, 5)
    }
}