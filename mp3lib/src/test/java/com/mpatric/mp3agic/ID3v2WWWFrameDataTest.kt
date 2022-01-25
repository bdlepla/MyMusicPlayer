package com.mpatric.mp3agic

import org.junit.Assert
import org.junit.Test

class ID3v2WWWFrameDataTest {
    @Test
    fun getsAndSetsId() {
        val frameData = ID3v2WWWFrameData(false)
        frameData.url = "My URL"
        Assert.assertEquals("My URL", frameData.url)
    }
}