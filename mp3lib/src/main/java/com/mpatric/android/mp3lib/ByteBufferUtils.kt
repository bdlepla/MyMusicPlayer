package com.mpatric.mp3agic

import java.nio.ByteBuffer

object ByteBufferUtils {
    fun extractNullTerminatedString(bb: ByteBuffer): String {
        val start = bb.position()
        val buffer = ByteArray(bb.remaining())
        bb[buffer]
        var s = String(buffer)
        val nullPos = s.indexOf(0.toChar())
        s = s.substring(0, nullPos)
        bb.position(start + s.length + 1)
        return s
    }
}