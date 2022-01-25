package com.mpatric.mp3agic

import com.mpatric.mp3agic.ID3v2PictureFrameData
import com.mpatric.mp3agic.EncodedText
import kotlin.Throws
import com.mpatric.mp3agic.BufferTools
import java.io.UnsupportedEncodingException

class ID3v2ObseletePictureFrameData : ID3v2PictureFrameData {
    constructor(unsynchronisation: Boolean) : super(unsynchronisation) {}
    constructor(
        unsynchronisation: Boolean,
        mimeType: String?,
        pictureType: Byte,
        description: EncodedText?,
        imageData: ByteArray?
    ) : super(unsynchronisation, mimeType, pictureType, description, imageData) {
    }

    constructor(unsynchronisation: Boolean, bytes: ByteArray) : super(unsynchronisation, bytes) {}

    @Throws(InvalidDataException::class)
    override fun unpackFrameData(bytes: ByteArray) {
        val filetype: String
        filetype = try {
            BufferTools.byteBufferToString(bytes, 1, 3)
        } catch (e: UnsupportedEncodingException) {
            "unknown"
        }
        mimeType = "image/" + filetype.toLowerCase()
        pictureType = bytes[4]
        var marker: Int = BufferTools.indexOfTerminatorForEncoding(bytes, 5, bytes[0].toInt())
        if (marker >= 0) {
            description = EncodedText(bytes[0], BufferTools.copyBuffer(bytes, 5, marker - 5))
            marker += description!!.terminator.size
        } else {
            description = EncodedText(bytes[0], "")
            marker = 1
        }
        imageData = BufferTools.copyBuffer(bytes, marker, bytes.size - marker)
    }
}