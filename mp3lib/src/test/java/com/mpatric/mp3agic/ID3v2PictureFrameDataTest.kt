package com.mpatric.mp3agic

import org.junit.Assert
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class ID3v2PictureFrameDataTest {
    @Test
    @Throws(Exception::class)
    fun equalsItself() {
        val frameData = ID3v2PictureFrameData(
            false, TEST_MIME_TYPE, 3.toByte(), EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), DUMMY_IMAGE_DATA
        )
        Assert.assertEquals(frameData, frameData)
    }

    @Test
    @Throws(Exception::class)
    fun notEqualToNull() {
        val frameData = ID3v2PictureFrameData(
            false, TEST_MIME_TYPE, 3.toByte(), EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), DUMMY_IMAGE_DATA
        )
        Assert.assertFalse(frameData.equals(null))
    }

    @Test
    fun notEqualToDifferentClass() {
        val frameData = ID3v2PictureFrameData(
            false, TEST_MIME_TYPE, 3.toByte(), EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), DUMMY_IMAGE_DATA
        )
        Assert.assertFalse(frameData.equals("8"))
    }

    @Test
    @Throws(Exception::class)
    fun shouldConsiderTwoEquivalentObjectsEqual() {
        val frameData1 = ID3v2PictureFrameData(
            false, TEST_MIME_TYPE, 3.toByte(), EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), DUMMY_IMAGE_DATA
        )
        val frameData2 = ID3v2PictureFrameData(
            false, TEST_MIME_TYPE, 3.toByte(), EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), DUMMY_IMAGE_DATA
        )
        Assert.assertEquals(frameData1, frameData2)
    }

    @Test
    fun notEqualIfUnsynchronizationNotEqual() {
        val frameData1 = ID3v2PictureFrameData(
            false, TEST_MIME_TYPE, 3.toByte(), EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), DUMMY_IMAGE_DATA
        )
        val frameData2 = ID3v2PictureFrameData(
            true, TEST_MIME_TYPE, 3.toByte(), EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), DUMMY_IMAGE_DATA
        )
        Assert.assertNotEquals(frameData1, frameData2)
    }

    @Test
    fun notEqualIfMimeTypeNotEqual() {
        val frameData1 = ID3v2PictureFrameData(
            false, TEST_MIME_TYPE, 3.toByte(), EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), DUMMY_IMAGE_DATA
        )
        val frameData2 = ID3v2PictureFrameData(
            false, "other mime type", 3.toByte(), EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), DUMMY_IMAGE_DATA
        )
        Assert.assertNotEquals(frameData1, frameData2)
    }

    @Test
    fun notEqualIfMimeTypeIsNullOnOne() {
        val frameData1 = ID3v2PictureFrameData(
            false, null, 3.toByte(), EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), DUMMY_IMAGE_DATA
        )
        val frameData2 = ID3v2PictureFrameData(
            false, TEST_MIME_TYPE, 3.toByte(), EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), DUMMY_IMAGE_DATA
        )
        ID3v2ChapterFrameData(false, "ch2", 1, 380, 3, 400)
        Assert.assertNotEquals(frameData1, frameData2)
    }

    @Test
    fun equalIfMimeTypeIsNullOnBoth() {
        val frameData1 = ID3v2PictureFrameData(
            false, null, 3.toByte(), EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), DUMMY_IMAGE_DATA
        )
        val frameData2 = ID3v2PictureFrameData(
            false, null, 3.toByte(), EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), DUMMY_IMAGE_DATA
        )
        Assert.assertEquals(frameData1, frameData2)
    }

    @Test
    fun notEqualIfPictureTypeNotEqual() {
        val frameData1 = ID3v2PictureFrameData(
            false, TEST_MIME_TYPE, 3.toByte(), EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), DUMMY_IMAGE_DATA
        )
        val frameData2 = ID3v2PictureFrameData(
            false, TEST_MIME_TYPE, 4.toByte(), EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), DUMMY_IMAGE_DATA
        )
        Assert.assertNotEquals(frameData1, frameData2)
    }

    @Test
    fun notEqualIfDescriptionNotEqual() {
        val frameData1 = ID3v2PictureFrameData(
            false, TEST_MIME_TYPE, 3.toByte(), EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), DUMMY_IMAGE_DATA
        )
        val frameData2 = ID3v2PictureFrameData(
            false, TEST_MIME_TYPE, 3.toByte(), EncodedText(
                0.toByte(), "other description"
            ), DUMMY_IMAGE_DATA
        )
        Assert.assertNotEquals(frameData1, frameData2)
    }

    @Test
    fun notEqualIfDescriptionIsNullOnOne() {
        val frameData1 =
            ID3v2PictureFrameData(false, TEST_MIME_TYPE, 3.toByte(), null, DUMMY_IMAGE_DATA)
        val frameData2 = ID3v2PictureFrameData(
            false, TEST_MIME_TYPE, 3.toByte(), EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), DUMMY_IMAGE_DATA
        )
        Assert.assertNotEquals(frameData1, frameData2)
    }

    @Test
    fun equalIfDescriptionIsNullOnBoth() {
        val frameData1 =
            ID3v2PictureFrameData(false, TEST_MIME_TYPE, 3.toByte(), null, DUMMY_IMAGE_DATA)
        val frameData2 =
            ID3v2PictureFrameData(false, TEST_MIME_TYPE, 3.toByte(), null, DUMMY_IMAGE_DATA)
        Assert.assertEquals(frameData1, frameData2)
    }

    @Test
    fun notEqualIfImageDataNotEqual() {
        val frameData1 = ID3v2PictureFrameData(
            false, TEST_MIME_TYPE, 3.toByte(), EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), DUMMY_IMAGE_DATA
        )
        val frameData2 = ID3v2PictureFrameData(
            false, TEST_MIME_TYPE, 3.toByte(), EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), byteArrayOf()
        )
        Assert.assertNotEquals(frameData1, frameData2)
    }

    @Test
    fun notEqualIfImageDataNullOnOne() {
        val frameData1 = ID3v2PictureFrameData(
            false, TEST_MIME_TYPE, 3.toByte(), EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), null
        )
        val frameData2 = ID3v2PictureFrameData(
            false, TEST_MIME_TYPE, 3.toByte(), EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), DUMMY_IMAGE_DATA
        )
        Assert.assertNotEquals(frameData1, frameData2)
    }

    @Test
    fun equalIfImageDataIsNullOnBoth() {
        val frameData1 = ID3v2PictureFrameData(
            false, TEST_MIME_TYPE, 3.toByte(), EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), null
        )
        val frameData2 = ID3v2PictureFrameData(
            false, TEST_MIME_TYPE, 3.toByte(), EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), null
        )
        Assert.assertEquals(frameData1, frameData2)
    }

    @Test
    fun hashCodeIsConsistent() {
        val frameData = ID3v2PictureFrameData(
            false, TEST_MIME_TYPE, 3.toByte(), EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), DUMMY_IMAGE_DATA
        )
        Assert.assertEquals(frameData.hashCode().toLong(), frameData.hashCode().toLong())
    }

    @Test
    fun equalObjectsHaveSameHashCode() {
        val frameData = ID3v2PictureFrameData(
            false, TEST_MIME_TYPE, 3.toByte(), EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), DUMMY_IMAGE_DATA
        )
        val frameDataAgain = ID3v2PictureFrameData(
            false, TEST_MIME_TYPE, 3.toByte(), EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), DUMMY_IMAGE_DATA
        )
        Assert.assertEquals(frameData.hashCode().toLong(), frameDataAgain.hashCode().toLong())
    }

    @Test
    @Throws(Exception::class)
    fun shouldConvertFrameDataToBytesAndBackToEquivalentObject() {
        val frameData = ID3v2PictureFrameData(
            false, TEST_MIME_TYPE, 3.toByte(), EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), DUMMY_IMAGE_DATA
        )
        val bytes = frameData.toBytes()
        val expectedBytes = byteArrayOf(
            0x00,
            'm'.toByte(),
            'i'.toByte(),
            'm'.toByte(),
            'e'.toByte(),
            '/'.toByte(),
            't'.toByte(),
            'y'.toByte(),
            'p'.toByte(),
            'e'.toByte(),
            0,
            0x03,
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
            1,
            2,
            3,
            4,
            5
        )
        Assert.assertArrayEquals(expectedBytes, bytes)
        val frameDataCopy = ID3v2PictureFrameData(false, bytes)
        Assert.assertEquals(frameData, frameDataCopy)
    }

    @Test
    @Throws(Exception::class)
    fun shouldConvertFrameDataWithUnicodeDescriptionToBytesAndBackToEquivalentObject() {
        val frameData = ID3v2PictureFrameData(
            false,
            TEST_MIME_TYPE,
            3.toByte(),
            EncodedText(EncodedText.TEXT_ENCODING_UTF_16, TEST_DESCRIPTION_UNICODE),
            DUMMY_IMAGE_DATA
        )
        val bytes = frameData.toBytes()
        val expectedBytes = byteArrayOf(
            0x01,
            'm'.toByte(),
            'i'.toByte(),
            'm'.toByte(),
            'e'.toByte(),
            '/'.toByte(),
            't'.toByte(),
            'y'.toByte(),
            'p'.toByte(),
            'e'.toByte(),
            0,
            0x03,
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
        Assert.assertArrayEquals(expectedBytes, bytes)
        val frameDataCopy = ID3v2PictureFrameData(false, bytes)
        Assert.assertEquals(frameData, frameDataCopy)
    }

    @Test
    @Throws(Exception::class)
    fun shouldUnsynchroniseAndSynchroniseDataWhenPackingAndUnpacking() {
        val data = byteArrayOf(
            0x00,
            'm'.toByte(),
            'i'.toByte(),
            'm'.toByte(),
            'e'.toByte(),
            '/'.toByte(),
            't'.toByte(),
            'y'.toByte(),
            'p'.toByte(),
            'e'.toByte(),
            0,
            0x03,
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
            1,
            2,
            3,
            BYTE_FF,
            0x00,
            BYTE_FB,
            BYTE_FF,
            0x00,
            BYTE_FB,
            BYTE_FF,
            0,
            0,
            4,
            5
        )
        val frameData = ID3v2PictureFrameData(true, data)
        val expectedImageData =
            byteArrayOf(1, 2, 3, BYTE_FF, BYTE_FB, BYTE_FF, BYTE_FB, BYTE_FF, 0, 4, 5)
        assertArrayEquals(expectedImageData, frameData.imageData)
        val bytes = frameData.toBytes()
        Assert.assertArrayEquals(data, bytes)
    }

    @Test
    fun getsAndSetsMimeType() {
        val frameData = ID3v2PictureFrameData(false)
        frameData.mimeType = "Mime Type 1"
        assertEquals("Mime Type 1", frameData.mimeType)
    }

    @Test
    fun getsAndSetsPictureType() {
        val frameData = ID3v2PictureFrameData(false)
        frameData.pictureType = 4.toByte()
        assertEquals(4.toByte(), frameData.pictureType)
    }

    @Test
    fun getsAndSetsDescription() {
        val frameData = ID3v2PictureFrameData(false)
        val description = EncodedText("my description")
        frameData.description = description
        Assert.assertEquals(description, frameData.description)
    }

    @Test
    fun getsAndSetsImageData() {
        val frameData = ID3v2PictureFrameData(false)
        frameData.imageData = byteArrayOf(1, 2)
        assertArrayEquals(byteArrayOf(1, 2), frameData.imageData)
    }

    companion object {
        private const val BYTE_FF: Byte = -0x01
        private const val BYTE_FB: Byte = -0x05
        private const val TEST_MIME_TYPE = "mime/type"
        private const val TEST_DESCRIPTION = "DESCRIPTION"
        private const val TEST_DESCRIPTION_UNICODE = "\u03B3\u03B5\u03B9\u03AC"
        private val DUMMY_IMAGE_DATA = byteArrayOf(1, 2, 3, 4, 5)
    }
}