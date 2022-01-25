package com.mpatric.mp3agic

import org.junit.Assert
import org.junit.Test

class BaseExceptionTest {
    @Test
    fun generatesCorrectDetailedMessageForSingleException() {
        val e = BaseException("ONE")
        Assert.assertEquals("ONE", e.message)
        Assert.assertEquals("[com.mpatric.mp3agic.BaseException: ONE]", e.detailedMessage)
    }

    @Test
    fun generatesCorrectDetailedMessageForChainedBaseExceptions() {
        val e1 = BaseException("ONE")
        val e2: BaseException = UnsupportedTagException("TWO", e1)
        val e3: BaseException = NotSupportedException("THREE", e2)
        val e4: BaseException = NoSuchTagException("FOUR", e3)
        val e5: BaseException = InvalidDataException("FIVE", e4)
        Assert.assertEquals("FIVE", e5.message)
        Assert.assertEquals(
            "[com.mpatric.mp3agic.InvalidDataException: FIVE] caused by [com.mpatric.mp3agic.NoSuchTagException: FOUR] caused by [com.mpatric.mp3agic.NotSupportedException: THREE] caused by [com.mpatric.mp3agic.UnsupportedTagException: TWO] caused by [com.mpatric.mp3agic.BaseException: ONE]",
            e5.detailedMessage
        )
    }

    @Test
    fun generatesCorrectDetailedMessageForChainedExceptionsWithOtherExceptionInMix() {
        val e1 = BaseException("ONE")
        val e2: BaseException = UnsupportedTagException("TWO", e1)
        val e3 = Exception("THREE", e2)
        val e4: BaseException = NoSuchTagException("FOUR", e3)
        val e5: BaseException = InvalidDataException("FIVE", e4)
        Assert.assertEquals("FIVE", e5.message)
        Assert.assertEquals(
            "[com.mpatric.mp3agic.InvalidDataException: FIVE] caused by [com.mpatric.mp3agic.NoSuchTagException: FOUR] caused by [java.lang.Exception: THREE] caused by [com.mpatric.mp3agic.UnsupportedTagException: TWO] caused by [com.mpatric.mp3agic.BaseException: ONE]",
            e5.detailedMessage
        )
    }
}