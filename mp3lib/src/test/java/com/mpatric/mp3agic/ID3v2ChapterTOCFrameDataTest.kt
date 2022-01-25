package com.mpatric.mp3agic


import org.junit.Assert
import org.junit.Assert.assertArrayEquals
import org.junit.Test
import java.util.*

class ID3v2ChapterTOCFrameDataTest {
    @Test
    @Throws(Exception::class)
    fun equalsItself() {
        val frameData = ID3v2ChapterTOCFrameData(false, true, false, "toc1", arrayOf("ch1", "ch2"))
        Assert.assertEquals(frameData, frameData)
    }

    @Test
    @Throws(Exception::class)
    fun notEqualToNull() {
        val frameData = ID3v2ChapterTOCFrameData(false, true, false, "toc1", arrayOf("ch1", "ch2"))
        Assert.assertFalse(frameData.equals(null))
    }

    @Test
    fun notEqualToDifferentClass() {
        val frameData = ID3v2ChapterTOCFrameData(false, true, false, "toc1", arrayOf("ch1", "ch2"))
        Assert.assertFalse(frameData.equals("8"))
    }

    @Test
    @Throws(Exception::class)
    fun shouldConsiderTwoEquivalentObjectsEqual() {
        val children = arrayOf<String?>("ch1", "ch2")
        val frameData1 = ID3v2ChapterTOCFrameData(false, true, false, "toc1", children)
        val subFrameData1 = ID3v2TextFrameData(false, EncodedText("Hello there"))
        frameData1.addSubframe("TIT2", subFrameData1)
        val frameData2 = ID3v2ChapterTOCFrameData(false, true, false, "toc1", children)
        val subFrameData2 = ID3v2TextFrameData(false, EncodedText("Hello there"))
        frameData2.addSubframe("TIT2", subFrameData2)
        Assert.assertEquals(frameData1, frameData2)
    }

    @Test
    fun notEqualIfUnsynchronizationNotEqual() {
        val frameData1 = ID3v2ChapterTOCFrameData(false, true, false, "toc1", arrayOf("ch1", "ch2"))
        val frameData2 = ID3v2ChapterTOCFrameData(true, true, false, "toc1", arrayOf("ch1", "ch2"))
        Assert.assertNotEquals(frameData1, frameData2)
    }

    @Test
    fun notEqualIfIsRootNotEqual() {
        val frameData1 = ID3v2ChapterTOCFrameData(false, true, false, "toc1", arrayOf("ch1", "ch2"))
        val frameData2 =
            ID3v2ChapterTOCFrameData(false, false, false, "toc1", arrayOf("ch1", "ch2"))
        Assert.assertNotEquals(frameData1, frameData2)
    }

    @Test
    fun notEqualIfIsOrderedNotEqual() {
        val frameData1 = ID3v2ChapterTOCFrameData(false, true, false, "toc1", arrayOf("ch1", "ch2"))
        val frameData2 = ID3v2ChapterTOCFrameData(false, true, true, "toc1", arrayOf("ch1", "ch2"))
        Assert.assertNotEquals(frameData1, frameData2)
    }

    @Test
    fun notEqualIfIdNotEqual() {
        val frameData1 = ID3v2ChapterTOCFrameData(false, true, false, "toc1", arrayOf("ch1", "ch2"))
        val frameData2 = ID3v2ChapterTOCFrameData(false, true, false, "toc2", arrayOf("ch1", "ch2"))
        Assert.assertNotEquals(frameData1, frameData2)
    }

    @Test
    fun notEqualIfIdIsNullOnOne() {
        val frameData1 = ID3v2ChapterTOCFrameData(false, true, false, null, arrayOf("ch1", "ch2"))
        val frameData2 = ID3v2ChapterTOCFrameData(false, true, false, "toc1", arrayOf("ch1", "ch2"))
        Assert.assertNotEquals(frameData1, frameData2)
    }

    @Test
    fun equalIfIdIsNullOnBoth() {
        val frameData1 = ID3v2ChapterTOCFrameData(false, true, false, null, arrayOf("ch1", "ch2"))
        val frameData2 = ID3v2ChapterTOCFrameData(false, true, false, null, arrayOf("ch1", "ch2"))
        Assert.assertEquals(frameData1, frameData2)
    }

    @Test
    fun notEqualIfChildrenNotEqual() {
        val frameData1 = ID3v2ChapterTOCFrameData(false, true, false, "toc1", arrayOf("ch1", "ch2"))
        val frameData2 = ID3v2ChapterTOCFrameData(false, true, false, "toc`", arrayOf("ch3", "ch2"))
        Assert.assertNotEquals(frameData1, frameData2)
    }

    @Test
    fun notEqualIfOneDoeNotHaveChildren() {
        val frameData1 = ID3v2ChapterTOCFrameData(false, true, false, "toc1", arrayOf("ch1", "ch2"))
        val frameData2 = ID3v2ChapterTOCFrameData(false, true, false, "toc`", arrayOf())
        Assert.assertNotEquals(frameData1, frameData2)
    }

