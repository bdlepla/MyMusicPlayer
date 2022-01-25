package com.mpatric.mp3agic

import org.junit.Assert
import org.junit.Test

class ID3v2PopmFrameDataTest {
    @Test
    @Throws(Exception::class)
    fun shouldReturnAddress() {
        val bytes = byteArrayOf(
            'A'.toByte(),
            'd'.toByte(),
            'd'.toByte(),
            'r'.toByte(),
            'e'.toByte(),
            's'.toByte(),
            's'.toByte(),
            0,
            0x00.toByte()
        )
        val iD3v2PopmFrameData = ID3v2PopmFrameData(false, bytes)
        Assert.assertEquals("Address", iD3v2PopmFrameData.address)
    }

    @Test
    @Throws(Exception::class)
    fun shouldReturn1StarRating() {
        val bytes = byteArrayOf(
            'A'.toByte(),
            'd'.toByte(),
            'd'.toByte(),
            'r'.toByte(),
            'e'.toByte(),
            's'.toByte(),
            's'.toByte(),
            0,
            0x01.toByte()
        )
        val iD3v2PopmFrameData = ID3v2PopmFrameData(false, bytes)
        Assert.assertEquals(1, iD3v2PopmFrameData.rating)
    }

    @Test
    @Throws(Exception::class)
    fun shouldReturn2StarRating() {
        val bytes = byteArrayOf(
            'A'.toByte(),
            'd'.toByte(),
            'd'.toByte(),
            'r'.toByte(),
            'e'.toByte(),
            's'.toByte(),
            's'.toByte(),
            0,
            0x40.toByte()
        )
        val iD3v2PopmFrameData = ID3v2PopmFrameData(false, bytes)
        Assert.assertEquals(2, iD3v2PopmFrameData.rating)
    }

    @Test
    @Throws(Exception::class)
    fun shouldReturn3StarRating() {
        val bytes = byteArrayOf(
            'A'.toByte(),
            'd'.toByte(),
            'd'.toByte(),
            'r'.toByte(),
            'e'.toByte(),
            's'.toByte(),
            's'.toByte(),
            0,
            0x80.toByte()
        )
        val iD3v2PopmFrameData = ID3v2PopmFrameData(false, bytes)
        Assert.assertEquals(3, iD3v2PopmFrameData.rating)
    }

    @Test
    @Throws(Exception::class)
    fun shouldReturn4StarRating() {
        val bytes = byteArrayOf(
            'A'.toByte(),
            'd'.toByte(),
            'd'.toByte(),
            'r'.toByte(),
            'e'.toByte(),
            's'.toByte(),
            's'.toByte(),
            0,
            0xC4.toByte()
        )
        val iD3v2PopmFrameData = ID3v2PopmFrameData(false, bytes)
        Assert.assertEquals(4, iD3v2PopmFrameData.rating)
    }

    @Test
    @Throws(Exception::class)
    fun shouldReturn5StarRating() {
        val bytes = byteArrayOf(
            'A'.toByte(),
            'd'.toByte(),
            'd'.toByte(),
            'r'.toByte(),
            'e'.toByte(),
            's'.toByte(),
            's'.toByte(),
            0,
            0xFF.toByte()
        )
        val iD3v2PopmFrameData = ID3v2PopmFrameData(false, bytes)
        Assert.assertEquals(5, iD3v2PopmFrameData.rating)
    }

    @Test
    @Throws(Exception::class)
    fun shouldReturnMinus1ForInvalidRating() {
        val bytes = byteArrayOf(
            'A'.toByte(),
            'd'.toByte(),
            'd'.toByte(),
            'r'.toByte(),
            'e'.toByte(),
            's'.toByte(),
            's'.toByte(),
            0,
            0x33.toByte()
        )
        val iD3v2PopmFrameData = ID3v2PopmFrameData(false, bytes)
        Assert.assertEquals(-1, iD3v2PopmFrameData.rating)
    }

    @Test
    @Throws(Exception::class)
    fun canSetAndGetRating() {
        val bytes = byteArrayOf(0)
        val iD3v2PopmFrameData = ID3v2PopmFrameData(false, bytes)
        iD3v2PopmFrameData.rating = 1
        Assert.assertEquals(1, iD3v2PopmFrameData.rating)
    }

    @Test
    @Throws(Exception::class)
    fun canSetAndGetAddress() {
        val bytes = byteArrayOf(0)
        val iD3v2PopmFrameData = ID3v2PopmFrameData(false, bytes)
        iD3v2PopmFrameData.address = "New Address"
        Assert.assertEquals("New Address", iD3v2PopmFrameData.address)
    }

