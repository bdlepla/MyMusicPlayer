package com.mpatric.mp3agic

import org.junit.Assert
import org.junit.Test

class MpegFrameTest {
    @Test
    fun testBitwiseLeftShiftOperationsOnLong() {
        val original: Long = -0x2 // 1111 1111 1111 1111 1111 1111 1111 1110
        val expectedShl1: Long = -0x4 // 1111 1111 1111 1111 1111 1111 1111 1100
        val expectedShl28: Long = -0x20000000 // 1110 0000 0000 0000 0000 0000 0000 0000
        val expectedShl30: Long = -0x80000000 // 1000 0000 0000 0000 0000 0000 0000 0000
        Assert.assertEquals(expectedShl1, original shl 1)
        Assert.assertEquals(expectedShl28, original shl 28)
        Assert.assertEquals(expectedShl30, original shl 30)
    }

    @Test
    fun testBitwiseRightShiftOperationsOnLong() {
        val original: Long = -0x80000000 // 1000 0000 0000 0000 0000 0000 0000 0000
        val expectedShr1: Long = -0x40000000 // 1100 0000 0000 0000 0000 0000 0000 0000
        val expectedShr28: Long = -0x8 // 1111 1111 1111 1111 1111 1111 1111 1000
        val expectedShr30: Long = -0x2 // 1111 1111 1111 1111 1111 1111 1111 1110
        Assert.assertEquals(expectedShr1, original shr 1)
        Assert.assertEquals(expectedShr28, original shr 28)
        Assert.assertEquals(expectedShr30, original shr 30)
    }

    @Test
    fun testShiftingByteIntoBiggerNumber() {
        val original: Byte = -0x02 // 1111 1110
        val originalAsLong: Long = (original.toInt() and 0xff).toLong()
        val expectedShl1: Byte = -0x04 // 1111 1100
        val expectedShl8: Long = 0x0000FE00 // 0000 0000 0000 0000 1111 1110 0000 0000
        val expectedShl16: Long = 0x00FE0000 // 0000 0000 1111 1110 0000 0000 0000 0000
        val expectedShl23: Long = 0x7F000000 // 0111 1111 00000 0000 0000 0000 0000 0000
        Assert.assertEquals(expectedShl1.toLong(), (original shl 1).toLong())
        Assert.assertEquals(254, originalAsLong)
        Assert.assertEquals(expectedShl8, originalAsLong shl 8)
        Assert.assertEquals(expectedShl16, originalAsLong shl 16)
        Assert.assertEquals(expectedShl23, originalAsLong shl 23)
    }

    @Test
    fun shouldExtractValidFields() {
        val mpegFrame = MpegFrameForTesting()
        Assert.assertEquals(0x000007FF, mpegFrame.extractField(-0x200000, 0xFFE00000L).toLong())
        Assert.assertEquals(0x000007FF, mpegFrame.extractField(-0x100001, 0xFFE00000L).toLong())
        Assert.assertEquals(0x00000055, mpegFrame.extractField(0x11111155, 0x000000FFL).toLong())
        Assert.assertEquals(0x00000055, mpegFrame.extractField(-0x1000ab, 0x000000FFL).toLong())
    }

    @Test
    @Throws(InvalidDataException::class)
    fun shouldExtractValidMpegVersion1Header() {
        val frameData = byteArrayOf(BYTE_FF, BYTE_FB, BYTE_A2, BYTE_40)
        val mpegFrame = MpegFrameForTesting(frameData)
        Assert.assertEquals(MpegFrame.MPEG_VERSION_1_0, mpegFrame.version)
        Assert.assertEquals(MpegFrame.MPEG_LAYER_3, mpegFrame.getLayer())
        Assert.assertEquals(160, mpegFrame.bitrate)
        Assert.assertEquals(44100, mpegFrame.getSampleRate().toLong())
        Assert.assertEquals(MpegFrame.CHANNEL_MODE_JOINT_STEREO, mpegFrame.channelMode)
        Assert.assertEquals("None", mpegFrame.modeExtension)
        Assert.assertEquals("None", mpegFrame.emphasis)
        Assert.assertEquals(true, mpegFrame.isProtection)
        Assert.assertEquals(true, mpegFrame.hasPadding())
        Assert.assertEquals(false, mpegFrame.isPrivate)
        Assert.assertEquals(false, mpegFrame.isCopyright)
        Assert.assertEquals(false, mpegFrame.isOriginal)
        Assert.assertEquals(523, mpegFrame.lengthInBytes.toLong())
    }

