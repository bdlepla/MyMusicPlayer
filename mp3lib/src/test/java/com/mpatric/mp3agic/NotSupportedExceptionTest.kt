package com.mpatric.mp3agic

import org.junit.Assert
import org.junit.Test

class NotSupportedExceptionTest {
    @Test
    fun defaultConstructor() {
        val exception = NotSupportedException()
        Assert.assertNull(exception.message)
        Assert.assertNull(exception.cause)
    }

    @Test
    fun constructorWithMessage() {
        val exception = NotSupportedException("A message")
        Assert.assertEquals("A message", exception.message)
        Assert.assertNull(exception.cause)
    }

    @Test
    fun constructorWithMessageAndCause() {
        val exceptionCause: Throwable = IllegalArgumentException("Bad argument")
        val exception = NotSupportedException("A message", exceptionCause)
        Assert.assertEquals("A message", exception.message)
        Assert.assertEquals(exceptionCause, exception.cause)
    }
}