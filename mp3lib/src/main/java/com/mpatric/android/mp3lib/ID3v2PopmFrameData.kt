package com.mpatric.mp3agic

import com.mpatric.mp3agic.AbstractID3v2FrameData
import com.mpatric.mp3agic.ID3v2PopmFrameData
import kotlin.Throws
import com.mpatric.mp3agic.BufferTools
import java.io.UnsupportedEncodingException
import java.util.*

class ID3v2PopmFrameData : AbstractID3v2FrameData {
    var address: String? = ""
    var rating = -1

    companion object {
        protected const val WMP9_ADDRESS = "Windows Media Player 9 Series"
        private val byteToRating: MutableMap<Byte, Int> = HashMap(5)
        private val wmp9encodedRatings = byteArrayOf(
            0x00.toByte(), 0x01.toByte(), 0x40.toByte(), 0x80.toByte(), 0xC4.toByte(),
            0xFF.toByte()
        )

        init {
            for (i in 0..5) {
                byteToRating[wmp9encodedRatings[i]] = i
            }
        }
    }

    constructor(unsynchronisation: Boolean, bytes: ByteArray) : super(unsynchronisation) {
        synchroniseAndUnpackFrameData(bytes)
    }

    constructor(unsynchronisation: Boolean, rating: Int) : super(unsynchronisation) {
        address = WMP9_ADDRESS
        this.rating = rating
    }

    @Throws(InvalidDataException::class)
    protected override fun unpackFrameData(bytes: ByteArray) {
        address = try {
            BufferTools.byteBufferToString(bytes, 0, bytes.size - 2)
        } catch (e: UnsupportedEncodingException) {
            ""
        }
        val ratingByte = bytes[bytes.size - 1]
        rating = if (byteToRating.containsKey(ratingByte)) {
            byteToRating[ratingByte]!!
        } else {
            -1
        }
    }

    public override fun packFrameData(): ByteArray {
        var bytes = address!!.toByteArray()
        bytes = Arrays.copyOf(bytes, address!!.length + 2)
        bytes[bytes.size - 2] = 0
        bytes[bytes.size - 1] = wmp9encodedRatings[rating]
        return bytes
    }

    public override val length: Int
        get() = address!!.length + 2

    override fun hashCode(): Int {
        val prime = 31
        var result = super.hashCode()
        result = prime * result + if (address == null) 0 else address.hashCode()
        result = prime * result + rating
        return result
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) return true
        if (!super.equals(obj)) return false
        if (javaClass != obj.javaClass) return false
        val other = obj as ID3v2PopmFrameData
        if (address == null) {
            if (other.address != null) return false
        } else if (address != other.address) return false
        return if (rating != other.rating) false else true
    }
}