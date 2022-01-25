package com.mpatric.mp3agic

import org.junit.Assert
import org.junit.Test
import java.util.*

class ID3v2ChapterFrameDataTest {
    @Test
    @Throws(Exception::class)
    fun equalsItself() {
        val frameData = ID3v2ChapterFrameData(false, "ch1", 1, 380, 3, 400)
        Assert.assertEquals(frameData, frameData)
    }

    @Test
    @Throws(Exception::class)
    fun notEqualToNull() {
        val frameData = ID3v2ChapterFrameData(false, "ch1", 1, 380, 3, 400)
        Assert.assertFalse(frameData.equals(null))
    }

    @Test
    fun notEqualToDifferentClass() {
        val frameData = ID3v2ChapterFrameData(false, "ch1", 1, 380, 3, 400)
        Assert.assertFalse(frameData.equals("8"))
    }

    @Test
    @Throws(Exception::class)
    fun shouldConsiderTwoEquivalentObjectsEqual() {
        val frameData1 = ID3v2ChapterFrameData(false, "ch1", 1, 380, 3, 400)
        val subFrameData1 = ID3v2TextFrameData(false, EncodedText("Hello there"))
        frameData1.addSubframe("TIT2", subFrameData1)
        val frameData2 = ID3v2ChapterFrameData(false, "ch1", 1, 380, 3, 400)
        val subFrameData2 = ID3v2TextFrameData(false, EncodedText("Hello there"))
        frameData2.addSubframe("TIT2", subFrameData2)
        Assert.assertEquals(frameData1, frameData2)
    }

    @Test
    fun notEqualIfUnsynchronizationNotEqual() {
        val frameData1 = ID3v2ChapterFrameData(false, "ch1", 1, 380, 3, 400)
        val frameData2 = ID3v2ChapterFrameData(true, "ch1", 1, 380, 3, 400)
        Assert.assertNotEquals(frameData1, frameData2)
    }

    @Test
    fun notEqualIfIdNotEqual() {
        val frameData1 = ID3v2ChapterFrameData(false, "ch1", 1, 380, 3, 400)
        val frameData2 = ID3v2ChapterFrameData(false, "ch2", 1, 380, 3, 400)
        Assert.assertNotEquals(frameData1, frameData2)
    }

    @Test
    fun notEqualIfIdIsNullOnOne() {
        val frameData1 = ID3v2ChapterFrameData(false, null, 1, 380, 3, 400)
        val frameData2 = ID3v2ChapterFrameData(false, "ch2", 1, 380, 3, 400)
        Assert.assertNotEquals(frameData1, frameData2)
    }

    @Test
    fun equalIfIdIsNullOnBoth() {
        val frameData1 = ID3v2ChapterFrameData(false, null, 1, 380, 3, 400)
        val frameData2 = ID3v2ChapterFrameData(false, null, 1, 380, 3, 400)
        Assert.assertEquals(frameData1, frameData2)
    }

    @Test
    fun notEqualIfStartTimeNotEqual() {
        val frameData1 = ID3v2ChapterFrameData(false, "ch1", 1, 380, 3, 400)
        val frameData2 = ID3v2ChapterFrameData(false, "ch1", 2, 380, 3, 400)
        Assert.assertNotEquals(frameData1, frameData2)
    }

    @Test
    fun notEqualIfEndTimeNotEqual() {
        val frameData1 = ID3v2ChapterFrameData(false, "ch1", 1, 380, 3, 400)
        val frameData2 = ID3v2ChapterFrameData(false, "ch1", 1, 280, 3, 400)
        Assert.assertNotEquals(frameData1, frameData2)
    }

    @Test
    fun notEqualIfStartOffsetNotEqual() {
        val frameData1 = ID3v2ChapterFrameData(false, "ch1", 1, 380, 3, 400)
        val frameData2 = ID3v2ChapterFrameData(false, "ch1", 1, 380, 2, 400)
        Assert.assertNotEquals(frameData1, frameData2)
    }

    @Test
    fun notEqualIfEndOffsetNotEqual() {
        val frameData1 = ID3v2ChapterFrameData(false, "ch1", 1, 380, 3, 400)
        val frameData2 = ID3v2ChapterFrameData(false, "ch1", 1, 380, 3, 200)
        Assert.assertNotEquals(frameData1, frameData2)
    }

    @Test
    @Throws(Exception::class)
    fun notEqualIfOneHasSubframes() {
        val frameData1 = ID3v2ChapterFrameData(false, "ch1", 1, 380, 3, 400)
        val frameData2 = ID3v2ChapterFrameData(false, "ch1", 1, 380, 3, 400)
        val subFrameData2 = ID3v2TextFrameData(false, EncodedText("Hello there"))
        frameData2.addSubframe("TIT2", subFrameData2)
        Assert.assertNotEquals(frameData1, frameData2)
    }

