package com.mpatric.mp3agic

import com.mpatric.mp3agic.AbstractID3v2FrameData
import com.mpatric.mp3agic.EncodedText
import kotlin.Throws
import com.mpatric.mp3agic.BufferTools
import com.mpatric.mp3agic.ID3v2PictureFrameData
import java.io.UnsupportedEncodingException
import java.util.*

open class ID3v2PictureFrameData : AbstractID3v2FrameData {
    @JvmField
    var mimeType: String? = null
    @JvmField
    var pictureType: Byte = 0
    var description: EncodedText? = null
    @JvmField
    var imageData: ByteArray? = null

    constructor(unsynchronisation: Boolean) : super(unsynchronisation) {}
    constructor(
        unsynchronisation: Boolean,
        mimeType: String?,
        pictureType: Byte,
        description: EncodedText?,
        imageData: ByteArray?
    ) : super(unsynchronisation) {
        this.mimeType = mimeType
        this.pictureType = pictureType
        this.description = description
        this.imageData = imageData
    }

    constructor(unsynchronisation: Boolean, bytes: ByteArray) : super(unsynchronisation) {
        synchroniseAndUnpackFrameData(bytes)
    }

    @Throws(InvalidDataException::class)
    protected override fun unpackFrameData(bytes: ByteArray) {
        var marker: Int = BufferTools.indexOfTerminator(bytes, 1, 1)
        mimeType = if (marker >= 0) {
            try {
                BufferTools.byteBufferToString(bytes, 1, marker - 1)
            } catch (e: UnsupportedEncodingException) {
                "image/unknown"
            }
        } else {
            "image/unknown"
        }
        pictureType = bytes[marker + 1]
        marker += 2
        var marker2: Int = BufferTools.indexOfTerminatorForEncoding(bytes, marker, bytes[0].toInt())
        if (marker2 >= 0) {
            description =
                EncodedText(bytes[0], BufferTools.copyBuffer(bytes, marker, marker2 - marker))
            marker2 += description!!.terminator.size
        } else {
            description = EncodedText(bytes[0], "")
            marker2 = marker
        }
        imageData = BufferTools.copyBuffer(bytes, marker2, bytes.size - marker2)
    }

    protected override fun packFrameData(): ByteArray {
        val bytes = ByteArray(length)
        if (description != null) bytes[0] = description!!.getTextEncoding() else bytes[0] = 0
        var mimeTypeLength = 0
        if (mimeType != null && mimeType!!.length > 0) {
            mimeTypeLength = mimeType!!.length
            try {
                BufferTools.stringIntoByteBuffer(mimeType!!, 0, mimeTypeLength, bytes, 1)
            } catch (e: UnsupportedEncodingException) {
            }
        }
        var marker = mimeTypeLength + 1
        bytes[marker++] = 0
        bytes[marker++] = pictureType
        if (description != null && description!!.toBytes().size > 0) {
            val descriptionBytes: ByteArray = description!!.toBytes(true, true)
            BufferTools.copyIntoByteBuffer(
                descriptionBytes,
                0,
                descriptionBytes.size,
                bytes,
                marker
            )
            marker += descriptionBytes.size
        } else {
            bytes[marker++] = 0
        }
        if (imageData != null && imageData!!.size > 0) {
            BufferTools.copyIntoByteBuffer(imageData!!, 0, imageData!!.size, bytes, marker)
        }
        return bytes
    }

    protected override val length: Int
        protected get() {
            var length = 3
            if (mimeType != null) length += mimeType!!.length
            if (description != null) length += description!!.toBytes(true, true).size else length++
            if (imageData != null) length += imageData!!.size
            return length
        }

    override fun hashCode(): Int {
        val prime = 31
        var result = super.hashCode()
        result = (prime * result
                + if (description == null) 0 else description.hashCode())
        result = prime * result + Arrays.hashCode(imageData)
        result = (prime * result
                + if (mimeType == null) 0 else mimeType.hashCode())
        result = prime * result + pictureType
        return result
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) return true
        if (!super.equals(obj)) return false
        if (javaClass != obj.javaClass) return false
        val other = obj as ID3v2PictureFrameData
        if (description == null) {
            if (other.description != null) return false
        } else if (description != other.description) return false
        if (!Arrays.equals(imageData, other.imageData)) return false
        if (mimeType == null) {
            if (other.mimeType != null) return false
        } else if (mimeType != other.mimeType) return false
        return if (pictureType != other.pictureType) false else true
    }
}