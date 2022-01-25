package com.mpatric.mp3agic

import java.nio.ByteBuffer
import java.util.*

class ID3v2ChapterFrameData : AbstractID3v2FrameData {
    var id: String? = null
    var startTime = 0
    var endTime = 0
    var startOffset = 0
    var endOffset = 0
    var subframes: ArrayList<ID3v2Frame>? = ArrayList()

    constructor(unsynchronisation: Boolean) : super(unsynchronisation)
    constructor(
        unsynchronisation: Boolean, id: String?, startTime: Int,
        endTime: Int, startOffset: Int, endOffset: Int
    ) : super(unsynchronisation) {
        this.id = id
        this.startTime = startTime
        this.endTime = endTime
        this.startOffset = startOffset
        this.endOffset = endOffset
    }

    constructor(unsynchronisation: Boolean, bytes: ByteArray) : super(unsynchronisation) {
        synchroniseAndUnpackFrameData(bytes)
    }

    @Throws(InvalidDataException::class)
    override fun unpackFrameData(bytes: ByteArray) {
        val bb = ByteBuffer.wrap(bytes)
        id = ByteBufferUtils.extractNullTerminatedString(bb)
        bb.position(id!!.length + 1)
        startTime = bb.int
        endTime = bb.int
        startOffset = bb.int
        endOffset = bb.int
        var offset = bb.position()
        while (offset < bytes.size) {
            val frame = ID3v2Frame(bytes, offset)
            offset += frame.length
            subframes!!.add(frame)
        }
    }

    fun addSubframe(id: String, frame: AbstractID3v2FrameData) {
        subframes!!.add(ID3v2Frame(id, frame.toBytes()))
    }

    override fun packFrameData(): ByteArray {
        val bb = ByteBuffer.allocate(length)
        bb.put(id!!.toByteArray())
        bb.put(0.toByte())
        bb.putInt(startTime)
        bb.putInt(endTime)
        bb.putInt(startOffset)
        bb.putInt(endOffset)
        for (frame in subframes!!) {
            try {
                bb.put(frame.toBytes())
            } catch (e: NotSupportedException) {
                e.printStackTrace()
            }
        }
        return bb.array()
    }

    override val length: Int
        get() {
            var length = 1
            length += 16
            if (id != null) length += id!!.length
            if (subframes != null) {
                for (frame in subframes!!) {
                    length += frame.length
                }
            }
            return length
        }

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("ID3v2ChapterFrameData [id=")
        builder.append(id)
        builder.append(", startTime=")
        builder.append(startTime)
        builder.append(", endTime=")
        builder.append(endTime)
        builder.append(", startOffset=")
        builder.append(startOffset)
        builder.append(", endOffset=")
        builder.append(endOffset)
        builder.append(", subframes=")
        builder.append(subframes)
        builder.append("]")
        return builder.toString()
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + endOffset
        result = prime * result + endTime
        result = prime * result + if (id == null) 0 else id.hashCode()
        result = prime * result + startOffset
        result = prime * result + startTime
        result = (prime * result
                + if (subframes == null) 0 else subframes.hashCode())
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (!super.equals(other)) return false
        if (javaClass != other.javaClass) return false
        val other2 = other as ID3v2ChapterFrameData
        if (endOffset != other2.endOffset) return false
        if (endTime != other2.endTime) return false
        if (id == null) {
            if (other2.id != null) return false
        } else if (id != other2.id) return false
        if (startOffset != other2.startOffset) return false
        if (startTime != other2.startTime) return false
        if (subframes == null) {
            if (other2.subframes != null) return false
        } else if (subframes != other2.subframes) return false
        return true
    }
}