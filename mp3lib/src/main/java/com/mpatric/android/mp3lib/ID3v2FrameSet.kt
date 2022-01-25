package com.mpatric.mp3agic

import java.util.*

class ID3v2FrameSet(val id: String?) {
    val frames: ArrayList<ID3v2Frame> = ArrayList<ID3v2Frame>()
    fun clear() {
        frames.clear()
    }

    fun addFrame(frame: ID3v2Frame) {
        frames.add(frame)
    }

    override fun toString(): String {
        return id + ": " + frames.size
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + (frames.hashCode() ?: 0)
        result = prime * result + (id?.hashCode() ?: 0)
        return result
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) return true
        if (obj == null) return false
        if (javaClass != obj.javaClass) return false
        val other = obj as ID3v2FrameSet
        if (frames == null) {
            if (other.frames != null) return false
        } else if (frames != other.frames) return false
        if (id == null) {
            if (other.id != null) return false
        } else if (id != other.id) return false
        return true
    }

}