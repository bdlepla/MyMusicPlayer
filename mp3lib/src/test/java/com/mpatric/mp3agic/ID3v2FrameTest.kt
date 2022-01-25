package com.mpatric.mp3agic

import org.junit.Assert
import org.junit.Test

class ID3v2FrameTest {
    @Test
    @Throws(Exception::class)
    fun shouldReadValid32TFrame() {
        val bytes = BufferTools.stringToByteBuffer("xxxxx" + T_FRAME, 0, 5 + T_FRAME.length)
        TestHelper.replaceNumbersWithBytes(bytes, 9)
        val frame = ID3v2Frame(bytes, 5)
        Assert.assertEquals(42, frame.length.toLong())
        Assert.assertEquals("TPE1", frame.id)
        val s = "0ABCDEFGHIJKLMNOPQRSTUVWXYZABCDE"
        val expectedBytes = BufferTools.stringToByteBuffer(s, 0, s.length)
        TestHelper.replaceNumbersWithBytes(expectedBytes, 0)
        Assert.assertArrayEquals(expectedBytes, frame.data)
    }

    @Test
    @Throws(Exception::class)
    fun shouldReadValid32WFrame() {
        val bytes = BufferTools.stringToByteBuffer(W_FRAME + "xxxxx", 0, W_FRAME.length)
        TestHelper.replaceNumbersWithBytes(bytes, 0)
        val frame = ID3v2Frame(bytes, 0)
        Assert.assertEquals(43, frame.length.toLong())
        Assert.assertEquals("WXXX", frame.id)
        val s = "00ABCDEFGHIJKLMNOPQRSTUVWXYZABCDE"
        val expectedBytes = BufferTools.stringToByteBuffer(s, 0, s.length)
        TestHelper.replaceNumbersWithBytes(expectedBytes, 0)
        Assert.assertArrayEquals(expectedBytes, frame.data)
    }

    @Test
    @Throws(Exception::class)
    fun shouldReadValid32CFrame() {
        val bytes = BufferTools.stringToByteBuffer(C_FRAME, 0, C_FRAME.length)
        TestHelper.replaceNumbersWithBytes(bytes, 0)
        val frame = ID3v2Frame(bytes, 0)
        Assert.assertEquals(46, frame.length.toLong())
        Assert.assertEquals("COMM", frame.id)
        val s = "00000ABCDEFGHIJKLMNOPQRSTUVWXYZABCDE"
        val expectedBytes = BufferTools.stringToByteBuffer(s, 0, s.length)
        TestHelper.replaceNumbersWithBytes(expectedBytes, 0)
        Assert.assertArrayEquals(expectedBytes, frame.data)
    }

    @Test
    @Throws(Exception::class)
    fun shouldPackAndUnpackHeaderToGiveEquivalentObject() {
        val bytes = ByteArray(26)
        for (i in bytes.indices) {
            bytes[i] = ('A'.toInt() + i).toByte()
        }
        val frame = ID3v2Frame("TEST", bytes)
        val newBytes = frame.toBytes()
        val frameCopy = ID3v2Frame(newBytes, 0)
        Assert.assertEquals("TEST", frameCopy.id)
        Assert.assertEquals(frame, frameCopy)
    }

    @Test
    @Throws(Exception::class)
    fun shouldCorrectlyUnpackHeader() {
        val bytes = BufferTools.stringToByteBuffer(W_FRAME + "?????", 0, W_FRAME.length)
        TestHelper.replaceNumbersWithBytes(bytes, 0)
        val frame = ID3v2Frame(bytes, 0)
        Assert.assertFalse(frame.hasDataLengthIndicator())
        Assert.assertFalse(frame.hasCompression())
        Assert.assertFalse(frame.hasEncryption())
        Assert.assertFalse(frame.hasGroup())
        Assert.assertFalse(frame.hasPreserveFile())
        Assert.assertFalse(frame.hasPreserveTag())
        Assert.assertFalse(frame.isReadOnly)
        Assert.assertFalse(frame.hasUnsynchronisation())
    }

    @Test
    @Throws(Exception::class)
    fun shouldStoreAndRetrieveData() {
        val oldBytes = BufferTools.stringToByteBuffer(C_FRAME, 0, C_FRAME.length)
        TestHelper.replaceNumbersWithBytes(oldBytes, 0)
        val frame = ID3v2Frame(oldBytes, 0)
        val newBytes = BufferTools.stringToByteBuffer(W_FRAME + "?????", 0, W_FRAME.length)
        TestHelper.replaceNumbersWithBytes(newBytes, 0)
        frame.data = newBytes
        val expectedBytes = BufferTools.stringToByteBuffer(W_FRAME, 0, W_FRAME.length)
        TestHelper.replaceNumbersWithBytes(expectedBytes, 0)
        Assert.assertArrayEquals(expectedBytes, frame.data)
    }

  /*  @Test
    @Throws(Exception::class)
    fun shouldCorrectlyImplementHashCodeAndEquals() {
        EqualsVerifier.forClass(ID3v2Frame::class.java)
            .usingGetClass()
            .suppress(Warning.NONFINAL_FIELDS)
            .verify()
    }*/

    companion object {
        private const val T_FRAME = "TPE1000 000ABCDEFGHIJKLMNOPQRSTUVWXYZABCDE"
        private const val W_FRAME = "WXXX000!0000ABCDEFGHIJKLMNOPQRSTUVWXYZABCDE"
        private const val C_FRAME = "COMM000$0000000ABCDEFGHIJKLMNOPQRSTUVWXYZABCDE"
    }
}