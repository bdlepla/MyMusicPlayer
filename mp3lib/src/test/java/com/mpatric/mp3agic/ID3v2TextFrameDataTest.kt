package com.mpatric.mp3agic

import org.junit.Assert
import org.junit.Test

class ID3v2TextFrameDataTest {
    @Test
    @Throws(Exception::class)
    fun equalsItself() {
        val frameData =
            ID3v2TextFrameData(false, EncodedText(EncodedText.TEXT_ENCODING_ISO_8859_1, TEST_TEXT))
        Assert.assertEquals(frameData, frameData)
    }

    @Test
    @Throws(Exception::class)
    fun notEqualToNull() {
        val frameData =
            ID3v2TextFrameData(false, EncodedText(EncodedText.TEXT_ENCODING_ISO_8859_1, TEST_TEXT))
        Assert.assertFalse(frameData.equals(null))
    }

    @Test
    fun notEqualToDifferentClass() {
        val frameData =
            ID3v2TextFrameData(false, EncodedText(EncodedText.TEXT_ENCODING_ISO_8859_1, TEST_TEXT))
        Assert.assertFalse(frameData.equals("8"))
    }

    @Test
    fun notEqualIfUnsynchronizationNotEqual() {
        val frameData1 =
            ID3v2TextFrameData(false, EncodedText(EncodedText.TEXT_ENCODING_ISO_8859_1, TEST_TEXT))
        val frameData2 =
            ID3v2TextFrameData(true, EncodedText(EncodedText.TEXT_ENCODING_ISO_8859_1, TEST_TEXT))
        Assert.assertNotEquals(frameData1, frameData2)
    }

    @Test
    fun notEqualIfTextNotEqual() {
        val frameData1 =
            ID3v2TextFrameData(false, EncodedText(EncodedText.TEXT_ENCODING_ISO_8859_1, TEST_TEXT))
        val frameData2 = ID3v2TextFrameData(
            false,
            EncodedText(EncodedText.TEXT_ENCODING_ISO_8859_1, "other text")
        )
        Assert.assertNotEquals(frameData1, frameData2)
    }

    @Test
    fun notEqualIfTextIsNullOnOne() {
        val frameData1 = ID3v2TextFrameData(false, null as EncodedText?)
        val frameData2 =
            ID3v2TextFrameData(false, EncodedText(EncodedText.TEXT_ENCODING_ISO_8859_1, TEST_TEXT))
        Assert.assertNotEquals(frameData1, frameData2)
    }

    @Test
    fun equalIfTextIsNullOnBoth() {
        val frameData1 = ID3v2TextFrameData(false, null as EncodedText?)
        val frameData2 = ID3v2TextFrameData(false, null as EncodedText?)
        Assert.assertEquals(frameData1, frameData2)
    }

    @Test
    fun hashCodeIsConsistent() {
        val frameData =
            ID3v2TextFrameData(false, EncodedText(EncodedText.TEXT_ENCODING_ISO_8859_1, TEST_TEXT))
        Assert.assertEquals(frameData.hashCode().toLong(), frameData.hashCode().toLong())
    }

    @Test
    fun equalObjectsHaveSameHashCode() {
        val frameData =
            ID3v2TextFrameData(false, EncodedText(EncodedText.TEXT_ENCODING_ISO_8859_1, TEST_TEXT))
        val frameDataAgain =
            ID3v2TextFrameData(false, EncodedText(EncodedText.TEXT_ENCODING_ISO_8859_1, TEST_TEXT))
        Assert.assertEquals(frameData.hashCode().toLong(), frameDataAgain.hashCode().toLong())
    }

    @Test
    @Throws(Exception::class)
    fun shouldConsiderTwoEquivalentObjectsEqual() {
        val frameData1 =
            ID3v2TextFrameData(false, EncodedText(EncodedText.TEXT_ENCODING_ISO_8859_1, TEST_TEXT))
        val frameData2 =
            ID3v2TextFrameData(false, EncodedText(EncodedText.TEXT_ENCODING_ISO_8859_1, TEST_TEXT))
        Assert.assertEquals(frameData1, frameData2)
    }

    @Test
    @Throws(Exception::class)
    fun shouldConvertFrameDataToBytesAndBackToEquivalentObject() {
        val frameData =
            ID3v2TextFrameData(false, EncodedText(EncodedText.TEXT_ENCODING_ISO_8859_1, TEST_TEXT))
        val bytes = frameData.toBytes()
        val expectedBytes = byteArrayOf(
            0,
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
        val frameDataCopy = ID3v2TextFrameData(false, bytes)
        Assert.assertEquals(frameData, frameDataCopy)
    }

    @Test
    @Throws(Exception::class)
    fun shouldConvertFrameDataWithUnicodeToBytesAndBackToEquivalentObject() {
        val frameData = ID3v2TextFrameData(
            false,
            EncodedText(EncodedText.TEXT_ENCODING_UTF_16, TEST_TEXT_UNICODE)
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
            0x03
        )
        Assert.assertArrayEquals(expectedBytes, bytes)
        val frameDataCopy = ID3v2TextFrameData(false, bytes)
        Assert.assertEquals(frameData, frameDataCopy)
    }

    @Test
    fun getsAndSetsDescription() {
        val frameData = ID3v2TextFrameData(false)
        val text = EncodedText("my text")
        frameData.text = text
        Assert.assertEquals(text, frameData.text)
    }

    companion object {
        private const val TEST_TEXT = "ABCDEFGHIJKLMNOPQ"
        private const val TEST_TEXT_UNICODE = "\u03B3\u03B5\u03B9\u03AC"
    }
}