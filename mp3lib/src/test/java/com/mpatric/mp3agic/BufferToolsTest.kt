package com.mpatric.mp3agic

import com.mpatric.mp3agic.BufferTools.asciiOnly
import com.mpatric.mp3agic.BufferTools.byteBufferToString
import com.mpatric.mp3agic.BufferTools.checkBit
import com.mpatric.mp3agic.BufferTools.copyBuffer
import com.mpatric.mp3agic.BufferTools.indexOfTerminator
import com.mpatric.mp3agic.BufferTools.packInteger
import com.mpatric.mp3agic.BufferTools.packSynchsafeInteger
import com.mpatric.mp3agic.BufferTools.padStringRight
import com.mpatric.mp3agic.BufferTools.setBit
import com.mpatric.mp3agic.BufferTools.shiftByte
import com.mpatric.mp3agic.BufferTools.sizeSynchronisationWouldSubtract
import com.mpatric.mp3agic.BufferTools.sizeUnsynchronisationWouldAdd
import com.mpatric.mp3agic.BufferTools.stringIntoByteBuffer
import com.mpatric.mp3agic.BufferTools.stringToByteBuffer
import com.mpatric.mp3agic.BufferTools.substitute
import com.mpatric.mp3agic.BufferTools.synchroniseBuffer
import com.mpatric.mp3agic.BufferTools.trimStringRight
import com.mpatric.mp3agic.BufferTools.unpackInteger
import com.mpatric.mp3agic.BufferTools.unpackSynchsafeInteger
import com.mpatric.mp3agic.BufferTools.unsynchroniseBuffer
import org.junit.Assert
import org.junit.Test
import java.io.UnsupportedEncodingException
import java.util.*

class BufferToolsTest {
    // byte buffer to string
    @Test
    @Throws(UnsupportedEncodingException::class)
    fun shouldExtractStringFromStartOfBuffer() {
        val buffer = byteArrayOf(
            BYTE_T,
            BYTE_A,
            BYTE_G,
            BYTE_DASH,
            BYTE_DASH,
            BYTE_DASH,
            BYTE_DASH,
            BYTE_DASH
        )
        Assert.assertEquals("TAG", byteBufferToString(buffer, 0, 3))
    }

    @Test
    @Throws(UnsupportedEncodingException::class)
    fun shouldExtractStringFromEndOfBuffer() {
        val buffer = byteArrayOf(
            BYTE_DASH,
            BYTE_DASH,
            BYTE_DASH,
            BYTE_DASH,
            BYTE_DASH,
            BYTE_T,
            BYTE_A,
            BYTE_G
        )
        Assert.assertEquals("TAG", byteBufferToString(buffer, 5, 3))
    }

    @Test
    @Throws(UnsupportedEncodingException::class)
    fun shouldExtractStringFromMiddleOfBuffer() {
        val buffer = byteArrayOf(
            BYTE_DASH,
            BYTE_DASH,
            BYTE_DASH,
            BYTE_DASH,
            BYTE_DASH,
            BYTE_T,
            BYTE_A,
            BYTE_G
        )
        Assert.assertEquals("TAG", byteBufferToString(buffer, 5, 3))
    }

    @Test
    @Throws(UnsupportedEncodingException::class)
    fun shouldExtractUnicodeStringFromMiddleOfBuffer() {
        val buffer = byteArrayOf(
            BYTE_DASH,
            BYTE_DASH,
            0x03,
            0xb3.toByte(),
            0x03,
            0xb5.toByte(),
            0x03,
            0xb9.toByte(),
            0x03,
            0xac.toByte(),
            BYTE_DASH,
            BYTE_DASH
        )
        Assert.assertEquals("\u03B3\u03B5\u03B9\u03AC", byteBufferToString(buffer, 2, 8, Charsets.UTF_16BE))
    }

    @Test
    @Throws(UnsupportedEncodingException::class)
    fun shouldThrowExceptionForOffsetBeforeStartOfArray() {
        val buffer = byteArrayOf(
            BYTE_DASH,
            BYTE_DASH,
            BYTE_DASH,
            BYTE_DASH,
            BYTE_DASH,
            BYTE_T,
            BYTE_A,
            BYTE_G
        )
        try {
            byteBufferToString(buffer, -1, 4)
            Assert.fail("StringIndexOutOfBoundsException expected but not thrown")
        } catch (e: StringIndexOutOfBoundsException) {
            // expected
        }
    }

    @Test
    @Throws(UnsupportedEncodingException::class)
    fun shouldThrowExceptionForOffsetAfterEndOfArray() {
        val buffer = byteArrayOf(
            BYTE_DASH,
            BYTE_DASH,
            BYTE_DASH,
            BYTE_DASH,
            BYTE_DASH,
            BYTE_T,
            BYTE_A,
            BYTE_G
        )
        try {
            byteBufferToString(buffer, buffer.size, 1)
            Assert.fail("StringIndexOutOfBoundsException expected but not thrown")
        } catch (e: StringIndexOutOfBoundsException) {
            // expected
        }
    }