    @Test
    fun notEqualIfChildrenNullOnOne() {
        val frameData1 = ID3v2ChapterTOCFrameData(false, true, false, "toc1", null)
        val frameData2 = ID3v2ChapterTOCFrameData(false, true, false, "toc`", arrayOf("ch3", "ch2"))
        Assert.assertNotEquals(frameData1, frameData2)
    }

    @Test
    @Throws(Exception::class)
    fun notEqualIfOneHasSubframes() {
        val frameData1 = ID3v2ChapterTOCFrameData(false, true, false, "toc1", arrayOf("ch1", "ch2"))
        val frameData2 = ID3v2ChapterTOCFrameData(false, true, false, "toc1", arrayOf("ch1", "ch2"))
        val subFrameData2 = ID3v2TextFrameData(false, EncodedText("Hello there"))
        frameData2.addSubframe("TIT2", subFrameData2)
        Assert.assertNotEquals(frameData1, frameData2)
    }

    @Test
    fun hashCodeIsConsistent() {
        val frameData = ID3v2ChapterTOCFrameData(false, true, false, "toc1", arrayOf("ch1", "ch2"))
        Assert.assertEquals(frameData.hashCode().toLong(), frameData.hashCode().toLong())
    }

    @Test
    fun equalObjectsHaveSameHashCode() {
        val frameData = ID3v2ChapterTOCFrameData(false, true, false, "toc1", arrayOf("ch1", "ch2"))
        val frameDataAgain =
            ID3v2ChapterTOCFrameData(false, true, false, "toc1", arrayOf("ch1", "ch2"))
        Assert.assertEquals(frameData.hashCode().toLong(), frameDataAgain.hashCode().toLong())
    }

    @Test
    @Throws(Exception::class)
    fun shouldConvertFrameDataToBytesAndBackToEquivalentObject() {
        val children = arrayOf<String?>("ch1", "ch2")
        val frameData = ID3v2ChapterTOCFrameData(false, true, true, "toc1", children)
        val subFrameData = ID3v2TextFrameData(false, EncodedText("Hello there"))
        frameData.addSubframe("TIT2", subFrameData)
        val bytes = frameData.toBytes()
        val expectedBytes = byteArrayOf(
            't'.toByte(),
            'o'.toByte(),
            'c'.toByte(),
            '1'.toByte(),
            0,
            3,
            2,
            'c'.toByte(),
            'h'.toByte(),
            '1'.toByte(),
            0,
            'c'.toByte(),
            'h'.toByte(),
            '2'.toByte(),
            0,
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
        val frameDataCopy = ID3v2ChapterTOCFrameData(false, bytes)
        Assert.assertEquals(frameData, frameDataCopy)
    }

    @Test
    fun toStringOnMostlyEmptyFrameData() {
        val frameData = ID3v2ChapterTOCFrameData(false)
        Assert.assertEquals(
            "ID3v2ChapterTOCFrameData [isRoot=false, isOrdered=false, id=null, children=null, subframes=[]]",
            frameData.toString()
        )
    }

    @Test
    fun toStringOnFullFrameData() {
        val frameData = ID3v2ChapterTOCFrameData(false, true, true, "toc1", arrayOf("ch1", "ch2"))
        Assert.assertEquals(
            "ID3v2ChapterTOCFrameData [isRoot=true, isOrdered=true, id=toc1, children=[ch1, ch2], subframes=[]]",
            frameData.toString()
        )
    }

    @Test
    fun getsAndSetsIsRoot() {
        val frameData = ID3v2ChapterTOCFrameData(false)
        frameData.isRoot = true
        Assert.assertTrue(frameData.isRoot)
    }

    @Test
    fun getsAndSetsIsOrdered() {
        val frameData = ID3v2ChapterTOCFrameData(false)
        frameData.isOrdered = true
        Assert.assertTrue(frameData.isOrdered)
    }

    @Test
    fun getsAndSetsId() {
        val frameData = ID3v2ChapterTOCFrameData(false)
        frameData.id = "My ID"
        Assert.assertEquals("My ID", frameData.id)
    }

    @Test
    fun getsAndSetsChildren() {
        val frameData = ID3v2ChapterTOCFrameData(false)
        frameData.childs = arrayOf("ch1", "ch2")
        assertArrayEquals(arrayOf("ch1", "ch2"), frameData.childs)
    }

    @Test
    fun getsAndSetsSubframes() {
        val frameData = ID3v2ChapterTOCFrameData(false)
        val subframes = ArrayList<ID3v2Frame>(2)
        subframes.add(ID3v2Frame("", byteArrayOf('c'.toByte(), 'h'.toByte(), '1'.toByte(), 0)))
        subframes.add(ID3v2Frame("", byteArrayOf(1, 0, 1, 0)))
        frameData.subframes = subframes
        Assert.assertEquals(subframes, frameData.subframes)
    }
}