    @Test
    fun hashCodeIsConsistent() {
        val frameData = ID3v2ChapterFrameData(false, "ch1", 1, 380, 3, 400)
        Assert.assertEquals(frameData.hashCode().toLong(), frameData.hashCode().toLong())
    }

    @Test
    fun equalObjectsHaveSameHashCode() {
        val frameData = ID3v2ChapterFrameData(false, "ch1", 1, 380, 3, 400)
        val frameDataAgain = ID3v2ChapterFrameData(false, "ch1", 1, 380, 3, 400)
        Assert.assertEquals(frameData.hashCode().toLong(), frameDataAgain.hashCode().toLong())
    }

    @Test
    @Throws(Exception::class)
    fun shouldConvertFrameDataToBytesAndBackToEquivalentObject() {
        val frameData = ID3v2ChapterFrameData(false, "ch1", 1, 380, 3, 400)
        val subFrameData = ID3v2TextFrameData(false, EncodedText("Hello there"))
        frameData.addSubframe("TIT2", subFrameData)
        val bytes = frameData.toBytes()
        val expectedBytes = byteArrayOf(
            'c'.toByte(),
            'h'.toByte(),
            '1'.toByte(),
            0,
            0,
            0,
            0,
            1,
            0,
            0,
            1,
            0x7c.toByte(),
            0,
            0,
            0,
            3,
            0,
            0,
            1,
            0x90.toByte(),
            'T'.toByte(),
            'I'.toByte(),
            'T'.toByte(),
            '2'.toByte(),
            0,
            0,
            0,
            0xc.toByte(),
            0,
            0,
            0,
            'H'.toByte(),
            'e'.toByte(),
            'l'.toByte(),
            'l'.toByte(),
            'o'.toByte(),
            ' '.toByte(),
            't'.toByte(),
            'h'.toByte(),
            'e'.toByte(),
            'r'.toByte(),
            'e'
                .toByte()
        )
        Assert.assertArrayEquals(expectedBytes, bytes)
        val frameDataCopy = ID3v2ChapterFrameData(false, bytes)
        Assert.assertEquals(frameData, frameDataCopy)
    }

    @Test
    fun toStringOnMostlyEmptyFrameData() {
        val frameData = ID3v2ChapterFrameData(false)
        Assert.assertEquals(
            "ID3v2ChapterFrameData [id=null, startTime=0, endTime=0, startOffset=0, endOffset=0, subframes=[]]",
            frameData.toString()
        )
    }

    @Test
    fun toStringOnFullFrameData() {
        val frameData = ID3v2ChapterFrameData(false, "ch1", 1, 380, 3, 400)
        Assert.assertEquals(
            "ID3v2ChapterFrameData [id=ch1, startTime=1, endTime=380, startOffset=3, endOffset=400, subframes=[]]",
            frameData.toString()
        )
    }

    @Test
    fun getsAndSetsId() {
        val frameData = ID3v2ChapterFrameData(false)
        frameData.id = "My ID"
        Assert.assertEquals("My ID", frameData.id)
    }

    @Test
    fun getsAndSetsStartTime() {
        val frameData = ID3v2ChapterFrameData(false)
        frameData.startTime = 9
        Assert.assertEquals(9, frameData.startTime)
    }

    @Test
    fun getsAndSetsEndTime() {
        val frameData = ID3v2ChapterFrameData(false)
        frameData.endTime = 9
        Assert.assertEquals(9, frameData.endTime)
    }

    @Test
    fun getsAndSetsStartOffset() {
        val frameData = ID3v2ChapterFrameData(false)
        frameData.startOffset = 9
        Assert.assertEquals(9, frameData.startOffset)
    }

    @Test
    fun getsAndSetsEndOffset() {
        val frameData = ID3v2ChapterFrameData(false)
        frameData.endOffset = 9
        Assert.assertEquals(9, frameData.endOffset)
    }

    @Test
    fun getsAndSetsSubframes() {
        val frameData = ID3v2ChapterFrameData(false)
        val subframes = ArrayList<ID3v2Frame>(2)
        subframes.add(ID3v2Frame("", byteArrayOf('c'.toByte(), 'h'.toByte(), '1'.toByte(), 0)))
        subframes.add(ID3v2Frame("", byteArrayOf(1, 0, 1, 0)))
        frameData.subframes = subframes
        Assert.assertEquals(subframes, frameData.subframes)
    }
}