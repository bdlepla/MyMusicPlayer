package com.mpatric.mp3agic

import org.junit.Assert
import org.junit.Test

class ID3v2UrlFrameDataTest {
    @Test
    @Throws(Exception::class)
    fun equalsItself() {
        val frameData =
            ID3v2UrlFrameData(false, EncodedText(0.toByte(), TEST_DESCRIPTION), TEST_URL)
        Assert.assertEquals(frameData, frameData)
    }

    @Test
    @Throws(Exception::class)
    fun notEqualToNull() {
        val frameData =
            ID3v2UrlFrameData(false, EncodedText(0.toByte(), TEST_DESCRIPTION), TEST_URL)
        Assert.assertFalse(frameData.equals(null))
    }

    @Test
    fun notEqualToDifferentClass() {
        val frameData =
            ID3v2UrlFrameData(false, EncodedText(0.toByte(), TEST_DESCRIPTION), TEST_URL)
        Assert.assertFalse(frameData.equals("8"))
    }

    @Test
    @Throws(Exception::class)
    fun shouldConsiderTwoEquivalentObjectsEqual() {
        val frameData1 =
            ID3v2UrlFrameData(false, EncodedText(0.toByte(), TEST_DESCRIPTION), TEST_URL)
        val frameData2 =
            ID3v2UrlFrameData(false, EncodedText(0.toByte(), TEST_DESCRIPTION), TEST_URL)
        Assert.assertEquals(frameData1, frameData2)
    }

    @Test
    fun notEqualIfUnsynchronizationNotEqual() {
        val frameData1 =
            ID3v2UrlFrameData(false, EncodedText(0.toByte(), TEST_DESCRIPTION), TEST_URL)
        val frameData2 =
            ID3v2UrlFrameData(true, EncodedText(0.toByte(), TEST_DESCRIPTION), TEST_URL)
        Assert.assertNotEquals(frameData1, frameData2)
    }

    @Test
    fun notEqualIfDescriptionNotEqual() {
        val frameData1 =
            ID3v2UrlFrameData(false, EncodedText(0.toByte(), TEST_DESCRIPTION), TEST_URL)
        val frameData2 =
            ID3v2UrlFrameData(false, EncodedText(0.toByte(), "other description"), TEST_URL)
        Assert.assertNotEquals(frameData1, frameData2)
    }

    @Test
    fun notEqualIfDescriptionIsNullOnOne() {
        val frameData1 = ID3v2UrlFrameData(false, null, TEST_URL)
        val frameData2 =
            ID3v2UrlFrameData(false, EncodedText(0.toByte(), TEST_DESCRIPTION), TEST_URL)
        Assert.assertNotEquals(frameData1, frameData2)
    }

    @Test
    fun equalIfDescriptionIsNullOnBoth() {
        val frameData1 = ID3v2UrlFrameData(false, null, TEST_URL)
        val frameData2 = ID3v2UrlFrameData(false, null, TEST_URL)
        Assert.assertEquals(frameData1, frameData2)
    }

    @Test
    fun notEqualIfUrlNotEqual() {
        val frameData1 =
            ID3v2UrlFrameData(false, EncodedText(0.toByte(), TEST_DESCRIPTION), TEST_URL)
        val frameData2 =
            ID3v2UrlFrameData(false, EncodedText(0.toByte(), TEST_DESCRIPTION), "other url")
        Assert.assertNotEquals(frameData1, frameData2)
    }

    @Test
    fun notEqualIfIdUrlNullOnOne() {
        val frameData1 = ID3v2UrlFrameData(false, EncodedText(0.toByte(), TEST_DESCRIPTION), null)
        val frameData2 =
            ID3v2UrlFrameData(false, EncodedText(0.toByte(), TEST_DESCRIPTION), TEST_URL)
        Assert.assertNotEquals(frameData1, frameData2)
    }

    @Test
    fun equalIfUrlIsNullOnBoth() {
        val frameData1 = ID3v2UrlFrameData(false, EncodedText(0.toByte(), TEST_DESCRIPTION), null)
        val frameData2 = ID3v2UrlFrameData(false, EncodedText(0.toByte(), TEST_DESCRIPTION), null)
        Assert.assertEquals(frameData1, frameData2)
    }

    @Test
    fun hashCodeIsConsistent() {
        val frameData =
            ID3v2UrlFrameData(false, EncodedText(0.toByte(), TEST_DESCRIPTION), TEST_URL)
        Assert.assertEquals(frameData.hashCode().toLong(), frameData.hashCode().toLong())
    }

    @Test
    fun equalObjectsHaveSameHashCode() {
        val frameData =
            ID3v2UrlFrameData(false, EncodedText(0.toByte(), TEST_DESCRIPTION), TEST_URL)
        val frameDataAgain =
            ID3v2UrlFrameData(false, EncodedText(0.toByte(), TEST_DESCRIPTION), TEST_URL)
        Assert.assertEquals(frameData.hashCode().toLong(), frameDataAgain.hashCode().toLong())
    }

