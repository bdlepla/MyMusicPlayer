package com.mpatric.mp3agic


import java.nio.ByteBuffer
import java.util.*
import kotlin.experimental.and
import kotlin.experimental.or

class ID3v2ChapterTOCFrameData : AbstractID3v2FrameData {
    var isRoot = false
    var isOrdered = false
    var id: String? = null
    var childs: Array<String?>? = null

    var subframes: ArrayList<ID3v2Frame>? = ArrayList()

    constructor(unsynchronisation: Boolean) : super(unsynchronisation)
    constructor(
        unsynchronisation: Boolean, isRoot: Boolean, isOrdered: Boolean,
        id: String?, children: Array<String?>?
    ) : super(unsynchronisation) {
        this.isRoot = isRoot
        this.isOrdered = isOrdered
        this.id = id
        childs = children
    }

    constructor(unsynchronisation: Boolean, bytes: ByteArray) : super(unsynchronisation) {
        synchroniseAndUnpackFrameData(bytes)
    }

    @Throws(InvalidDataException::class)
    override fun unpackFrameData(bytes: ByteArray) {
        val bb = ByteBuffer.wrap(bytes)
        id = ByteBufferUtils.extractNullTerminatedString(bb)
        val flags = bb.get()
        if (flags and 0x01 == 0x01.toByte()) {
            isRoot = true
        }
        if (flags and 0x02 == 0x02.toByte()) {
            isOrdered = true
        }
        val childCount = bb.get().toInt() // TODO: 0xFF -> int = 255; byte = -128;
        childs = arrayOfNulls(childCount)
        for (i in 0 until childCount) {
            childs!![i] = ByteBufferUtils.extractNullTerminatedString(bb)
        }
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
        bb.put(flags)
        bb.put(childs!!.size.toByte())
        for (child in childs!!) {
            bb.put(child!!.toByteArray())
            bb.put(0.toByte())
        }
        for (frame in subframes!!) {
            try {
                bb.put(frame.toBytes())
            } catch (e: NotSupportedException) {
                e.printStackTrace()
            }
        }
        return bb.array()
    }

    private val flags: Byte
        get() {
            var b: Byte = 0
            if (isRoot) {
                b = b or 0x01
            }
            if (isOrdered) {
                b = b or 0x02
            }
            return b
        }
    override val length: Int
        get() {
            var length = 3
            if (id != null) length += id!!.length
            if (childs != null) {
                length += childs!!.size
                for (child in childs!!) {
                    length += child!!.length
                }
            }
            if (subframes != null) {
                for (frame in subframes!!) {
                    length += frame.length
                }
            }
            return length
        }

    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("ID3v2ChapterTOCFrameData [isRoot=")
        builder.append(isRoot)
        builder.append(", isOrdered=")
        builder.append(isOrdered)
        builder.append(", id=")
        builder.append(id)
        builder.append(", children=")
        builder.append(Arrays.toString(childs))
        builder.append(", subframes=")
        builder.append(subframes)
        builder.append("]")
        return builder.toString()
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = super.hashCode()
        result = prime * result + Arrays.hashCode(childs)
        result = prime * result + if (id == null) 0 else id.hashCode()
        result = prime * result + if (isOrdered) 1231 else 1237
        result = prime * result + if (isRoot) 1231 else 1237
        result = (prime * result
                + if (subframes == null) 0 else subframes.hashCode())
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (!super.equals(other)) return false
        if (javaClass != other.javaClass) return false
        val other2 = other as ID3v2ChapterTOCFrameData
        if (!Arrays.equals(childs, other2.childs)) return false
        if (id == null) {
            if (other2.id != null) return false
        } else if (id != other2.id) return false
        if (isOrdered != other2.isOrdered) return false
        if (isRoot != other2.isRoot) return false
        if (subframes == null) {
            if (other2.subframes != null) return false
        } else if (subframes != other2.subframes) return false
        return true
    }
}