    @Test
    @Throws(Exception::class)
    fun canGetLength() {
        val iD3v2PopmFrameData = ID3v2PopmFrameData(false, 0)
        iD3v2PopmFrameData.address = "Address"
        val expectedLength =
            "Address".length + 2 // Length of address , plus 1 separator byte + 1 bye for rating
        Assert.assertEquals(expectedLength.toLong(), iD3v2PopmFrameData.length.toLong())
    }

    @Test
    @Throws(Exception::class)
    fun canPackFrameData() {
        val iD3v2PopmFrameData = ID3v2PopmFrameData(false, 5)
        val expectedBytes = byteArrayOf(
            'W'.toByte(),
            'i'.toByte(),
            'n'.toByte(),
            'd'.toByte(),
            'o'.toByte(),
            'w'.toByte(),
            's'.toByte(),
            ' '.toByte(),
            'M'.toByte(),
            'e'.toByte(),
            'd'.toByte(),
            'i'.toByte(),
            'a'.toByte(),
            ' '.toByte(),
            'P'.toByte(),
            'l'.toByte(),
            'a'.toByte(),
            'y'.toByte(),
            'e'.toByte(),
            'r'.toByte(),
            ' '.toByte(),
            '9'.toByte(),
            ' '.toByte(),
            'S'.toByte(),
            'e'.toByte(),
            'r'.toByte(),
            'i'.toByte(),
            'e'.toByte(),
            's'.toByte(),
            0,
            0xFF.toByte()
        )
        val result = iD3v2PopmFrameData.packFrameData()
        Assert.assertEquals(expectedBytes.size.toLong(), result.size.toLong())
        for ((i, expectedByte) in expectedBytes.withIndex()) {
            var expected = expectedByte.toLong()
            var actual = result[i].toLong()
            Assert.assertEquals(expected, actual)
        }
    }

    @Test
    @Throws(Exception::class)
    fun hashCodeOfTwoDifferentObjectsAreDifferent() {
        val iD3v2PopmFrameData1 = ID3v2PopmFrameData(false, 0)
        val iD3v2PopmFrameData2 = ID3v2PopmFrameData(false, 1)
        Assert.assertFalse(iD3v2PopmFrameData1.hashCode() == iD3v2PopmFrameData2.hashCode())
    }

    @Test
    @Throws(Exception::class)
    fun twoEquivalentObjectsAreEquals() {
        val iD3v2PopmFrameData1 = ID3v2PopmFrameData(false, 0)
        val iD3v2PopmFrameData2 = ID3v2PopmFrameData(false, 0)
        Assert.assertEquals(iD3v2PopmFrameData1, iD3v2PopmFrameData2)
    }

    @Test
    @Throws(Exception::class)
    fun sameObjectsAreEquals() {
        val iD3v2PopmFrameData = ID3v2PopmFrameData(false, 0)
        Assert.assertEquals(iD3v2PopmFrameData, iD3v2PopmFrameData)
    }

    @Test
    @Throws(Exception::class)
    fun ID3v2PopmFrameDataIsNotEqualOtherType() {
        val iD3v2PopmFrameData = ID3v2PopmFrameData(false, 0)
        Assert.assertFalse(iD3v2PopmFrameData.equals("a String"))
    }

    @Test
    @Throws(Exception::class)
    fun ID3v2PopmFrameDataIsNotEqualNull() {
        val iD3v2PopmFrameData = ID3v2PopmFrameData(false, 0)
        Assert.assertFalse(iD3v2PopmFrameData.equals(null))
    }

    @Test
    @Throws(Exception::class)
    fun ID3v2PopmFrameDataIsNotEqualOtherWithDifferentRating() {
        val iD3v2PopmFrameData1 = ID3v2PopmFrameData(false, 1)
        val iD3v2PopmFrameData2 = ID3v2PopmFrameData(false, 2)
        Assert.assertFalse(iD3v2PopmFrameData1.equals(iD3v2PopmFrameData2))
    }

    @Test
    @Throws(Exception::class)
    fun ID3v2PopmFrameDataIsNotEqualOtherWithDifferentAddress() {
        val iD3v2PopmFrameData1 = ID3v2PopmFrameData(false, 1)
        iD3v2PopmFrameData1.address = "Address1"
        val iD3v2PopmFrameData2 = ID3v2PopmFrameData(false, 1)
        iD3v2PopmFrameData1.address = "Address2"
        Assert.assertFalse(iD3v2PopmFrameData1.equals(iD3v2PopmFrameData2))
    }

    @Test
    @Throws(Exception::class)
    fun ID3v2PopmFrameDataIsNotEqualOtherWithNullAddress() {
        val iD3v2PopmFrameData1 = ID3v2PopmFrameData(false, 1)
        iD3v2PopmFrameData1.address = "Address1"
        val iD3v2PopmFrameData2 = ID3v2PopmFrameData(false, 1)
        iD3v2PopmFrameData1.address = null
        Assert.assertFalse(iD3v2PopmFrameData1.equals(iD3v2PopmFrameData2))
    }
}