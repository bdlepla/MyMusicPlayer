package com.mpatric.mp3agic

import com.mpatric.mp3agic.UnsupportedTagException
import org.junit.Assert
import org.junit.Test
import java.lang.IllegalArgumentException

class UnsupportedTagExceptionTest {
    @Test
    fun defaultConstructor() {
        val exception = UnsupportedTagException()
        Assert.assertNull(exception.message)
        Assert.assertNull(exception.cause)
    }

    @Test
    fun constructorWithMessage() {
        val exception = UnsupportedTagException("A message")
        Assert.assertEquals("A message", exception.message)
        Assert.assertNull(exception.cause)
    }

    @Test
    fun constructorWithMessageAndCause() {
        val exceptionCause: Throwable = IllegalArgumentException("Bad argument")
        val exception = UnsupportedTagException("A message", exceptionCause)
        Assert.assertEquals("A message", exception.message)
        Assert.assertEquals(exceptionCause, exception.cause)
    }
}