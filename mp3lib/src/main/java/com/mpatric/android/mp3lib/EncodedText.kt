package com.mpatric.mp3agic

import java.nio.ByteBuffer
import java.nio.CharBuffer
import java.nio.charset.CharacterCodingException
import java.nio.charset.Charset

open class EncodedText {
    private var value: ByteArray
    private var textEncoding: Byte = 0

    constructor(textEncoding: Byte, value: ByteArray) {
        // if encoding type 1 and big endian BOM is present, switch to big endian
        if (textEncoding == TEXT_ENCODING_UTF_16 &&
            textEncodingForBytesFromBOM(value) == TEXT_ENCODING_UTF_16BE
        ) {
            this.textEncoding = TEXT_ENCODING_UTF_16BE
        } else {
            this.textEncoding = textEncoding
        }
        this.value = value
        stripBomAndTerminator()
    }

    constructor(string: String) {
        for (textEncoding in textEncodingFallback) {
            this.textEncoding = textEncoding
            value = stringToBytes(string, characterSetForTextEncoding(textEncoding)) ?: ByteArray(0)
            stripBomAndTerminator()
            return
        }
        throw IllegalArgumentException("Invalid string, could not find appropriate encoding")
    }

    constructor(string: String, transcodeToTextEncoding: Byte) : this(string) {
        setTextEncoding(transcodeToTextEncoding)
    }

    constructor(textEncoding: Byte, string: String) {
        this.textEncoding = textEncoding
        value = stringToBytes(string, characterSetForTextEncoding(textEncoding)) ?: ByteArray(0)
        stripBomAndTerminator()
    }

    constructor(value: ByteArray) : this(textEncodingForBytesFromBOM(value), value)

    private fun stripBomAndTerminator() {
        var leadingCharsToRemove = 0
        if (value.size >= 2 && (value[0] == 0xfe.toByte() && value[1] == 0xff.toByte() || value[0] == 0xff.toByte() && value[1] == 0xfe.toByte())) {
            leadingCharsToRemove = 2
        } else if (value.size >= 3 && value[0] == 0xef.toByte() && value[1] == 0xbb.toByte() && value[2] == 0xbf.toByte()) {
            leadingCharsToRemove = 3
        }
        var trailingCharsToRemove = 0
        val terminator = terminators[textEncoding.toInt()]
        if (value.size - leadingCharsToRemove >= terminator.size) {
            var haveTerminator = true
            for (i in terminator.indices) {
                if (value[value.size - terminator.size + i] != terminator[i]) {
                    haveTerminator = false
                    break
                }
            }
            if (haveTerminator) trailingCharsToRemove = terminator.size
        }
        if (leadingCharsToRemove + trailingCharsToRemove > 0) {
            val newLength = value.size - leadingCharsToRemove - trailingCharsToRemove
            val newValue = ByteArray(newLength)
            if (newLength > 0) {
                System.arraycopy(value, leadingCharsToRemove, newValue, 0, newValue.size)
            }
            value = newValue
        }
    }

    fun getTextEncoding(): Byte {
        return textEncoding
    }

    @Throws(CharacterCodingException::class)
    fun setTextEncoding(textEncoding: Byte) {
        if (this.textEncoding != textEncoding) {
            val charBuffer =
                bytesToCharBuffer(value, characterSetForTextEncoding(this.textEncoding))
            val transcodedBytes =
                charBufferToBytes(charBuffer, characterSetForTextEncoding(textEncoding))
            this.textEncoding = textEncoding
            value = transcodedBytes
        }
    }

    val terminator: ByteArray
        get() = terminators[textEncoding.toInt()]

    @JvmOverloads
    fun toBytes(includeBom: Boolean = false, includeTerminator: Boolean = false): ByteArray {
        characterSetForTextEncoding(textEncoding) // ensured textEncoding is valid
        val newLength: Int =
            value.size + (if (includeBom) boms[textEncoding.toInt()].size else 0) + if (includeTerminator) terminator.size else 0
        return if (newLength == value.size) {value}
        else {
            val bytes = ByteArray(newLength)
            var i = 0
            if (includeBom) {
                val bom = boms[textEncoding.toInt()]
                if (bom.isNotEmpty()) {
                    System.arraycopy(
                        boms[textEncoding.toInt()],
                        0,
                        bytes,
                        i,
                        boms[textEncoding.toInt()].size
                    )
                    i += boms[textEncoding.toInt()].size
                }
            }
            if (value.isNotEmpty()) {
                System.arraycopy(value, 0, bytes, i, value.size)
                i += value.size
            }
            if (includeTerminator) {
                val terminator = terminator
                if (terminator.isNotEmpty()) {
                    System.arraycopy(terminator, 0, bytes, i, terminator.size)
                }
            }
            bytes
        }
    }

