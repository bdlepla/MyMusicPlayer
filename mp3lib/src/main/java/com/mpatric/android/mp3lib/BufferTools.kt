package com.mpatric.mp3agic

import java.io.UnsupportedEncodingException
import java.nio.charset.Charset
import kotlin.experimental.and
import kotlin.experimental.inv
import kotlin.experimental.or

object BufferTools {
    private val defaultCharsetName = Charsets.ISO_8859_1
    @JvmStatic
    fun byteBufferToStringIgnoringEncodingIssues(
        bytes: ByteArray,
        offset: Int,
        length: Int
    ): String {
        return try {
            byteBufferToString(bytes, offset, length, defaultCharsetName)
        } catch (e: UnsupportedEncodingException) {
            ""
        }
    }

    @JvmStatic
    @JvmOverloads
    @Throws(UnsupportedEncodingException::class)
    fun byteBufferToString(
        bytes: ByteArray,
        offset: Int,
        length: Int,
        charsetName: Charset = defaultCharsetName
    ): String {
        return if (length < 1) "" else String(bytes, offset, length, charsetName)
    }

    @JvmStatic
    @JvmOverloads
    @Throws(UnsupportedEncodingException::class)
    fun stringToByteBuffer(
        s: String,
        offset: Int,
        length: Int,
        charsetName: Charset = defaultCharsetName
    ): ByteArray {
        val stringToCopy = s.substring(offset, offset + length)
        return stringToCopy.toByteArray(charsetName)
    }

    @JvmStatic
    @JvmOverloads
    @Throws(UnsupportedEncodingException::class)
    fun stringIntoByteBuffer(
        s: String,
        offset: Int,
        length: Int,
        bytes: ByteArray,
        destOffset: Int,
        charsetName: Charset = defaultCharsetName
    ) {
        val stringToCopy = s.substring(offset, offset + length)
        val srcBytes = stringToCopy.toByteArray(charsetName)
        if (srcBytes.size > 0) {
            System.arraycopy(srcBytes, 0, bytes, destOffset, srcBytes.size)
        }
    }

    @JvmStatic
    fun trimStringRight(s: String): String {
        var endPosition = s.length - 1
        var endChar: Char
        while (endPosition >= 0) {
            endChar = s[endPosition]
            if (endChar.toInt() > 32) {
                break
            }
            endPosition--
        }
        if (endPosition == s.length - 1) return s else if (endPosition < 0) return ""
        return s.substring(0, endPosition + 1)
    }

    @JvmStatic
    fun padStringRight(s: String, length: Int, padWith: Char): String {
        if (s.length >= length) return s
        val stringBuffer = StringBuilder(s)
        while (stringBuffer.length < length) {
            stringBuffer.append(padWith)
        }
        return stringBuffer.toString()
    }

    @JvmStatic
    fun checkBit(b: Byte, bitPosition: Int): Boolean {
        return b and (0x01.toByte() shl bitPosition) != 0x00.toByte()
    }

    @JvmStatic
    fun setBit(b: Byte, bitPosition: Int, value: Boolean): Byte {
        val newByte: Byte = if (value) {
            (b or (0x01.toByte() shl bitPosition))
        } else {
            (b and (0x01.toByte() shl bitPosition).inv())
        }.toByte()
        return newByte
    }

    fun shiftByte(c: Byte, places: Int): Int {
        val i: Int = c.toInt() and 0xff
        if (places < 0) {
            return i shl -places
        } else if (places > 0) {
            return i shr places
        }
        return i
    }

    @JvmStatic
    fun unpackInteger(b1: Byte, b2: Byte, b3: Byte, b4: Byte): Int {
        var value: Int = b4.toInt() and 0xff
        value += shiftByte(b3, -8)
        value += shiftByte(b2, -16)
        value += shiftByte(b1, -24)
        return value
    }

    @JvmStatic
    fun packInteger(i: Int): ByteArray {
        val bytes = ByteArray(4)
        bytes[3] = (i and 0xff).toByte()
        bytes[2] = (i shr 8 and 0xff).toByte()
        bytes[1] = (i shr 16 and 0xff).toByte()
        bytes[0] = (i shr 24 and 0xff).toByte()
        return bytes
    }

    @JvmStatic
    fun unpackSynchsafeInteger(b1: Byte, b2: Byte, b3: Byte, b4: Byte): Int {
        var value = (b4 and 0x7f).toInt()
        value += shiftByte((b3 and 0x7f), -7)
        value += shiftByte((b2 and 0x7f), -14)
        value += shiftByte((b1 and 0x7f), -21)
        return value
    }

    @JvmStatic
    fun packSynchsafeInteger(i: Int): ByteArray {
        val bytes = ByteArray(4)
        packSynchsafeInteger(i, bytes, 0)
        return bytes
    }

    @JvmStatic
    fun packSynchsafeInteger(i: Int, bytes: ByteArray, offset: Int) {
        bytes[offset + 3] = (i and 0x7f).toByte()
        bytes[offset + 2] = (i shr 7 and 0x7f).toByte()
        bytes[offset + 1] = (i shr 14 and 0x7f).toByte()
        bytes[offset + 0] = (i shr 21 and 0x7f).toByte()
    }

    @JvmStatic
    fun copyBuffer(bytes: ByteArray, offset: Int, length: Int): ByteArray {
        val copy = ByteArray(length)
        if (length > 0) {
            System.arraycopy(bytes, offset, copy, 0, length)
        }
        return copy
    }

