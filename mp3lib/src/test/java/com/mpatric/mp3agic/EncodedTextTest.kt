package com.mpatric.mp3agic

import com.mpatric.mp3agic.BufferTools.stringToByteBuffer
import org.junit.Assert
import org.junit.Test
import java.io.UnsupportedEncodingException
import java.nio.charset.CharacterCodingException

class EncodedTextTest {
    @Test
    fun shouldConstructFromStringOrBytes() {
        var encodedText: EncodedText
        var encodedText2: EncodedText
        encodedText = EncodedText(EncodedText.TEXT_ENCODING_ISO_8859_1, TEST_STRING)
        encodedText2 = EncodedText(
            EncodedText.TEXT_ENCODING_ISO_8859_1, TestHelper.hexStringToBytes(
                TEST_STRING_HEX_ISO8859_1
            )
        )
        Assert.assertEquals(encodedText, encodedText2)
        encodedText = EncodedText(EncodedText.TEXT_ENCODING_UTF_8, UNICODE_TEST_STRING)
        encodedText2 = EncodedText(
            EncodedText.TEXT_ENCODING_UTF_8, TestHelper.hexStringToBytes(
                UNICODE_TEST_STRING_HEX_UTF8
            )
        )
        Assert.assertEquals(encodedText, encodedText2)
        encodedText = EncodedText(EncodedText.TEXT_ENCODING_UTF_16, UNICODE_TEST_STRING)
        encodedText2 = EncodedText(
            EncodedText.TEXT_ENCODING_UTF_16, TestHelper.hexStringToBytes(
                UNICODE_TEST_STRING_HEX_UTF16LE
            )
        )
        Assert.assertEquals(encodedText, encodedText2)
        encodedText = EncodedText(EncodedText.TEXT_ENCODING_UTF_16BE, UNICODE_TEST_STRING)
        encodedText2 = EncodedText(
            EncodedText.TEXT_ENCODING_UTF_16BE, TestHelper.hexStringToBytes(
                UNICODE_TEST_STRING_HEX_UTF16BE
            )
        )
        Assert.assertEquals(encodedText, encodedText2)
    }

    @Test
    fun shouldUseAppropriateEncodingWhenConstructingFromStringOnly() {
        var encodedText: EncodedText
        var s: String
        encodedText = EncodedText(TEST_STRING)
        s = encodedText.toString()
        Assert.assertNotNull(s)
        encodedText = EncodedText(UNICODE_TEST_STRING)
        s = encodedText.toString()
        Assert.assertNotNull(s)
    }

    @Test
    fun shouldEncodeAndDecodeISO8859_1Text() {
        val encodedText = EncodedText(EncodedText.TEXT_ENCODING_ISO_8859_1, TEST_STRING)
        Assert.assertEquals(EncodedText.CHARSET_ISO_8859_1, encodedText.characterSet)
        Assert.assertEquals(TEST_STRING, encodedText.toString())
        var encodedText2: EncodedText?
        var bytes: ByteArray
        // no bom & no terminator
        bytes = encodedText.toBytes()
        Assert.assertEquals(TEST_STRING_HEX_ISO8859_1, TestHelper.bytesToHexString(bytes))
        encodedText2 = EncodedText(EncodedText.TEXT_ENCODING_ISO_8859_1, bytes)
        Assert.assertEquals(encodedText, encodedText2)
        // bom & no terminator
        bytes = encodedText.toBytes(true)
        Assert.assertEquals(TEST_STRING_HEX_ISO8859_1, TestHelper.bytesToHexString(bytes))
        encodedText2 = EncodedText(EncodedText.TEXT_ENCODING_ISO_8859_1, bytes)
        Assert.assertEquals(encodedText, encodedText2)
        // no bom & terminator
        bytes = encodedText.toBytes(false, true)
        Assert.assertEquals(TEST_STRING_HEX_ISO8859_1 + " 00", TestHelper.bytesToHexString(bytes))
        encodedText2 = EncodedText(EncodedText.TEXT_ENCODING_ISO_8859_1, bytes)
        Assert.assertEquals(encodedText, encodedText2)
        // bom & terminator
        bytes = encodedText.toBytes(true, true)
        Assert.assertEquals(TEST_STRING_HEX_ISO8859_1 + " 00", TestHelper.bytesToHexString(bytes))
        encodedText2 = EncodedText(EncodedText.TEXT_ENCODING_ISO_8859_1, bytes)
        Assert.assertEquals(encodedText, encodedText2)
    }

