package com.mpatric.mp3agic

import com.mpatric.mp3agic.BufferTools.stringToByteBuffer
import org.junit.Assert
import org.junit.Test

class ID3v2ObseleteFrameTest {
    @Test
    @Throws(Exception::class)
    fun shouldReadValidLong32ObseleteTFrame() {
        val bytes = stringToByteBuffer(LONG_T_FRAME, 0, LONG_T_FRAME.length)
        TestHelper.replaceNumbersWithBytes(bytes, 3)
        val frame = ID3v2ObseleteFrame(bytes, 0)
        Assert.assertEquals(263, frame.length.toLong())
        Assert.assertEquals("TP1", frame.id)
        val s =
            "0Metamorphosis A a very long album B a very long album C a very long album D a very long album E a very long album F a very long album G a very long album H a very long album I a very long album J a very long album K a very long album L a very long album M0"
        val expectedBytes = stringToByteBuffer(s, 0, s.length)
        TestHelper.replaceNumbersWithBytes(expectedBytes, 0)
        Assert.assertArrayEquals(expectedBytes, frame.data)
    }

    @Test
    @Throws(Exception::class)
    fun shouldReadValid32ObseleteTFrame() {
        val bytes = stringToByteBuffer("xxxxx" + T_FRAME, 0, 5 + T_FRAME.length)
        TestHelper.replaceNumbersWithBytes(bytes, 8)
        val frame = ID3v2ObseleteFrame(bytes, 5)
        Assert.assertEquals(40, frame.length.toLong())
        Assert.assertEquals("TP1", frame.id)
        val s = "0ARTISTABCDEFGHIJKLMNOPQRSTUVWXYZ0"
        val expectedBytes = stringToByteBuffer(s, 0, s.length)
        TestHelper.replaceNumbersWithBytes(expectedBytes, 0)
        Assert.assertArrayEquals(expectedBytes, frame.data)
    }

    companion object {
        private const val T_FRAME = "TP100\"0ARTISTABCDEFGHIJKLMNOPQRSTUVWXYZ0"
        private const val LONG_T_FRAME =
            "TP10110Metamorphosis A a very long album B a very long album C a very long album D a very long album E a very long album F a very long album G a very long album H a very long album I a very long album J a very long album K a very long album L a very long album M0"
    }
}