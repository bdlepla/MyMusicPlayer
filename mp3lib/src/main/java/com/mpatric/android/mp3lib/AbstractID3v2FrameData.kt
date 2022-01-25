package com.mpatric.mp3agic

import com.mpatric.mp3agic.BufferTools.sizeSynchronisationWouldSubtract
import com.mpatric.mp3agic.BufferTools.sizeUnsynchronisationWouldAdd
import com.mpatric.mp3agic.BufferTools.synchroniseBuffer
import com.mpatric.mp3agic.BufferTools.unsynchroniseBuffer

abstract class AbstractID3v2FrameData(var unsynchronisation: Boolean) {
    @Throws(InvalidDataException::class)
    protected fun synchroniseAndUnpackFrameData(bytes: ByteArray) {
        if (unsynchronisation && sizeSynchronisationWouldSubtract(bytes) > 0) {
            val synchronisedBytes = synchroniseBuffer(bytes)
            unpackFrameData(synchronisedBytes)
        } else {
            unpackFrameData(bytes)
        }
    }

    protected fun packAndUnsynchroniseFrameData(): ByteArray {
        val bytes = packFrameData()
        return if (unsynchronisation && sizeUnsynchronisationWouldAdd(bytes) > 0) {
            unsynchroniseBuffer(bytes)
        } else bytes
    }

    fun toBytes(): ByteArray {
        return packAndUnsynchroniseFrameData()
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + if (unsynchronisation) 1231 else 1237
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (javaClass != other.javaClass) return false
        val other2 = other as AbstractID3v2FrameData
        return unsynchronisation == other2.unsynchronisation
    }

    @Throws(InvalidDataException::class)
    protected abstract fun unpackFrameData(bytes: ByteArray)
    protected abstract fun packFrameData(): ByteArray
    protected abstract val length: Int

}