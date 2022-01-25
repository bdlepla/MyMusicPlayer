package com.mpatric.mp3agic

import com.mpatric.mp3agic.ID3v1Genres.matchGenreDescription
import org.junit.Assert
import org.junit.Test

class ID3v1GenresTest {
    @Test
    @Throws(Exception::class)
    fun returnsMinusOneForNonExistentGenre() {
        Assert.assertEquals(-1, matchGenreDescription("non existent").toLong())
    }

    @Test
    @Throws(Exception::class)
    fun returnsCorrectGenreIdForFirstExistentGenre() {
        Assert.assertEquals(0, matchGenreDescription("Blues").toLong())
    }

    @Test
    @Throws(Exception::class)
    fun returnsCorrectGenreIdForPolka() {
        Assert.assertEquals(75, matchGenreDescription("Polka").toLong())
    }

    @Test
    @Throws(Exception::class)
    fun returnsCorrectGenreIdForLastExistentGenre() {
        Assert.assertEquals(147, matchGenreDescription("Synthpop").toLong())
    }

    @Test
    @Throws(Exception::class)
    fun ignoresCase() {
        Assert.assertEquals(137, matchGenreDescription("heavy METAL").toLong())
    }
}