    @Test
    @Throws(UnsupportedEncodingException::class)
    fun shouldThrowExceptionForLengthExtendingBeyondEndOfArray() {
        val buffer = byteArrayOf(
            BYTE_DASH,
            BYTE_DASH,
            BYTE_DASH,
            BYTE_DASH,
            BYTE_DASH,
            BYTE_T,
            BYTE_A,
            BYTE_G
        )
        try {
            byteBufferToString(buffer, buffer.size - 2, 3)
            Assert.fail("StringIndexOutOfBoundsException expected but not thrown")
        } catch (e: StringIndexOutOfBoundsException) {
            // expected
        }
    }

    // string to byte buffer
    @Test
    @Throws(UnsupportedEncodingException::class)
    fun shouldConvertStringToBufferAndBack() {
        val original = "1234567890QWERTYUIOP"
        val buffer = stringToByteBuffer(original, 0, original.length)
        val converted = byteBufferToString(buffer, 0, buffer.size)
        Assert.assertEquals(original, converted)
    }

    @Test
    @Throws(UnsupportedEncodingException::class)
    fun shouldConvertSubstringToBufferAndBack() {
        val original = "1234567890QWERTYUIOP"
        val buffer = stringToByteBuffer(original, 2, original.length - 5)
        val converted = byteBufferToString(buffer, 0, buffer.size)
        Assert.assertEquals("34567890QWERTYU", converted)
    }

    @Test
    @Throws(UnsupportedEncodingException::class)
    fun shouldConvertUnicodeStringToBufferAndBack() {
        val original = "\u03B3\u03B5\u03B9\u03AC \u03C3\u03BF\u03C5"
        val buffer = stringToByteBuffer(original, 0, original.length, Charsets.UTF_16LE)
        val converted = byteBufferToString(buffer, 0, buffer.size, Charsets.UTF_16LE)
        Assert.assertEquals(original, converted)
    }

    @Test
    @Throws(UnsupportedEncodingException::class)
    fun shouldConvertUnicodeSubstringToBufferAndBack() {
        val original = "\u03B3\u03B5\u03B9\u03AC \u03C3\u03BF\u03C5"
        val buffer = stringToByteBuffer(original, 2, original.length - 5, Charsets.UTF_16LE)
        val converted = byteBufferToString(buffer, 0, buffer.size, Charsets.UTF_16LE)
        Assert.assertEquals("\u03B9\u03AC ", converted)
    }

    @Test
    @Throws(UnsupportedEncodingException::class)
    fun shouldThrowAnExceptionWhenConvertingStringToBytesWithOffsetOutOfRange() {
        val original = "1234567890QWERTYUIOP"
        try {
            stringToByteBuffer(original, -1, 1)
            Assert.fail("StringIndexOutOfBoundsException expected but not thrown")
        } catch (e: StringIndexOutOfBoundsException) { /* expected*/
        }
        try {
            stringToByteBuffer(original, original.length, 1)
            Assert.fail("StringIndexOutOfBoundsException expected but not thrown")
        } catch (e: StringIndexOutOfBoundsException) { /* expected*/
        }
    }

    @Test
    @Throws(UnsupportedEncodingException::class)
    fun shouldThrowAnExceptionWhenConvertingStringToBytesWithLengthOutOfRange() {
        val original = "1234567890QWERTYUIOP"
        try {
            stringToByteBuffer(original, 0, -1)
            Assert.fail("StringIndexOutOfBoundsException expected but not thrown")
        } catch (e: StringIndexOutOfBoundsException) { /* expected*/
        }
        try {
            stringToByteBuffer(original, 0, original.length + 1)
            Assert.fail("StringIndexOutOfBoundsException expected but not thrown")
        } catch (e: StringIndexOutOfBoundsException) { /* expected*/
        }
        try {
            stringToByteBuffer(original, 3, original.length - 2)
            Assert.fail("StringIndexOutOfBoundsException expected but not thrown")
        } catch (e: StringIndexOutOfBoundsException) { /* expected*/
        }
    }

    // string into existing byte buffer
    @Test
    @Throws(UnsupportedEncodingException::class)
    fun shouldCopyStringToStartOfByteBuffer() {
        val buffer = ByteArray(10)
        Arrays.fill(buffer, 0.toByte())
        val s = "TAG-"
        stringIntoByteBuffer(s, 0, s.length, buffer, 0)
        val expectedBuffer = byteArrayOf(BYTE_T, BYTE_A, BYTE_G, BYTE_DASH, 0, 0, 0, 0, 0, 0)
        Assert.assertArrayEquals(expectedBuffer, buffer)
    }

