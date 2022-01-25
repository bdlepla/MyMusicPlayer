package com.mpatric.mp3agic

import org.junit.Assert
import org.junit.Test

class ID3v2CommentFrameDataTest {
    @Test
    @Throws(Exception::class)
    fun equalsItself() {
        val frameData = ID3v2CommentFrameData(
            false, TEST_LANGUAGE, EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), EncodedText(0.toByte(), TEST_VALUE)
        )
        Assert.assertEquals(frameData, frameData)
    }

    @Test
    @Throws(Exception::class)
    fun notEqualToNull() {
        val frameData = ID3v2CommentFrameData(
            false, TEST_LANGUAGE, EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), EncodedText(0.toByte(), TEST_VALUE)
        )
        Assert.assertFalse(frameData.equals(null))
    }

    @Test
    fun notEqualToDifferentClass() {
        val frameData = ID3v2CommentFrameData(
            false, TEST_LANGUAGE, EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), EncodedText(0.toByte(), TEST_VALUE)
        )
        Assert.assertFalse(frameData.equals("8"))
    }

    @Test
    @Throws(Exception::class)
    fun shouldConsiderTwoEquivalentObjectsEqual() {
        val frameData1 = ID3v2CommentFrameData(
            false, TEST_LANGUAGE, EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), EncodedText(0.toByte(), TEST_VALUE)
        )
        val frameData2 = ID3v2CommentFrameData(
            false, TEST_LANGUAGE, EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), EncodedText(0.toByte(), TEST_VALUE)
        )
        Assert.assertEquals(frameData1, frameData2)
    }

    @Test
    fun notEqualIfUnsynchronizationNotEqual() {
        val frameData1 = ID3v2CommentFrameData(
            false, TEST_LANGUAGE, EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), EncodedText(0.toByte(), TEST_VALUE)
        )
        val frameData2 = ID3v2CommentFrameData(
            true, TEST_LANGUAGE, EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), EncodedText(0.toByte(), TEST_VALUE)
        )
        Assert.assertNotEquals(frameData1, frameData2)
    }

    @Test
    fun notEqualIfLanguageNotEqual() {
        val frameData1 = ID3v2CommentFrameData(
            false, TEST_LANGUAGE, EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), EncodedText(0.toByte(), TEST_VALUE)
        )
        val frameData2 = ID3v2CommentFrameData(
            false, "jap", EncodedText(0.toByte(), TEST_DESCRIPTION), EncodedText(
                0.toByte(), TEST_VALUE
            )
        )
        Assert.assertNotEquals(frameData1, frameData2)
    }

    @Test
    fun notEqualIfLanguageIsNullOnOne() {
        val frameData1 = ID3v2CommentFrameData(
            false, null, EncodedText(0.toByte(), TEST_DESCRIPTION), EncodedText(
                0.toByte(), TEST_VALUE
            )
        )
        val frameData2 = ID3v2CommentFrameData(
            false, TEST_LANGUAGE, EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), EncodedText(0.toByte(), TEST_VALUE)
        )
        Assert.assertNotEquals(frameData1, frameData2)
    }

    @Test
    fun equalIfLanguageIsNullOnBoth() {
        val frameData1 = ID3v2CommentFrameData(
            false, null, EncodedText(0.toByte(), TEST_DESCRIPTION), EncodedText(
                0.toByte(), TEST_VALUE
            )
        )
        val frameData2 = ID3v2CommentFrameData(
            false, null, EncodedText(0.toByte(), TEST_DESCRIPTION), EncodedText(
                0.toByte(), TEST_VALUE
            )
        )
        Assert.assertEquals(frameData1, frameData2)
    }

    @Test
    fun notEqualIfDescriptionNotEqual() {
        val frameData1 = ID3v2CommentFrameData(
            false, TEST_LANGUAGE, EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), EncodedText(0.toByte(), TEST_VALUE)
        )
        val frameData2 = ID3v2CommentFrameData(
            false, TEST_LANGUAGE, EncodedText(
                0.toByte(), "other description"
            ), EncodedText(0.toByte(), TEST_VALUE)
        )
        Assert.assertNotEquals(frameData1, frameData2)
    }

    @Test
    fun notEqualIfDescriptionIsNullOnOne() {
        val frameData1 = ID3v2CommentFrameData(
            false, TEST_LANGUAGE, null, EncodedText(
                0.toByte(), TEST_VALUE
            )
        )
        val frameData2 = ID3v2CommentFrameData(
            false, TEST_LANGUAGE, EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), EncodedText(0.toByte(), TEST_VALUE)
        )
        Assert.assertNotEquals(frameData1, frameData2)
    }

    @Test
    fun equalIfDescriptionIsNullOnBoth() {
        val frameData1 = ID3v2CommentFrameData(
            false, TEST_LANGUAGE, null, EncodedText(
                0.toByte(), TEST_VALUE
            )
        )
        val frameData2 = ID3v2CommentFrameData(
            false, TEST_LANGUAGE, null, EncodedText(
                0.toByte(), TEST_VALUE
            )
        )
        Assert.assertEquals(frameData1, frameData2)
    }

    @Test
    fun notEqualIfCommentNotEqual() {
        val frameData1 = ID3v2CommentFrameData(
            false, TEST_LANGUAGE, EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), EncodedText(0.toByte(), TEST_VALUE)
        )
        val frameData2 = ID3v2CommentFrameData(
            false, TEST_LANGUAGE, EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), EncodedText(0.toByte(), "other comment")
        )
        Assert.assertNotEquals(frameData1, frameData2)
    }

    @Test
    fun notEqualIfCommentIsNullOnOne() {
        val frameData1 = ID3v2CommentFrameData(
            false, TEST_LANGUAGE, EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), null
        )
        val frameData2 = ID3v2CommentFrameData(
            false, TEST_LANGUAGE, EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), EncodedText(0.toByte(), TEST_VALUE)
        )
        Assert.assertNotEquals(frameData1, frameData2)
    }

    @Test
    fun equalIfCommentIsNullOnBoth() {
        val frameData1 = ID3v2CommentFrameData(
            false, TEST_LANGUAGE, EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), null
        )
        val frameData2 = ID3v2CommentFrameData(
            false, TEST_LANGUAGE, EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), null
        )
        Assert.assertEquals(frameData1, frameData2)
    }

    @Test
    fun hashCodeIsConsistent() {
        val frameData = ID3v2CommentFrameData(
            false, TEST_LANGUAGE, EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), null
        )
        Assert.assertEquals(frameData.hashCode().toLong(), frameData.hashCode().toLong())
    }

    @Test
    fun equalObjectsHaveSameHashCode() {
        val frameData = ID3v2CommentFrameData(
            false, TEST_LANGUAGE, EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), null
        )
        val frameDataAgain = ID3v2CommentFrameData(
            false, TEST_LANGUAGE, EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), null
        )
        Assert.assertEquals(frameData.hashCode().toLong(), frameDataAgain.hashCode().toLong())
    }

    @Test
    @Throws(Exception::class)
    fun shouldConvertFrameDataToBytesAndBackToEquivalentObject() {
        val frameData = ID3v2CommentFrameData(
            false, TEST_LANGUAGE, EncodedText(
                0.toByte(), TEST_DESCRIPTION
            ), EncodedText(0.toByte(), TEST_VALUE)
        )
        val bytes = frameData.toBytes()
        val expectedBytes = byteArrayOf(
            0,
            'e'.toByte(),
            'n'.toByte(),
            'g'.toByte(),
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
        val frameDataCopy = ID3v2CommentFrameData(false, bytes)
        Assert.assertEquals(frameData, frameDataCopy)
    }

    @Test(expected = IllegalArgumentException::class)
    fun constructorThrowsErrorWhenEncodingsDoNotMatch() {
        ID3v2CommentFrameData(
            false, TEST_LANGUAGE,
            EncodedText(EncodedText.TEXT_ENCODING_UTF_16, "description"),
            EncodedText(EncodedText.TEXT_ENCODING_UTF_8, "comment")
        )
    }

    @Test
    @Throws(Exception::class)
    fun shouldConvertFrameDataWithBlankDescriptionAndLanguageToBytesAndBackToEquivalentObject() {
        val bytes = byteArrayOf(
            0,
            0,
            0,
            0,
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
        val frameData = ID3v2CommentFrameData(false, bytes)
        Assert.assertEquals("\u0000\u0000\u0000", frameData.language)
        Assert.assertEquals(EncodedText(""), frameData.getDescription())
        Assert.assertEquals(EncodedText(TEST_VALUE), frameData.getComment())
        Assert.assertArrayEquals(bytes, frameData.toBytes())
    }

    @Test
    @Throws(Exception::class)
    fun shouldConvertFrameDataWithUnicodeToBytesAndBackToEquivalentObject() {
        val frameData = ID3v2CommentFrameData(
            false,
            TEST_LANGUAGE,
            EncodedText(EncodedText.TEXT_ENCODING_UTF_16, TEST_DESCRIPTION_UNICODE),
            EncodedText(EncodedText.TEXT_ENCODING_UTF_16, TEST_VALUE_UNICODE)
        )
        val bytes = frameData.toBytes()
        val expectedBytes = byteArrayOf(
            1,
            'e'.toByte(),
            'n'.toByte(),
            'g'.toByte(),
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
            0xff.toByte(),
            0xfe.toByte(),
            0xc3.toByte(),
            0x03,
            0xbf.toByte(),
            0x03,
            0xc5.toByte(),
            0x03
        )
        Assert.assertArrayEquals(expectedBytes, bytes)
        val frameDataCopy = ID3v2CommentFrameData(false, bytes)
        Assert.assertEquals(frameData, frameDataCopy)
    }

    @Test
    @Throws(Exception::class)
    fun convertsEmptyFrameDataToBytesAndBack() {
        val frameData = ID3v2CommentFrameData(false, null, null, null)
        val bytes = frameData.toBytes()
        val frameDataCopy = ID3v2CommentFrameData(false, bytes)
        Assert.assertEquals("eng", frameDataCopy.language)
        Assert.assertEquals(EncodedText(""), frameDataCopy.getDescription())
        Assert.assertEquals(EncodedText(""), frameDataCopy.getComment())
    }

    @Test
    @Throws(Exception::class)
    fun convertsFrameDataWithNoLanguageToBytesAndBack() {
        val frameData = ID3v2CommentFrameData(
            false, null, EncodedText(TEST_DESCRIPTION), EncodedText(
                TEST_VALUE
            )
        )
        val bytes = frameData.toBytes()
        val frameDataCopy = ID3v2CommentFrameData(false, bytes)
        Assert.assertEquals("eng", frameDataCopy.language)
        Assert.assertEquals(EncodedText(TEST_DESCRIPTION), frameDataCopy.getDescription())
        Assert.assertEquals(EncodedText(TEST_VALUE), frameDataCopy.getComment())
    }

    @Test
    @Throws(Exception::class)
    fun convertsFrameDataWithNoDescriptionToBytesAndBack() {
        val frameData = ID3v2CommentFrameData(false, TEST_LANGUAGE, null, EncodedText(TEST_VALUE))
        val bytes = frameData.toBytes()
        val frameDataCopy = ID3v2CommentFrameData(false, bytes)
        Assert.assertEquals("eng", frameDataCopy.language)
        Assert.assertEquals(EncodedText(""), frameDataCopy.getDescription())
        Assert.assertEquals(EncodedText(TEST_VALUE), frameDataCopy.getComment())
    }

    @Test
    @Throws(Exception::class)
    fun convertsFrameDataWithNoDescriptionAndCommentIsUnicodeToBytesAndBack() {
        val frameData = ID3v2CommentFrameData(
            false, TEST_LANGUAGE, null,
            EncodedText(EncodedText.TEXT_ENCODING_UTF_16, TEST_VALUE_UNICODE)
        )
        val bytes = frameData.toBytes()
        val frameDataCopy = ID3v2CommentFrameData(false, bytes)
        Assert.assertEquals("eng", frameDataCopy.language)
        Assert.assertEquals(
            EncodedText(EncodedText.TEXT_ENCODING_UTF_16, ""),
            frameDataCopy.getDescription()
        )
        Assert.assertEquals(
            EncodedText(EncodedText.TEXT_ENCODING_UTF_16, TEST_VALUE_UNICODE),
            frameDataCopy.getComment()
        )
    }

    @Test
    @Throws(Exception::class)
    fun convertsFrameDataWithNoCommentToBytesAndBack() {
        val frameData =
            ID3v2CommentFrameData(false, TEST_LANGUAGE, EncodedText(TEST_DESCRIPTION), null)
        val bytes = frameData.toBytes()
        val frameDataCopy = ID3v2CommentFrameData(false, bytes)
        Assert.assertEquals("eng", frameDataCopy.language)
        Assert.assertEquals(EncodedText(TEST_DESCRIPTION), frameDataCopy.getDescription())
        Assert.assertEquals(EncodedText(""), frameDataCopy.getComment())
    }

    @Test
    fun getsAndSetsLanguage() {
        val frameData = ID3v2CommentFrameData(false)
        frameData.language = "my language"
        Assert.assertEquals("my language", frameData.language)
    }

    @Test
    fun getsAndSetsComment() {
        val frameData = ID3v2CommentFrameData(false)
        val comment = EncodedText("my comment")
        frameData.setComment(comment)
        Assert.assertEquals(comment, frameData.getComment())
    }

    @Test
    fun getsAndSetsDescription() {
        val frameData = ID3v2CommentFrameData(false)
        val description = EncodedText("my description")
        frameData.setDescription(description)
        Assert.assertEquals(description, frameData.getDescription())
    }

    companion object {
        private const val TEST_LANGUAGE = "eng"
        private const val TEST_DESCRIPTION = "DESCRIPTION"
        private const val TEST_VALUE = "ABCDEFGHIJKLMNOPQ"
        private const val TEST_DESCRIPTION_UNICODE = "\u03B3\u03B5\u03B9\u03AC"
        private const val TEST_VALUE_UNICODE = "\u03C3\u03BF\u03C5"
    }
}