    @JvmStatic
    fun copyIntoByteBuffer(
        bytes: ByteArray,
        offset: Int,
        length: Int,
        destBuffer: ByteArray?,
        destOffset: Int
    ) {
        if (length > 0) {
            System.arraycopy(bytes, offset, destBuffer, destOffset, length)
        }
    }

    @JvmStatic
    fun sizeUnsynchronisationWouldAdd(bytes: ByteArray): Int {
        var count = 0
        for (i in 0 until bytes.size - 1) {
            if (bytes[i] == 0xff.toByte() && (bytes[i + 1] and 0xe0.toByte() == 0xe0.toByte() || bytes[i + 1] == 0.toByte())){
                count++
            }
        }
        if (bytes.size > 0 && bytes[bytes.size - 1] == 0xff.toByte()) count++
        return count
    }

    @JvmStatic
    fun unsynchroniseBuffer(bytes: ByteArray): ByteArray {
        // unsynchronisation is replacing instances of:
        // 11111111 111xxxxx with 11111111 00000000 111xxxxx and
        // 11111111 00000000 with 11111111 00000000 00000000
        val count = sizeUnsynchronisationWouldAdd(bytes)
        if (count == 0) return bytes
        val newBuffer = ByteArray(bytes.size + count)
        var j = 0
        for (i in 0 until bytes.size - 1) {
            newBuffer[j++] = bytes[i]
            if (bytes[i] == 0xff.toByte() && ((bytes[i + 1] and 0xe0.toByte()) == 0xe0.toByte()  || bytes[i + 1] == 0.toByte())){
                newBuffer[j++] = 0
            }
        }
        newBuffer[j++] = bytes[bytes.size - 1]
        if (bytes[bytes.size - 1] == 0xff.toByte()) {
            newBuffer[j] = 0
        }
        return newBuffer
    }

    @JvmStatic
    fun sizeSynchronisationWouldSubtract(bytes: ByteArray): Int {
        var count = 0
        for (i in 0 until bytes.size - 2) {
            if (bytes[i] == 0xff.toByte() && bytes[i + 1] == 0.toByte() && (bytes[i + 2] and 0xe0.toByte() == 0xe0.toByte() || bytes[i + 2] == 0.toByte())){
                count++
            }
        }
        if (bytes.size > 1 && bytes[bytes.size - 2] == 0xff.toByte() && bytes[bytes.size - 1] == 0.toByte()) count++
        return count
    }

    @JvmStatic
    fun synchroniseBuffer(bytes: ByteArray): ByteArray {
        // synchronisation is replacing instances of:
        // 11111111 00000000 111xxxxx with 11111111 111xxxxx and
        // 11111111 00000000 00000000 with 11111111 00000000
        val count = sizeSynchronisationWouldSubtract(bytes)
        if (count == 0) return bytes
        val newBuffer = ByteArray(bytes.size - count)
        var i = 0
        for (j in 0 until newBuffer.size - 1) {
            newBuffer[j] = bytes[i]
            if (bytes[i] == 0xff.toByte() && bytes[i + 1] == 0.toByte() && (bytes[i + 2] and 0xe0.toByte() == 0xe0.toByte() || bytes[i + 2] == 0.toByte())){
                i++
            }
            i++
        }
        newBuffer[newBuffer.size - 1] = bytes[i]
        return newBuffer
    }

    @JvmStatic
    fun substitute(s: String, replaceThis: String, withThis: String?): String {
        if (replaceThis.length < 1 || !s.contains(replaceThis)) {
            return s
        }
        val newString = StringBuilder()
        var lastPosition = 0
        var position = 0
        while (s.indexOf(replaceThis, position).also { position = it } >= 0) {
            if (position > lastPosition) {
                newString.append(s.substring(lastPosition, position))
            }
            if (withThis != null) {
                newString.append(withThis)
            }
            lastPosition = position + replaceThis.length
            position++
        }
        if (lastPosition < s.length) {
            newString.append(s.substring(lastPosition))
        }
        return newString.toString()
    }

    @JvmStatic
    fun asciiOnly(s: String): String {
        val newString = StringBuilder()
        for (i in 0 until s.length) {
            val ch = s[i]
            if (ch.toInt() < 32 || ch.toInt() > 126) {
                newString.append('?')
            } else {
                newString.append(ch)
            }
        }
        return newString.toString()
    }


    fun indexOfTerminator(bytes: ByteArray, fromIndex: Int = 0): Int {
        return indexOfTerminator(bytes, 0, 1)
    }

    @JvmStatic
    fun indexOfTerminator(bytes: ByteArray, fromIndex: Int, terminatorLength: Int): Int {
        var marker = -1
        for (i in fromIndex..bytes.size - terminatorLength) {
            if ((i - fromIndex) % terminatorLength == 0) {
                var matched: Int
                matched = 0
                while (matched < terminatorLength) {
                    if (bytes[i + matched] != 0.toByte()) break
                    matched++
                }
                if (matched == terminatorLength) {
                    marker = i
                    break
                }
            }
        }
        return marker
    }

    fun indexOfTerminatorForEncoding(bytes: ByteArray, fromIndex: Int, encoding: Int): Int {
        val terminatorLength =
            if (encoding == EncodedText.TEXT_ENCODING_UTF_16.toInt() || encoding == EncodedText.TEXT_ENCODING_UTF_16BE.toInt()) 2 else 1
        return indexOfTerminator(bytes, fromIndex, terminatorLength)
    }
}

infix fun Byte.shl(bitPosition: Int): Byte {
    return (this.toInt() shl bitPosition).toByte()
}