    @Test
    @Throws(UnsupportedEncodingException::class)
    fun shouldCopyUnicodeStringToStartOfByteBuffer() {
        val buffer = ByteArray(10)
        Arrays.fill(buffer, 0.toByte())
        val s = "\u03B3\u03B5\u03B9\u03AC"
        stringIntoByteBuffer(s, 0, s.length, buffer, 0, Charsets.UTF_16BE)
        val expectedBuffer = byteArrayOf(
            0x03,
            0xb3.toByte(),
            0x03,
            0xb5.toByte(),
            0x03,
            0xb9.toByte(),
            0x03,
            0xac.toByte(),
            0,
            0
        )
        Assert.assertArrayEquals(expectedBuffer, buffer)
    }

    @Test
    @Throws(UnsupportedEncodingException::class)
    fun shouldCopyStringToEndOfByteBuffer() {
        val buffer = ByteArray(10)
        Arrays.fill(buffer, 0.toByte())
        val s = "TAG-"
        stringIntoByteBuffer(s, 0, s.length, buffer, 6)
        val expectedBuffer = byteArrayOf(0, 0, 0, 0, 0, 0, BYTE_T, BYTE_A, BYTE_G, BYTE_DASH)
        Assert.assertArrayEquals(expectedBuffer, buffer)
    }

    @Test
    @Throws(UnsupportedEncodingException::class)
    fun shouldCopyUnicodeStringToEndOfByteBuffer() {
        val buffer = ByteArray(10)
        Arrays.fill(buffer, 0.toByte())
        val s = "\u03B3\u03B5\u03B9\u03AC"
        stringIntoByteBuffer(s, 0, s.length, buffer, 2, Charsets.UTF_16BE)
        val expectedBuffer = byteArrayOf(
            0,
            0,
            0x03,
            0xb3.toByte(),
            0x03,
            0xb5.toByte(),
            0x03,
            0xb9.toByte(),
            0x03,
            0xac.toByte()
        )
        Assert.assertArrayEquals(expectedBuffer, buffer)
    }

    @Test
    @Throws(UnsupportedEncodingException::class)
    fun shouldCopySubstringToStartOfByteBuffer() {
        val buffer = ByteArray(10)
        Arrays.fill(buffer, 0.toByte())
        val s = "TAG-"
        stringIntoByteBuffer(s, 1, 2, buffer, 0)
        val expectedBuffer = byteArrayOf(BYTE_A, BYTE_G, 0, 0, 0, 0, 0, 0, 0, 0)
        Assert.assertArrayEquals(expectedBuffer, buffer)
    }

    @Test
    @Throws(UnsupportedEncodingException::class)
    fun shouldCopyUnicodeSubstringToStartOfByteBuffer() {
        val buffer = ByteArray(10)
        Arrays.fill(buffer, 0.toByte())
        val s = "\u03B3\u03B5\u03B9\u03AC"
        stringIntoByteBuffer(s, 1, 2, buffer, 0, Charsets.UTF_16BE)
        val expectedBuffer = byteArrayOf(0x03, 0xb5.toByte(), 0x03, 0xb9.toByte(), 0, 0, 0, 0, 0, 0)
        Assert.assertArrayEquals(expectedBuffer, buffer)
    }

    @Test
    @Throws(UnsupportedEncodingException::class)
    fun shouldCopySubstringToMiddleOfByteBuffer() {
        val buffer = ByteArray(10)
        Arrays.fill(buffer, 0.toByte())
        val s = "TAG-"
        stringIntoByteBuffer(s, 1, 2, buffer, 4)
        val expectedBuffer = byteArrayOf(0, 0, 0, 0, BYTE_A, BYTE_G, 0, 0, 0, 0)
        Assert.assertArrayEquals(expectedBuffer, buffer)
    }

    @Test
    @Throws(UnsupportedEncodingException::class)
    fun shouldRaiseExceptionWhenCopyingStringIntoByteBufferWithOffsetOutOfRange() {
        val buffer = ByteArray(10)
        val s = "TAG-"
        try {
            stringIntoByteBuffer(s, -1, 1, buffer, 0)
            Assert.fail("StringIndexOutOfBoundsException expected but not thrown")
        } catch (e: StringIndexOutOfBoundsException) { /* expected*/
        }
        try {
            stringIntoByteBuffer(s, s.length, 1, buffer, 0)
            Assert.fail("StringIndexOutOfBoundsException expected but not thrown")
        } catch (e: StringIndexOutOfBoundsException) { /* expected*/
        }
    }

    @Test
    @Throws(UnsupportedEncodingException::class)
    fun shouldRaiseExceptionWhenCopyingStringIntoByteBufferWithLengthOutOfRange() {
        val buffer = ByteArray(10)
        val s = "TAG-"
        try {
            stringIntoByteBuffer(s, 0, -1, buffer, 0)
            Assert.fail("StringIndexOutOfBoundsException expected but not thrown")
        } catch (e: StringIndexOutOfBoundsException) { /* expected*/
        }
        try {
            stringIntoByteBuffer(s, 0, s.length + 1, buffer, 0)
            Assert.fail("StringIndexOutOfBoundsException expected but not thrown")
        } catch (e: StringIndexOutOfBoundsException) { /* expected*/
        }
        try {
            stringIntoByteBuffer(s, 3, s.length - 2, buffer, 0)
            Assert.fail("StringIndexOutOfBoundsException expected but not thrown")
        } catch (e: StringIndexOutOfBoundsException) { /* expected*/
        }
    }