    @Test
    fun shouldEncodeAndDecodeUTF8Text() {
        val encodedText = EncodedText(EncodedText.TEXT_ENCODING_UTF_8, UNICODE_TEST_STRING)
        Assert.assertEquals(EncodedText.CHARSET_UTF_8, encodedText.characterSet)
        Assert.assertEquals(UNICODE_TEST_STRING, encodedText.toString())
        var encodedText2: EncodedText?
        var bytes: ByteArray
        // no bom & no terminator
        bytes = encodedText.toBytes()
        val c = TestHelper.bytesToHexString(bytes)
        Assert.assertEquals(UNICODE_TEST_STRING_HEX_UTF8, c)
        encodedText2 = EncodedText(EncodedText.TEXT_ENCODING_UTF_8, bytes)
        Assert.assertEquals(encodedText, encodedText2)
        // bom & no terminator
        bytes = encodedText.toBytes(true)
        Assert.assertEquals(UNICODE_TEST_STRING_HEX_UTF8, TestHelper.bytesToHexString(bytes))
        encodedText2 = EncodedText(EncodedText.TEXT_ENCODING_UTF_8, bytes)
        Assert.assertEquals(encodedText, encodedText2)
        // no bom & terminator
        bytes = encodedText.toBytes(false, true)
        Assert.assertEquals(
            UNICODE_TEST_STRING_HEX_UTF8 + " 00",
            TestHelper.bytesToHexString(bytes)
        )
        encodedText2 = EncodedText(EncodedText.TEXT_ENCODING_UTF_8, bytes)
        Assert.assertEquals(encodedText, encodedText2)
        // bom & terminator
        bytes = encodedText.toBytes(true, true)
        Assert.assertEquals(
            UNICODE_TEST_STRING_HEX_UTF8 + " 00",
            TestHelper.bytesToHexString(bytes)
        )
        encodedText2 = EncodedText(EncodedText.TEXT_ENCODING_UTF_8, bytes)
        Assert.assertEquals(encodedText, encodedText2)
    }

    @Test
    fun shouldEncodeAndDecodeUTF16Text() {
        val encodedText = EncodedText(EncodedText.TEXT_ENCODING_UTF_16, UNICODE_TEST_STRING)
        Assert.assertEquals(EncodedText.CHARSET_UTF_16, encodedText.characterSet)
        Assert.assertEquals(UNICODE_TEST_STRING, encodedText.toString())
        var bytes: ByteArray
        var encodedText2: EncodedText
        // no bom & no terminator
        bytes = encodedText.toBytes()
        Assert.assertEquals(UNICODE_TEST_STRING_HEX_UTF16LE, TestHelper.bytesToHexString(bytes))
        encodedText2 = EncodedText(EncodedText.TEXT_ENCODING_UTF_16, bytes)
        Assert.assertEquals(encodedText, encodedText2)
        // bom & no terminator
        bytes = encodedText.toBytes(true)
        Assert.assertEquals(
            "ff fe " + UNICODE_TEST_STRING_HEX_UTF16LE,
            TestHelper.bytesToHexString(bytes)
        )
        encodedText2 = EncodedText(EncodedText.TEXT_ENCODING_UTF_16, bytes)
        Assert.assertEquals(encodedText, encodedText2)
        // no bom & terminator
        bytes = encodedText.toBytes(false, true)
        Assert.assertEquals(
            UNICODE_TEST_STRING_HEX_UTF16LE + " 00 00",
            TestHelper.bytesToHexString(bytes)
        )
        encodedText2 = EncodedText(EncodedText.TEXT_ENCODING_UTF_16, bytes)
        Assert.assertEquals(encodedText, encodedText2)
        // bom & terminator
        bytes = encodedText.toBytes(true, true)
        Assert.assertEquals(
            "ff fe " + UNICODE_TEST_STRING_HEX_UTF16LE + " 00 00",
            TestHelper.bytesToHexString(bytes)
        )
        encodedText2 = EncodedText(EncodedText.TEXT_ENCODING_UTF_16, bytes)
        Assert.assertEquals(encodedText, encodedText2)
    }