    override fun toString(): String {
        return try {
            bytesToString(value, characterSetForTextEncoding(textEncoding))
        } catch (e: CharacterCodingException) {
            ""
        }
    }

    val characterSet: String
        get() = characterSetForTextEncoding(textEncoding)

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + textEncoding
        result = prime * result + value.contentHashCode()
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (javaClass != other.javaClass) return false
        val other2 = other as EncodedText
        if (textEncoding != other.textEncoding) return false
        return value.contentEquals(other2.value)
    }

    companion object {
        const val TEXT_ENCODING_ISO_8859_1: Byte = 0
        const val TEXT_ENCODING_UTF_16: Byte = 1
        const val TEXT_ENCODING_UTF_16BE: Byte = 2
        const val TEXT_ENCODING_UTF_8: Byte = 3
        const val CHARSET_ISO_8859_1 = "ISO-8859-1"
        const val CHARSET_UTF_16 = "UTF-16LE"
        const val CHARSET_UTF_16BE = "UTF-16BE"
        const val CHARSET_UTF_8 = "UTF-8"
        private val characterSets = arrayOf(
            CHARSET_ISO_8859_1,
            CHARSET_UTF_16,
            CHARSET_UTF_16BE,
            CHARSET_UTF_8
        )
        private val textEncodingFallback = byteArrayOf(0, 2, 1, 3)
        private val boms = arrayOf(
            byteArrayOf(), byteArrayOf(
                0xff.toByte(), 0xfe.toByte()
            ), byteArrayOf(0xfe.toByte(), 0xff.toByte()), byteArrayOf()
        )
        private val terminators =
            arrayOf(byteArrayOf(0), byteArrayOf(0, 0), byteArrayOf(0, 0), byteArrayOf(0))

        private fun textEncodingForBytesFromBOM(value: ByteArray): Byte {
            return if (value.size >= 2 && value[0] == 0xff.toByte() && value[1] == 0xfe.toByte()) {
                TEXT_ENCODING_UTF_16
            } else if (value.size >= 2 && value[0] == 0xfe.toByte() && value[1] == 0xff.toByte()) {
                TEXT_ENCODING_UTF_16BE
            } else if (value.size >= 3 && value[0] == 0xef.toByte() && value[1] == 0xbb.toByte() && value[2] == 0xbf.toByte()
            ) {
                TEXT_ENCODING_UTF_8
            } else {
                TEXT_ENCODING_ISO_8859_1
            }
        }

        private fun characterSetForTextEncoding(textEncoding: Byte): String {
            return try {
                characterSets[textEncoding.toInt()]
            } catch (e: ArrayIndexOutOfBoundsException) {
                throw IllegalArgumentException("Invalid text encoding $textEncoding")
            }
        }

        @Throws(CharacterCodingException::class)
        private fun bytesToString(bytes: ByteArray?, characterSet: String): String {
            val cbuf = bytesToCharBuffer(bytes, characterSet)
            val s = cbuf.toString()
            val length = s.indexOf(0.toChar())
            return if (length == -1) s else s.substring(0, length)
        }

        @Throws(CharacterCodingException::class)
        protected fun bytesToCharBuffer(bytes: ByteArray?, characterSet: String?): CharBuffer {
            val charset = Charset.forName(characterSet)
            val decoder = charset.newDecoder()
            return decoder.decode(ByteBuffer.wrap(bytes))
        }

        private fun stringToBytes(s: String, characterSet: String): ByteArray? {
            return try {
                charBufferToBytes(CharBuffer.wrap(s), characterSet)
            } catch (e: CharacterCodingException) {
                null
            }
        }

        @Throws(CharacterCodingException::class)
        protected fun charBufferToBytes(charBuffer: CharBuffer, characterSet: String?): ByteArray {
            val charset = Charset.forName(characterSet)
            val encoder = charset.newEncoder()
            val byteBuffer = encoder.encode(charBuffer)
            return BufferTools.copyBuffer(byteBuffer.array(), 0, byteBuffer.limit())
        }
    }
}