    @Test
    @Throws(UnsupportedEncodingException::class)
    fun shouldRaiseExceptionWhenCopyingStringIntoByteBufferWithDestinationOffsetOutOfRange() {
        val buffer = ByteArray(10)
        val s = "TAG-"
        try {
            stringIntoByteBuffer(s, 0, 1, buffer, 10)
            Assert.fail("ArrayIndexOutOfBoundsException expected but not thrown")
        } catch (e: ArrayIndexOutOfBoundsException) { /* expected*/
        }
        try {
            stringIntoByteBuffer(s, 0, s.length, buffer, buffer.size - s.length + 1)
            Assert.fail("ArrayIndexOutOfBoundsException expected but not thrown")
        } catch (e: ArrayIndexOutOfBoundsException) { /* expected*/
        }
    }

    // trim strings
    @Test
    @Throws(UnsupportedEncodingException::class)
    fun shouldRightTrimStringsCorrectly() {
        Assert.assertEquals("", trimStringRight(""))
        Assert.assertEquals("", trimStringRight(" "))
        Assert.assertEquals("TEST", trimStringRight("TEST"))
        Assert.assertEquals("TEST", trimStringRight("TEST   "))
        Assert.assertEquals("   TEST", trimStringRight("   TEST"))
        Assert.assertEquals("   TEST", trimStringRight("   TEST   "))
        Assert.assertEquals("TEST", trimStringRight("TEST\t\r\n"))
        Assert.assertEquals(
            "TEST",
            trimStringRight("TEST" + byteBufferToString(byteArrayOf(0, 0), 0, 2))
        )
    }

    @Test
    @Throws(UnsupportedEncodingException::class)
    fun shouldRightTrimUnicodeStringsCorrectly() {
        Assert.assertEquals("\u03B3\u03B5\u03B9\u03AC", trimStringRight("\u03B3\u03B5\u03B9\u03AC"))
        Assert.assertEquals(
            "\u03B3\u03B5\u03B9\u03AC",
            trimStringRight("\u03B3\u03B5\u03B9\u03AC   ")
        )
        Assert.assertEquals(
            "   \u03B3\u03B5\u03B9\u03AC",
            trimStringRight("   \u03B3\u03B5\u03B9\u03AC")
        )
        Assert.assertEquals(
            "   \u03B3\u03B5\u03B9\u03AC",
            trimStringRight("   \u03B3\u03B5\u03B9\u03AC   ")
        )
        Assert.assertEquals(
            "\u03B3\u03B5\u03B9\u03AC",
            trimStringRight("\u03B3\u03B5\u03B9\u03AC\t\r\n")
        )
        Assert.assertEquals(
            "\u03B3\u03B5\u03B9\u03AC",
            trimStringRight(
                "\u03B3\u03B5\u03B9\u03AC" + byteBufferToString(
                    byteArrayOf(0, 0),
                    0,
                    2
                )
            )
        )
    }

    @Test
    fun shouldRightPadStringsCorrectly() {
        Assert.assertEquals("1234", padStringRight("1234", 3, ' '))
        Assert.assertEquals("123", padStringRight("123", 3, ' '))
        Assert.assertEquals("12 ", padStringRight("12", 3, ' '))
        Assert.assertEquals("1  ", padStringRight("1", 3, ' '))
        Assert.assertEquals("   ", padStringRight("", 3, ' '))
    }

    @Test
    fun shouldRightPadUnicodeStringsCorrectly() {
        Assert.assertEquals(
            "\u03B3\u03B5\u03B9\u03AC",
            padStringRight("\u03B3\u03B5\u03B9\u03AC", 3, ' ')
        )
        Assert.assertEquals("\u03B3\u03B5\u03B9", padStringRight("\u03B3\u03B5\u03B9", 3, ' '))
        Assert.assertEquals("\u03B3\u03B5 ", padStringRight("\u03B3\u03B5", 3, ' '))
        Assert.assertEquals("\u03B3  ", padStringRight("\u03B3", 3, ' '))
    }

    @Test
    fun shouldPadRightWithNullCharacters() {
        Assert.assertEquals("123", padStringRight("123", 3, '\u0000'))
        Assert.assertEquals("12\u0000", padStringRight("12", 3, '\u0000'))
        Assert.assertEquals("1\u0000\u0000", padStringRight("1", 3, '\u0000'))
        Assert.assertEquals("\u0000\u0000\u0000", padStringRight("", 3, '\u0000'))
    }

    @Test
    fun shouldExtractBitsCorrectly() {
        val b: Byte = -0x36 // 11001010
        Assert.assertFalse(checkBit(b, 0))
        Assert.assertTrue(checkBit(b, 1))
        Assert.assertFalse(checkBit(b, 2))
        Assert.assertTrue(checkBit(b, 3))
        Assert.assertFalse(checkBit(b, 4))
        Assert.assertFalse(checkBit(b, 5))
        Assert.assertTrue(checkBit(b, 6))
        Assert.assertTrue(checkBit(b, 7))
    }