    @Test
    fun shouldEncodeAndDecodeUTF16BEText() {
        val encodedText = EncodedText(EncodedText.TEXT_ENCODING_UTF_16BE, UNICODE_TEST_STRING)
        Assert.assertEquals(EncodedText.CHARSET_UTF_16BE, encodedText.characterSet)
        Assert.assertEquals(UNICODE_TEST_STRING, encodedText.toString())
        var bytes: ByteArray
        var encodedText2: EncodedText
        // no bom & no terminator
        bytes = encodedText.toBytes()
        Assert.assertEquals(UNICODE_TEST_STRING_HEX_UTF16BE, TestHelper.bytesToHexString(bytes))
        encodedText2 = EncodedText(EncodedText.TEXT_ENCODING_UTF_16BE, bytes)
        Assert.assertEquals(encodedText, encodedText2)
        // bom & no terminator
        bytes = encodedText.toBytes(true)
        Assert.assertEquals(
            "fe ff " + UNICODE_TEST_STRING_HEX_UTF16BE,
            TestHelper.bytesToHexString(bytes)
        )
        encodedText2 = EncodedText(EncodedText.TEXT_ENCODING_UTF_16BE, bytes)
        Assert.assertEquals(encodedText, encodedText2)
        // no bom & terminator
        bytes = encodedText.toBytes(false, true)
        Assert.assertEquals(
            UNICODE_TEST_STRING_HEX_UTF16BE + " 00 00",
            TestHelper.bytesToHexString(bytes)
        )
        encodedText2 = EncodedText(EncodedText.TEXT_ENCODING_UTF_16BE, bytes)
        Assert.assertEquals(encodedText, encodedText2)
        // bom & terminator
        bytes = encodedText.toBytes(true, true)
        Assert.assertEquals(
            "fe ff " + UNICODE_TEST_STRING_HEX_UTF16BE + " 00 00",
            TestHelper.bytesToHexString(bytes)
        )
        encodedText2 = EncodedText(EncodedText.TEXT_ENCODING_UTF_16BE, bytes)
        Assert.assertEquals(encodedText, encodedText2)
    }

    @Test
    fun UTF16ShouldDecodeBEWhenSpecifiedInBOM() {
        // id3 v2.2 and 2.3: encoding set to UTF_16 (type 1), but BOM set to big endian, so interpret as UTF_16BE
        val encodedText = EncodedText(EncodedText.TEXT_ENCODING_UTF_16BE, UNICODE_TEST_STRING)
        val bytes = encodedText.toBytes(true, true)
        val encodedText2 = EncodedText(EncodedText.TEXT_ENCODING_UTF_16, bytes)
        Assert.assertEquals(encodedText, encodedText2)
    }

    @Test
    fun shouldThrowExceptionWhenEncodingWithInvalidCharacterSet() {
        try {
            EncodedText(4.toByte(), TEST_STRING)
            Assert.fail("IllegalArgumentException expected but not thrown")
        } catch (e: IllegalArgumentException) {
            Assert.assertEquals("Invalid text encoding 4", e.message)
        }
    }

    @Test
    fun shouldInferISO8859_1EncodingFromBytesWithNoBOM() {
        val encodedText = EncodedText(TestHelper.hexStringToBytes(TEST_STRING_HEX_ISO8859_1))
        Assert.assertEquals(
            EncodedText.TEXT_ENCODING_ISO_8859_1.toLong(),
            encodedText.getTextEncoding().toLong()
        )
    }

