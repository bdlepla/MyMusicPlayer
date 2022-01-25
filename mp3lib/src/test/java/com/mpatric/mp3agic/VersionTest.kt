package com.mpatric.mp3agic

import org.junit.Test
import org.junit.Assert.assertEquals

class VersionTest {
    @Test
    fun returnsVersion() {
        assertEquals("UNKNOWN-SNAPSHOT", Version.version)
    }

    @Test
    fun returnsUrl() {
        assertEquals("http://github.com/mpatric/mp3agic", Version.url)
    }

    @Test
    fun returnsVersionAndUrlAsString() {
        assertEquals("UNKNOWN-SNAPSHOT - http://github.com/mpatric/mp3agic", Version.asString())
    }
}