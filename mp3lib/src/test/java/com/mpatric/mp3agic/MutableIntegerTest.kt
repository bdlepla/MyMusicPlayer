package com.mpatric.mp3agic

import org.junit.Assert
import org.junit.Test

class MutableIntegerTest {
    @Test
    fun initializesValue() {
        val integer = MutableInteger(8)
        Assert.assertEquals(8, integer.value.toLong())
    }

    @Test
    fun incrementsValue() {
        val integer = MutableInteger(8)
        integer.increment()
        Assert.assertEquals(9, integer.value.toLong())
    }

    @Test
    fun setsValue() {
        val integer = MutableInteger(8)
        integer.value = 5
        Assert.assertEquals(5, integer.value.toLong())
    }

    @Test
    fun equalsItself() {
        val integer = MutableInteger(8)
        Assert.assertEquals(integer, integer)
    }

    @Test
    fun equalIfValueEqual() {
        val eight = MutableInteger(8)
        val eightAgain = MutableInteger(8)
        Assert.assertEquals(eight, eightAgain)
    }

    @Test
    fun notEqualToNull() {
        val integer = MutableInteger(8)
        Assert.assertFalse(integer.equals(null))
    }

    @Test
    fun notEqualToDifferentClass() {
        val integer = MutableInteger(8)
        Assert.assertFalse(integer.equals("8"))
    }

    @Test
    fun notEqualIfValueNotEqual() {
        val eight = MutableInteger(8)
        val nine = MutableInteger(9)
        Assert.assertNotEquals(eight, nine)
    }

    @Test
    fun hashCodeIsConsistent() {
        val integer = MutableInteger(8)
        Assert.assertEquals(integer.hashCode().toLong(), integer.hashCode().toLong())
    }

    @Test
    fun equalObjectsHaveSameHashCode() {
        val eight = MutableInteger(8)
        val eightAgain = MutableInteger(8)
        Assert.assertEquals(eight.hashCode().toLong(), eightAgain.hashCode().toLong())
    }
}