    @Test
    fun shouldDetectUTF8EncodingFromBytesWithBOM() {
        val encodedText =
            EncodedText(TestHelper.hexStringToBytes("ef bb bf " + UNICODE_TEST_STRING_HEX_UTF8))
        Assert.assertEquals(
            EncodedText.TEXT_ENCODING_UTF_8.toLong(),
            encodedText.getTextEncoding().toLong()
        )
    }

    @Test
    fun shouldDetectUTF16EncodingFromBytesWithBOM() {
        val encodedText =
            EncodedText(TestHelper.hexStringToBytes("ff fe " + UNICODE_TEST_STRING_HEX_UTF16LE))
        Assert.assertEquals(
            EncodedText.TEXT_ENCODING_UTF_16.toLong(),
            encodedText.getTextEncoding().toLong()
        )
    }

    @Test
    fun shouldDetectUTF16BEEncodingFromBytesWithBOM() {
        val encodedText =
            EncodedText(TestHelper.hexStringToBytes("fe ff " + UNICODE_TEST_STRING_HEX_UTF16BE))
        Assert.assertEquals(
            EncodedText.TEXT_ENCODING_UTF_16BE.toLong(),
            encodedText.getTextEncoding().toLong()
        )
    }

    @Test
    @Throws(CharacterCodingException::class)
    fun shouldTranscodeFromOneEncodingToAnother() {
        var encodedText: EncodedText
        encodedText = EncodedText(
            EncodedText.TEXT_ENCODING_UTF_8,
            TestHelper.hexStringToBytes("43 61 66 c3 a9 20 50 61 72 61 64 69 73 6f")
        )
        encodedText.setTextEncoding(EncodedText.TEXT_ENCODING_ISO_8859_1)
        Assert.assertEquals(
            "43 61 66 e9 20 50 61 72 61 64 69 73 6f",
            TestHelper.bytesToHexString(encodedText.toBytes())
        )
        encodedText = EncodedText(
            EncodedText.TEXT_ENCODING_UTF_8,
            TestHelper.hexStringToBytes("43 61 66 c3 a9 20 50 61 72 61 64 69 73 6f")
        )
        encodedText.setTextEncoding(EncodedText.TEXT_ENCODING_UTF_8)
        Assert.assertEquals(
            "43 61 66 c3 a9 20 50 61 72 61 64 69 73 6f",
            TestHelper.bytesToHexString(encodedText.toBytes())
        )
        encodedText = EncodedText(
            EncodedText.TEXT_ENCODING_UTF_8,
            TestHelper.hexStringToBytes("43 61 66 c3 a9 20 50 61 72 61 64 69 73 6f")
        )
        encodedText.setTextEncoding(EncodedText.TEXT_ENCODING_UTF_16)
        Assert.assertEquals(
            "43 00 61 00 66 00 e9 00 20 00 50 00 61 00 72 00 61 00 64 00 69 00 73 00 6f 00",
            TestHelper.bytesToHexString(encodedText.toBytes())
        )
        encodedText = EncodedText(
            EncodedText.TEXT_ENCODING_UTF_8,
            TestHelper.hexStringToBytes("43 61 66 c3 a9 20 50 61 72 61 64 69 73 6f")
        )
        encodedText.setTextEncoding(EncodedText.TEXT_ENCODING_UTF_16BE)
        Assert.assertEquals(
            "00 43 00 61 00 66 00 e9 00 20 00 50 00 61 00 72 00 61 00 64 00 69 00 73 00 6f",
            TestHelper.bytesToHexString(encodedText.toBytes())
        )
    }

    @Test(expected = CharacterCodingException::class)
    @Throws(CharacterCodingException::class)
    fun shouldThrowAnExceptionWhenAttemptingToTranscodeToACharacterSetWithUnmappableCharacters() {
        // given
        val encodedText = EncodedText(EncodedText.TEXT_ENCODING_UTF_8, UNICODE_TEST_STRING)

        // expect exception
        encodedText.setTextEncoding(EncodedText.TEXT_ENCODING_ISO_8859_1)
    }