    @Test
    @Throws(InvalidDataException::class)
    fun shouldProcessValidMpegVersion2Header() {
        val frameData = byteArrayOf(BYTE_FF, BYTE_F3, BYTE_A2, BYTE_40)
        val mpegFrame = MpegFrameForTesting(frameData)
        Assert.assertEquals(MpegFrame.MPEG_VERSION_2_0, mpegFrame.version)
        Assert.assertEquals(MpegFrame.MPEG_LAYER_3, mpegFrame.getLayer())
        Assert.assertEquals(96, mpegFrame.bitrate)
        Assert.assertEquals(22050, mpegFrame.getSampleRate().toLong())
        Assert.assertEquals(MpegFrame.CHANNEL_MODE_JOINT_STEREO, mpegFrame.channelMode)
        Assert.assertEquals("None", mpegFrame.modeExtension)
        Assert.assertEquals("None", mpegFrame.emphasis)
        Assert.assertEquals(true, mpegFrame.isProtection)
        Assert.assertEquals(true, mpegFrame.hasPadding())
        Assert.assertEquals(false, mpegFrame.isPrivate)
        Assert.assertEquals(false, mpegFrame.isCopyright)
        Assert.assertEquals(false, mpegFrame.isOriginal)
        Assert.assertEquals(627, mpegFrame.lengthInBytes.toLong())
    }

    @Test
    fun shouldThrowExceptionForInvalidFrameSync() {
        val frameData = byteArrayOf(BYTE_FF, BYTE_DB, BYTE_A2, BYTE_40)
        try {
            MpegFrameForTesting(frameData)
            Assert.fail("InvalidDataException expected but not thrown")
        } catch (e: InvalidDataException) {
            Assert.assertEquals("Frame sync missing", e.message)
        }
    }

    @Test
    fun shouldThrowExceptionForInvalidMpegVersion() {
        val frameData = byteArrayOf(BYTE_FF, BYTE_EB, BYTE_A2, BYTE_40)
        try {
            MpegFrameForTesting(frameData)
            Assert.fail("InvalidDataException expected but not thrown")
        } catch (e: InvalidDataException) {
            Assert.assertEquals("Invalid mpeg audio version in frame header", e.message)
        }
    }

    @Test
    fun shouldThrowExceptionForInvalidMpegLayer() {
        val frameData = byteArrayOf(BYTE_FF, BYTE_F9, BYTE_A2, BYTE_40)
        try {
            MpegFrameForTesting(frameData)
            Assert.fail("InvalidDataException expected but not thrown")
        } catch (e: InvalidDataException) {
            Assert.assertEquals("Invalid mpeg layer description in frame header", e.message)
        }
    }

    @Test
    fun shouldThrowExceptionForFreeBitrate() {
        val frameData = byteArrayOf(BYTE_FF, BYTE_FB, BYTE_02, BYTE_40)
        try {
            MpegFrameForTesting(frameData)
            Assert.fail("InvalidDataException expected but not thrown")
        } catch (e: InvalidDataException) {
            Assert.assertEquals("Invalid bitrate in frame header", e.message)
        }
    }

    @Test
    fun shouldThrowExceptionForInvalidBitrate() {
        val frameData = byteArrayOf(BYTE_FF, BYTE_FB, BYTE_F2, BYTE_40)
        try {
            MpegFrameForTesting(frameData)
            Assert.fail("InvalidDataException expected but not thrown")
        } catch (e: InvalidDataException) {
            Assert.assertEquals("Invalid bitrate in frame header", e.message)
        }
    }

    @Test
    fun shouldThrowExceptionForInvalidSampleRate() {
        val frameData = byteArrayOf(BYTE_FF, BYTE_FB, BYTE_AE, BYTE_40)
        try {
            MpegFrameForTesting(frameData)
            Assert.fail("InvalidDataException expected but not thrown")
        } catch (e: InvalidDataException) {
            Assert.assertEquals("Invalid sample rate in frame header", e.message)
        }
    }

    internal inner class MpegFrameForTesting : MpegFrame {
        constructor() : super() {}
        constructor(frameData: ByteArray?) : super(frameData!!) {}
    }

    companion object {
        private const val BYTE_FF: Byte = -0x01
        private const val BYTE_FB: Byte = -0x05
        private const val BYTE_F9: Byte = -0x07
        private const val BYTE_F3: Byte = -0x0D
        private const val BYTE_F2: Byte = -0x0E
        private const val BYTE_A2: Byte = -0x5E
        private const val BYTE_AE: Byte = -0x52
        private const val BYTE_DB: Byte = -0x25
        private const val BYTE_EB: Byte = -0x15
        private const val BYTE_40: Byte = 0x40
        private const val BYTE_02: Byte = 0x02
    }
}