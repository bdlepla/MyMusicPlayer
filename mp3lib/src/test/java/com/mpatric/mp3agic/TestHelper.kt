package com.mpatric.mp3agic

import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.IOException
import java.io.RandomAccessFile

class TestHelper {
    // self tests
    @Test
    @Throws(Exception::class)
    fun shouldConvertBytesToHexAndBack() {
        val bytes = byteArrayOf(
            0x48.toByte(),
            0x45.toByte(),
            0x4C.toByte(),
            0x4C.toByte(),
            0x4F.toByte(),
            0x20.toByte(),
            0x74.toByte(),
            0x68.toByte(),
            0x65.toByte(),
            0x72.toByte(),
            0x65.toByte(),
            0x21.toByte()
        )
        val hexString = bytesToHexString(bytes)
        Assert.assertEquals("48 45 4c 4c 4f 20 74 68 65 72 65 21", hexString)
        Assert.assertArrayEquals(bytes, hexStringToBytes(hexString))
    }

    companion object {
        fun bytesToHexString(bytes: ByteArray): String {
            val hexString = StringBuilder()
            for (i in bytes.indices) {
                if (i > 0) hexString.append(' ')
                val hex = Integer.toHexString(
                    0xff and bytes[i]
                        .toInt()
                )
                if (hex.length == 1) hexString.append('0')
                hexString.append(hex)
            }
            return hexString.toString()
        }

        fun hexStringToBytes(hex: String): ByteArray {
            val len = hex.length
            val bytes = ByteArray((len + 1) / 3)
            var i = 0
            while (i < len) {
                bytes[i / 3] = ((Character.digit(hex[i], 16) shl 4) + Character.digit(
                    hex[i + 1],
                    16
                )).toByte()
                i += 3
            }
            return bytes
        }

        @Throws(IOException::class)
        fun loadFile(filename: String?): ByteArray {
            val file = RandomAccessFile(filename, "r")
            val buffer = ByteArray(file.length().toInt())
            file.read(buffer)
            file.close()
            return buffer
        }

        fun deleteFile(filename: String?) {
            val file = File(filename)
            file.delete()
        }

        fun replaceSpacesWithNulls(buffer: ByteArray) {
            for (i in buffer.indices) {
                if (buffer[i] == 0x20.toByte()) {
                    buffer[i] = 0x00
                }
            }
        }

        fun replaceNumbersWithBytes(bytes: ByteArray, offset: Int) {
            for (i in offset until bytes.size) {
                if (bytes[i] >= '0'.toByte() && bytes[i] <= '9'.toByte()) {
                    bytes[i] = (bytes[i] - 48.toByte()).toByte()
                }
            }
        }
    }
}