    @Test
    @Throws(CharacterCodingException::class)
    fun shouldThrowExceptionWhenTranscodingWithInvalidCharacterSet() {
        val encodedText = EncodedText(
            EncodedText.TEXT_ENCODING_UTF_8,
            TestHelper.hexStringToBytes("43 61 66 c3 a9 20 50 61 72 61 64 69 73 6f")
        )
        try {
            encodedText.setTextEncoding(4.toByte())
            Assert.fail("IllegalArgumentException expected but not thrown")
        } catch (e: IllegalArgumentException) {
            Assert.assertEquals("Invalid text encoding 4", e.message)
        }
    }

    @Test
    @Throws(UnsupportedEncodingException::class)
    fun shouldReturnEmptyWhenDecodingInvalidString() {
        val s = "Not unicode"
        val notUnicode = stringToByteBuffer(s, 0, s.length)
        val encodedText = EncodedText(EncodedText.TEXT_ENCODING_UTF_16, notUnicode)
        Assert.assertEquals("", encodedText.toString())
    }

    @Test
    fun shouldHandleBacktickCharacterInString() {
        val encodedText = EncodedText(0.toByte(), BUFFER_WITH_A_BACK_TICK)
        Assert.assertEquals("I" + 96.toChar() + "m", encodedText.toString())
    }

    @Test
    fun shouldStillReturnBytesWhenStringIsEmpty() {
        val encodedText = EncodedText(EncodedText.TEXT_ENCODING_ISO_8859_1, "")
        Assert.assertArrayEquals(byteArrayOf(), encodedText.toBytes(false, false))
        Assert.assertArrayEquals(byteArrayOf(), encodedText.toBytes(true, false))
        Assert.assertArrayEquals(byteArrayOf(0), encodedText.toBytes(false, true))
        Assert.assertArrayEquals(byteArrayOf(0), encodedText.toBytes(true, true))
    }

    @Test
    fun shouldStillReturnBytesWhenUnicodeStringIsEmpty() {
        val encodedText = EncodedText(EncodedText.TEXT_ENCODING_UTF_16, "")
        Assert.assertArrayEquals(byteArrayOf(), encodedText.toBytes(false, false))
        Assert.assertArrayEquals(
            byteArrayOf(0xff.toByte(), 0xfe.toByte()),
            encodedText.toBytes(true, false)
        )
        Assert.assertArrayEquals(byteArrayOf(0, 0), encodedText.toBytes(false, true))
        Assert.assertArrayEquals(
            byteArrayOf(0xff.toByte(), 0xfe.toByte(), 0, 0),
            encodedText.toBytes(true, true)
        )
    }

    @Test
    fun shouldStillReturnBytesWhenDataIsEmpty() {
        var encodedText: EncodedText
        encodedText = EncodedText(EncodedText.TEXT_ENCODING_ISO_8859_1, byteArrayOf())
        Assert.assertArrayEquals(byteArrayOf(), encodedText.toBytes(false, false))
        Assert.assertArrayEquals(byteArrayOf(), encodedText.toBytes(true, false))
        Assert.assertArrayEquals(byteArrayOf(0), encodedText.toBytes(false, true))
        Assert.assertArrayEquals(byteArrayOf(0), encodedText.toBytes(true, true))
        encodedText = EncodedText(EncodedText.TEXT_ENCODING_ISO_8859_1, byteArrayOf(0))
        Assert.assertArrayEquals(byteArrayOf(), encodedText.toBytes(false, false))
        Assert.assertArrayEquals(byteArrayOf(), encodedText.toBytes(true, false))
        Assert.assertArrayEquals(byteArrayOf(0), encodedText.toBytes(false, true))
        Assert.assertArrayEquals(byteArrayOf(0), encodedText.toBytes(true, true))
    }

