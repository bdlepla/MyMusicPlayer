package com.mpatric.mp3agic

import org.junit.Assert
import org.junit.Test

class InvalidDataExceptionTest {
    @Test
    fun defaultConstructor() {
        val exception = InvalidDataException()
        Assert.assertNull(exception.message)
        Assert.assertNull(exception.cause)
    }

    @Test
    fun constructorWithMessage() {
        val exception = InvalidDataException("A message")
        Assert.assertEquals("A message", exception.message)
        Assert.assertNull(exception.cause)
    }

    @Test
    fun constructorWithMessageAndCause() {
        val exceptionCause: Throwable = IllegalArgumentException("Bad argument")
        val exception = InvalidDataException("A message", exceptionCause)
        Assert.assertEquals("A message", exception.message)
        Assert.assertEquals(exceptionCause, exception.cause)
    }
}