    @Test
    fun shouldSetBitsInBytesCorrectly() {
        val b: Byte = -0x36 // 11001010
        Assert.assertEquals(-0x36, setBit(b, 7, true).toLong()) // 11010010
        Assert.assertEquals(-0x35, setBit(b, 0, true).toLong()) // 11001011
        Assert.assertEquals(-0x26, setBit(b, 4, true).toLong()) // 11011010
        Assert.assertEquals(-0x36, setBit(b, 0, false).toLong()) // 11010010
        Assert.assertEquals(0x4A, setBit(b, 7, false).toLong()) // 01001010
        Assert.assertEquals(-0x3E, setBit(b, 3, false).toLong()) // 11000010
    }

    @Test
    fun shiftByteLeftTest() {
        val num = 1.toByte();
        val shift = -1
        val actual = shiftByte(num, shift)
        var expected = 2
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun shiftByteRightTest() {
        val num = 64.toByte();
        val shift = 2
        val actual = shiftByte(num, shift)
        var expected = 16
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun shouldUnpackIntegerCorrectly() {
        Assert.assertEquals(-0x46fbc, unpackInteger(BYTE_FF, BYTE_FB, BYTE_90, BYTE_44).toLong())
        Assert.assertEquals(
            0x00000081,
            unpackInteger(0.toByte(), 0.toByte(), 0.toByte(), BYTE_81).toLong()
        )
        Assert.assertEquals(
            0x00000101,
            unpackInteger(0.toByte(), 0.toByte(), 1.toByte(), 1.toByte()).toLong()
        )
    }

    @Test
    fun shouldUnpackSynchsafeIntegersCorrectly() {
        Assert.assertEquals(
            1217,
            unpackSynchsafeInteger(0.toByte(), 0.toByte(), 0x09.toByte(), 0x41.toByte()).toLong()
        )
        Assert.assertEquals(
            1227,
            unpackSynchsafeInteger(0.toByte(), 0.toByte(), 0x09.toByte(), 0x4B.toByte()).toLong()
        )
        Assert.assertEquals(
            1002,
            unpackSynchsafeInteger(0.toByte(), 0.toByte(), 0x07.toByte(), 0x6A.toByte()).toLong()
        )
        Assert.assertEquals(
            0x0101,
            unpackSynchsafeInteger(0.toByte(), 0.toByte(), 2.toByte(), 1.toByte()).toLong()
        )
        Assert.assertEquals(
            0x01010101,
            unpackSynchsafeInteger(8.toByte(), 4.toByte(), 2.toByte(), 1.toByte()).toLong()
        )
    }

    @Test
    fun shouldPackIntegerCorrectly() {
        Assert.assertArrayEquals(
            byteArrayOf(BYTE_FF, BYTE_FB, BYTE_90, BYTE_44),
            packInteger(-0x46fbc)
        )
    }

    @Test
    fun shouldPackSynchsafeIntegersCorrectly() {
        Assert.assertArrayEquals(
            byteArrayOf(0.toByte(), 0.toByte(), 0x09.toByte(), 0x41.toByte()),
            packSynchsafeInteger(1217)
        )
        Assert.assertArrayEquals(
            byteArrayOf(0.toByte(), 0.toByte(), 0x09.toByte(), 0x4B.toByte()),
            packSynchsafeInteger(1227)
        )
        Assert.assertArrayEquals(
            byteArrayOf(0.toByte(), 0.toByte(), 0x07.toByte(), 0x6A.toByte()),
            packSynchsafeInteger(1002)
        )
        Assert.assertArrayEquals(
            byteArrayOf(0.toByte(), 0.toByte(), 2.toByte(), 1.toByte()),
            packSynchsafeInteger(0x0101)
        )
        Assert.assertArrayEquals(
            byteArrayOf(8.toByte(), 4.toByte(), 2.toByte(), 1.toByte()),
            packSynchsafeInteger(0x01010101)
        )
    }

    @Test
    fun shouldPackAndUnpackIntegerBackToOriginalValue() {
        val original = 12345
        val bytes = packInteger(original)
        val unpacked = unpackInteger(bytes[0], bytes[1], bytes[2], bytes[3])
        Assert.assertEquals(original.toLong(), unpacked.toLong())
    }

    @Test
    fun shouldPackAndUnpackSynchsafeIntegerBackToOriginalValue() {
        val original = 12345
        val bytes = packSynchsafeInteger(original)
        val unpacked = unpackSynchsafeInteger(bytes[0], bytes[1], bytes[2], bytes[3])
        Assert.assertEquals(original.toLong(), unpacked.toLong())
    }

    @Test
    fun shouldCopyBuffersWithValidOffsetsAndLengths() {
        val buffer = byteArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
        Assert.assertArrayEquals(
            byteArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9),
            copyBuffer(buffer, 0, buffer.size)
        )
        Assert.assertArrayEquals(
            byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9),
            copyBuffer(buffer, 1, buffer.size - 1)
        )
        Assert.assertArrayEquals(
            byteArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8),
            copyBuffer(buffer, 0, buffer.size - 1)
        )
        Assert.assertArrayEquals(
            byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8),
            copyBuffer(buffer, 1, buffer.size - 2)
        )
        Assert.assertArrayEquals(byteArrayOf(4), copyBuffer(buffer, 4, 1))
    }

    @Test
    fun throwsExceptionWhenCopyingBufferWithInvalidOffsetAndOrLength() {
        val buffer = byteArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9)
        try {
            copyBuffer(buffer, -1, buffer.size)
            Assert.fail("ArrayIndexOutOfBoundsException expected but not thrown")
        } catch (e: ArrayIndexOutOfBoundsException) { /* expected*/
        }
        try {
            copyBuffer(buffer, buffer.size, 1)
            Assert.fail("ArrayIndexOutOfBoundsException expected but not thrown")
        } catch (e: ArrayIndexOutOfBoundsException) { /* expected*/
        }
        try {
            copyBuffer(buffer, 1, buffer.size)
            Assert.fail("ArrayIndexOutOfBoundsException expected but not thrown")
        } catch (e: ArrayIndexOutOfBoundsException) { /* expected*/
        }
    }

    @Test
    fun shouldDetermineUnsynchronisationSizesCorrectly() {
        Assert.assertEquals(0, sizeUnsynchronisationWouldAdd(byteArrayOf()).toLong())
        Assert.assertEquals(
            0,
            sizeUnsynchronisationWouldAdd(byteArrayOf(BYTE_FF, 1, BYTE_FB)).toLong()
        )
        Assert.assertEquals(
            1,
            sizeUnsynchronisationWouldAdd(byteArrayOf(BYTE_FF, BYTE_FB)).toLong()
        )
        Assert.assertEquals(
            1,
            sizeUnsynchronisationWouldAdd(byteArrayOf(0, BYTE_FF, BYTE_FB, 0)).toLong()
        )
        Assert.assertEquals(1, sizeUnsynchronisationWouldAdd(byteArrayOf(0, BYTE_FF)).toLong())
        Assert.assertEquals(
            2,
            sizeUnsynchronisationWouldAdd(
                byteArrayOf(
                    BYTE_FF,
                    BYTE_FB,
                    0,
                    BYTE_FF,
                    BYTE_FB
                )
            ).toLong()
        )
        Assert.assertEquals(
            3,
            sizeUnsynchronisationWouldAdd(byteArrayOf(BYTE_FF, BYTE_FF, BYTE_FF)).toLong()
        )
    }

    @Test
    fun shouldDetermineSynchronisationSizesCorrectly() {
        Assert.assertEquals(0, sizeSynchronisationWouldSubtract(byteArrayOf()).toLong())
        Assert.assertEquals(
            0,
            sizeSynchronisationWouldSubtract(byteArrayOf(BYTE_FF, 1, BYTE_FB)).toLong()
        )
        Assert.assertEquals(
            1,
            sizeSynchronisationWouldSubtract(byteArrayOf(BYTE_FF, 0, BYTE_FB)).toLong()
        )
        Assert.assertEquals(
            1,
            sizeSynchronisationWouldSubtract(byteArrayOf(0, BYTE_FF, 0, BYTE_FB, 0)).toLong()
        )
        Assert.assertEquals(
            1,
            sizeSynchronisationWouldSubtract(byteArrayOf(0, BYTE_FF, 0)).toLong()
        )
        Assert.assertEquals(
            2,
            sizeSynchronisationWouldSubtract(
                byteArrayOf(
                    BYTE_FF,
                    0,
                    BYTE_FB,
                    0,
                    BYTE_FF,
                    0,
                    BYTE_FB
                )
            ).toLong()
        )
        Assert.assertEquals(
            3,
            sizeSynchronisationWouldSubtract(
                byteArrayOf(
                    BYTE_FF,
                    0,
                    BYTE_FF,
                    0,
                    BYTE_FF,
                    0
                )
            ).toLong()
        )
    }

    @Test
    fun shouldUnsynchroniseThenSynchroniseFFExBytesCorrectly() {
        val buffer = byteArrayOf(
            BYTE_FF,
            BYTE_FB,
            2,
            3,
            4,
            BYTE_FF,
            BYTE_E0,
            7,
            8,
            9,
            10,
            11,
            12,
            13,
            BYTE_FF,
            BYTE_F0
        )
        val expectedBuffer = byteArrayOf(
            BYTE_FF,
            0,
            BYTE_FB,
            2,
            3,
            4,
            BYTE_FF,
            0,
            BYTE_E0,
            7,
            8,
            9,
            10,
            11,
            12,
            13,
            BYTE_FF,
            0,
            BYTE_F0
        )
        val unsynchronised = unsynchroniseBuffer(buffer)
        val synchronised = synchroniseBuffer(unsynchronised)
        Assert.assertArrayEquals(expectedBuffer, unsynchronised)
        Assert.assertArrayEquals(buffer, synchronised)
    }

    @Test
    fun shouldUnsynchroniseThenSynchroniseFF00BytesCorrectly() {
        val buffer =
            byteArrayOf(BYTE_FF, 0, 2, 3, 4, BYTE_FF, 0, 7, 8, 9, 10, 11, 12, 13, BYTE_FF, 0)
        val expectedBuffer = byteArrayOf(
            BYTE_FF,
            0,
            0,
            2,
            3,
            4,
            BYTE_FF,
            0,
            0,
            7,
            8,
            9,
            10,
            11,
            12,
            13,
            BYTE_FF,
            0,
            0
        )
        val unsynchronised = unsynchroniseBuffer(buffer)
        val synchronised = synchroniseBuffer(unsynchronised)
        Assert.assertArrayEquals(expectedBuffer, unsynchronised)
        Assert.assertArrayEquals(buffer, synchronised)
    }

    @Test
    fun shouldUnsynchroniseThenSynchroniseBufferFullOfFFsCorrectly() {
        val buffer = byteArrayOf(BYTE_FF, BYTE_FF, BYTE_FF, BYTE_FF)
        val expectedBuffer = byteArrayOf(BYTE_FF, 0, BYTE_FF, 0, BYTE_FF, 0, BYTE_FF, 0)
        val unsynchronised = unsynchroniseBuffer(buffer)
        val synchronised = synchroniseBuffer(unsynchronised)
        Assert.assertArrayEquals(expectedBuffer, unsynchronised)
        Assert.assertArrayEquals(buffer, synchronised)
    }

    @Test
    fun shouldUnsynchroniseThenSynchroniseBufferMinimalBufferCorrectly() {
        val buffer = byteArrayOf(BYTE_FF)
        val expectedBuffer = byteArrayOf(BYTE_FF, 0)
        val unsynchronised = unsynchroniseBuffer(buffer)
        val synchronised = synchroniseBuffer(unsynchronised)
        Assert.assertArrayEquals(expectedBuffer, unsynchronised)
        Assert.assertArrayEquals(buffer, synchronised)
    }

    @Test
    fun shouldReturnOriginalBufferIfNoUnynchronisationOrSynchronisationIsRequired() {
        val buffer = byteArrayOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
        val unsynchronised = unsynchroniseBuffer(buffer)
        val synchronised = synchroniseBuffer(buffer)
        Assert.assertEquals(buffer, unsynchronised)
        Assert.assertEquals(buffer, synchronised)
    }

    @Test
    fun shouldReplaceTokensWithSpecifiedStrings() {
        val source = "%1-%2 something %1-%3"
        Assert.assertEquals("ONE-%2 something ONE-%3", substitute(source, "%1", "ONE"))
        Assert.assertEquals("%1-TWO something %1-%3", substitute(source, "%2", "TWO"))
        Assert.assertEquals("%1-%2 something %1-THREE", substitute(source, "%3", "THREE"))
    }

    @Test
    fun shouldReturnOriginalStringIfTokenToSubstituteDoesNotExistInString() {
        val source = "%1-%2 something %1-%3"
        Assert.assertEquals("%1-%2 something %1-%3", substitute(source, "%X", "XXXXX"))
    }

    @Test
    fun shouldReturnOriginalStringForSubstitutionWithEmptyString() {
        val source = "%1-%2 something %1-%3"
        Assert.assertEquals("%1-%2 something %1-%3", substitute(source, "", "WHATEVER"))
    }

    @Test
    fun shouldSubstituteEmptyStringWhenDestinationStringIsNull() {
        val source = "%1-%2 something %1-%3"
        Assert.assertEquals("-%2 something -%3", substitute(source, "%1", null))
    }

    @Test
    fun shouldConvertNonAsciiCharactersToQuestionMarksInString() {
        Assert.assertEquals("?12?34?567???89?", asciiOnly("ü12¬34ü567¬¬¬89ü"))
    }

    @Test
    @Throws(UnsupportedEncodingException::class)
    fun convertsBufferContainingHighAscii() {
        val buffer = byteArrayOf(BYTE_T, BYTE_ESZETT, BYTE_G)
        Assert.assertEquals("T" + 223.toChar() + "G", byteBufferToString(buffer, 0, 3))
    }

    // finding terminators
    @Test
    fun findsSingleTerminator() {
        val buffer = byteArrayOf(BYTE_T, BYTE_ESZETT, BYTE_G, BYTE_T, 0, BYTE_G, BYTE_A)
        Assert.assertEquals(4, indexOfTerminator(buffer, 0, 1).toLong())
    }

    @Test
    fun findsFirstSingleTerminator() {
        val buffer =
            byteArrayOf(BYTE_T, BYTE_ESZETT, BYTE_G, BYTE_T, 0, BYTE_G, BYTE_A, 0, BYTE_G, BYTE_A)
        Assert.assertEquals(4, indexOfTerminator(buffer, 0, 1).toLong())
    }

    @Test
    fun findsFirstSingleTerminatorAfterFromIndex() {
        val buffer =
            byteArrayOf(BYTE_T, BYTE_ESZETT, BYTE_G, BYTE_T, 0, BYTE_G, BYTE_A, 0, BYTE_G, BYTE_A)
        Assert.assertEquals(7, indexOfTerminator(buffer, 5, 1).toLong())
    }

    @Test
    fun findsSingleTerminatorWhenFirstElement() {
        val buffer = byteArrayOf(0, BYTE_T, BYTE_ESZETT, BYTE_G, BYTE_T)
        Assert.assertEquals(0, indexOfTerminator(buffer, 0, 1).toLong())
    }

    @Test
    fun findsSingleTerminatorWhenLastElement() {
        val buffer = byteArrayOf(BYTE_T, BYTE_ESZETT, BYTE_G, BYTE_T, 0)
        Assert.assertEquals(4, indexOfTerminator(buffer, 0, 1).toLong())
    }

    @Test
    fun ReturnsMinusOneWhenNoSingleTerminator() {
        val buffer = byteArrayOf(BYTE_T, BYTE_ESZETT, BYTE_G, BYTE_T)
        Assert.assertEquals(-1, indexOfTerminator(buffer, 0, 1).toLong())
    }

    @Test
    fun findsDoubleTerminator() {
        val buffer = byteArrayOf(BYTE_T, 0, BYTE_G, BYTE_T, 0, 0, BYTE_G, BYTE_A)
        Assert.assertEquals(4, indexOfTerminator(buffer, 0, 2).toLong())
    }

    @Test
    fun findsNotFindDoubleTerminatorIfNotOnEvenByte() {
        val buffer = byteArrayOf(BYTE_T, 0, BYTE_G, BYTE_T, BYTE_T, 0, 0, BYTE_G, BYTE_A)
        Assert.assertEquals(-1, indexOfTerminator(buffer, 0, 2).toLong())
    }

    @Test
    fun findsFirstDoubleTerminator() {
        val buffer = byteArrayOf(
            BYTE_T,
            BYTE_ESZETT,
            BYTE_G,
            BYTE_T,
            0,
            0,
            BYTE_G,
            BYTE_A,
            0,
            0,
            BYTE_G,
            BYTE_A
        )
        Assert.assertEquals(4, indexOfTerminator(buffer, 0, 2).toLong())
    }

    @Test
    fun findsFirstDoubleTerminatorOnAnEvenByte() {
        val buffer = byteArrayOf(
            BYTE_T,
            BYTE_ESZETT,
            BYTE_G,
            0,
            0,
            BYTE_T,
            BYTE_G,
            BYTE_A,
            0,
            0,
            BYTE_G,
            BYTE_A
        )
        Assert.assertEquals(8, indexOfTerminator(buffer, 0, 2).toLong())
    }

    @Test
    fun findsFirstDoubleTerminatorAfterFromIndex() {
        val buffer = byteArrayOf(
            BYTE_T,
            BYTE_ESZETT,
            BYTE_G,
            BYTE_T,
            0,
            0,
            BYTE_G,
            BYTE_A,
            0,
            0,
            BYTE_G,
            BYTE_A
        )
        Assert.assertEquals(8, indexOfTerminator(buffer, 6, 2).toLong())
    }

    @Test
    fun findsDoubleTerminatorWhenFirstElement() {
        val buffer = byteArrayOf(0, 0, BYTE_T, BYTE_ESZETT, BYTE_G, BYTE_T)
        Assert.assertEquals(0, indexOfTerminator(buffer, 0, 2).toLong())
    }

    @Test
    fun findsDoubleTerminatorWhenLastElement() {
        val buffer = byteArrayOf(BYTE_T, BYTE_ESZETT, BYTE_G, BYTE_T, 0, 0)
        Assert.assertEquals(4, indexOfTerminator(buffer, 0, 2).toLong())
    }

    @Test
    fun returnsMinusOneWhenNoDoubleTerminator() {
        val buffer = byteArrayOf(BYTE_T, BYTE_ESZETT, BYTE_G, BYTE_T)
        Assert.assertEquals(-1, indexOfTerminator(buffer, 0, 2).toLong())
    }

    companion object {
        private const val BYTE_T: Byte = 0x54
        private const val BYTE_A: Byte = 0x41
        private const val BYTE_G: Byte = 0x47
        private const val BYTE_DASH: Byte = 0x2D
        private const val BYTE_FF: Byte = -0x01
        private const val BYTE_FB: Byte = -0x05
        private const val BYTE_90: Byte = -0x70
        private const val BYTE_44: Byte = 0x44
        private const val BYTE_E0: Byte = -0x20
        private const val BYTE_F0: Byte = -0x10
        private const val BYTE_81: Byte = -0x7F
        private const val BYTE_ESZETT: Byte = -0x21
    }
}