    @Test
    @Throws(Exception::class)
    fun shouldConvertFrameDataToBytesAndBackToEquivalentObject() {
        val frameData =
            ID3v2UrlFrameData(false, EncodedText(0.toByte(), TEST_DESCRIPTION), TEST_URL)
        val bytes = frameData.toBytes()
        val expectedBytes = byteArrayOf(
            0,
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
            0,
            'h'.toByte(),
            't'.toByte(),
            't'.toByte(),
            'p'.toByte(),
            ':'.toByte(),
            '/'.toByte(),
            '/'.toByte(),
            'A'.toByte(),
            'B'.toByte(),
            'C'.toByte(),
            'D'.toByte(),
            'E'.toByte(),
            'F'.toByte(),
            'G'.toByte(),
            'H'.toByte(),
            'I'.toByte(),
            'J'.toByte(),
            'K'.toByte(),
            'L'.toByte(),
            'M'.toByte(),
            'N'.toByte(),
            'O'.toByte(),
            'P'.toByte(),
            'Q'.toByte()
        )
        Assert.assertArrayEquals(expectedBytes, bytes)
        val frameDataCopy = ID3v2UrlFrameData(false, bytes)
        Assert.assertEquals(frameData, frameDataCopy)
    }

    @Test
    @Throws(Exception::class)
    fun shouldConvertFrameDataWithNoDescriptionToBytesAndBackToEquivalentObject() {
        val frameData = ID3v2UrlFrameData(false, EncodedText(""), TEST_URL)
        val bytes = frameData.toBytes()
        val expectedBytes = byteArrayOf(
            0,
            0,
            'h'.toByte(),
            't'.toByte(),
            't'.toByte(),
            'p'.toByte(),
            ':'.toByte(),
            '/'.toByte(),
            '/'.toByte(),
            'A'.toByte(),
            'B'.toByte(),
            'C'.toByte(),
            'D'.toByte(),
            'E'.toByte(),
            'F'.toByte(),
            'G'.toByte(),
            'H'.toByte(),
            'I'.toByte(),
            'J'.toByte(),
            'K'.toByte(),
            'L'.toByte(),
            'M'.toByte(),
            'N'.toByte(),
            'O'.toByte(),
            'P'.toByte(),
            'Q'.toByte()
        )
        Assert.assertArrayEquals(expectedBytes, bytes)
        val frameDataCopy = ID3v2UrlFrameData(false, bytes)
        Assert.assertEquals(frameData, frameDataCopy)
    }

    @Test
    @Throws(Exception::class)
    fun shouldConvertFrameDataWithUnicodeDescriptionToBytesAndBackToEquivalentObject() {
        val frameData = ID3v2UrlFrameData(
            false,
            EncodedText(EncodedText.TEXT_ENCODING_UTF_16, TEST_DESCRIPTION_UNICODE),
            TEST_URL
        )
        val bytes = frameData.toBytes()
        val expectedBytes = byteArrayOf(
            1,
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
            'h'.toByte(),
            't'.toByte(),
            't'.toByte(),
            'p'.toByte(),
            ':'.toByte(),
            '/'.toByte(),
            '/'.toByte(),
            'A'.toByte(),
            'B'.toByte(),
            'C'.toByte(),
            'D'.toByte(),
            'E'.toByte(),
            'F'.toByte(),
            'G'.toByte(),
            'H'.toByte(),
            'I'.toByte(),
            'J'.toByte(),
            'K'.toByte(),
            'L'.toByte(),
            'M'.toByte(),
            'N'.toByte(),
            'O'.toByte(),
            'P'.toByte(),
            'Q'.toByte()
        )
        Assert.assertArrayEquals(expectedBytes, bytes)
        val frameDataCopy = ID3v2UrlFrameData(false, bytes)
        Assert.assertEquals(frameData, frameDataCopy)
    }

    @Test
    fun getsAndSetsDescription() {
        val frameData = ID3v2UrlFrameData(false)
        val description = EncodedText("my description")
        frameData.description = description
        Assert.assertEquals(description, frameData.description)
    }

    @Test
    fun getsAndSetsUrl() {
        val frameData = ID3v2UrlFrameData(false)
        frameData.url = "My URL"
        Assert.assertEquals("My URL", frameData.url)
    }

    companion object {
        private const val TEST_DESCRIPTION = "DESCRIPTION"
        private const val TEST_DESCRIPTION_UNICODE = "\u03B3\u03B5\u03B9\u03AC"
        private const val TEST_URL = "http://ABCDEFGHIJKLMNOPQ"
    }
}