    @Test
    fun shouldStillReturnBytesWhenUnicodeDataIsEmpty() {
        var encodedText: EncodedText
        encodedText = EncodedText(EncodedText.TEXT_ENCODING_UTF_16, byteArrayOf())
        Assert.assertArrayEquals(byteArrayOf(), encodedText.toBytes(false, false))
        Assert.assertArrayEquals(
            byteArrayOf(0xff.toByte(), 0xfe.toByte()),
            encodedText.toBytes(true, false)
        )
        Assert.assertArrayEquals(byteArrayOf(0, 0), encodedText.toBytes(false, true))
        Assert.assertArrayEquals(
            byteArrayOf(0xff.toByte(), 0xfe.toByte(), 0, 0),
            encodedText.toBytes(true, true)
        )
        encodedText = EncodedText(EncodedText.TEXT_ENCODING_UTF_16, byteArrayOf(0, 0))
        Assert.assertArrayEquals(byteArrayOf(), encodedText.toBytes(false, false))
        Assert.assertArrayEquals(
            byteArrayOf(0xff.toByte(), 0xfe.toByte()),
            encodedText.toBytes(true, false)
        )
        Assert.assertArrayEquals(byteArrayOf(0, 0), encodedText.toBytes(false, true))
        Assert.assertArrayEquals(
            byteArrayOf(0xff.toByte(), 0xfe.toByte(), 0, 0),
            encodedText.toBytes(true, true)
        )
        encodedText =
            EncodedText(EncodedText.TEXT_ENCODING_UTF_16, byteArrayOf(0xff.toByte(), 0xfe.toByte()))
        Assert.assertArrayEquals(byteArrayOf(), encodedText.toBytes(false, false))
        Assert.assertArrayEquals(
            byteArrayOf(0xff.toByte(), 0xfe.toByte()),
            encodedText.toBytes(true, false)
        )
        Assert.assertArrayEquals(byteArrayOf(0, 0), encodedText.toBytes(false, true))
        Assert.assertArrayEquals(
            byteArrayOf(0xff.toByte(), 0xfe.toByte(), 0, 0),
            encodedText.toBytes(true, true)
        )
        encodedText = EncodedText(
            EncodedText.TEXT_ENCODING_UTF_16,
            byteArrayOf(0xff.toByte(), 0xfe.toByte(), 0, 0)
        )
        Assert.assertArrayEquals(byteArrayOf(), encodedText.toBytes(false, false))
        Assert.assertArrayEquals(
            byteArrayOf(0xff.toByte(), 0xfe.toByte()),
            encodedText.toBytes(true, false)
        )
        Assert.assertArrayEquals(byteArrayOf(0, 0), encodedText.toBytes(false, true))
        Assert.assertArrayEquals(
            byteArrayOf(0xff.toByte(), 0xfe.toByte(), 0, 0),
            encodedText.toBytes(true, true)
        )
    }

    companion object {
        private const val TEST_STRING = "This is a string!"

        // "This is a String!"
        private const val TEST_STRING_HEX_ISO8859_1 =
            "54 68 69 73 20 69 73 20 61 20 73 74 72 69 6e 67 21"

        // γειά σου
        private const val UNICODE_TEST_STRING = "\u03B3\u03B5\u03B9\u03AC \u03C3\u03BF\u03C5"

        // "γειά σου" (This can't be encoded in ISO-8859-1)
        private const val UNICODE_TEST_STRING_HEX_UTF8 =
            "ce b3 ce b5 ce b9 ce ac 20 cf 83 ce bf cf 85"

        // "γειά σου" (This can't be encoded in ISO-8859-1)
        private const val UNICODE_TEST_STRING_HEX_UTF16LE =
            "b3 03 b5 03 b9 03 ac 03 20 00 c3 03 bf 03 c5 03"

        // "γειά σου" (This can't be encoded in ISO-8859-1)
        private const val UNICODE_TEST_STRING_HEX_UTF16BE =
            "03 b3 03 b5 03 b9 03 ac 00 20 03 c3 03 bf 03 c5"
        private val BUFFER_WITH_A_BACK_TICK =
            byteArrayOf(0x49.toByte(), 0x60.toByte(), 0x6D.toByte())
    }
}