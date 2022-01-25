package com.mpatric.mp3agic

import java.io.UnsupportedEncodingException

class ID3v2UrlFrameData : AbstractID3v2FrameData {
    var url: String? = null
    var description: EncodedText? = null

    constructor(unsynchronisation: Boolean) : super(unsynchronisation) {}
    constructor(unsynchronisation: Boolean, description: EncodedText?, url: String?) : super(
        unsynchronisation
    ) {
        this.description = description
        this.url = url
    }

    constructor(unsynchronisation: Boolean, bytes: ByteArray) : super(unsynchronisation) {
        synchroniseAndUnpackFrameData(bytes)
    }

    @Throws(InvalidDataException::class)
    protected override fun unpackFrameData(bytes: ByteArray) {
        var marker: Int = BufferTools.indexOfTerminatorForEncoding(bytes, 1, bytes[0].toInt())
        if (marker >= 0) {
            description = EncodedText(bytes[0], BufferTools.copyBuffer(bytes, 1, marker - 1))
            marker += description!!.terminator.size
        } else {
            description = EncodedText(bytes[0], "")
            marker = 1
        }
        url = try {
            BufferTools.byteBufferToString(bytes, marker, bytes.size - marker)
        } catch (e: UnsupportedEncodingException) {
            ""
        }
    }

    protected override fun packFrameData(): ByteArray {
        val bytes = ByteArray(length)
        if (description != null) bytes[0] = description!!.getTextEncoding() else bytes[0] = 0
        var marker = 1
        if (description != null) {
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
        if (url != null && url!!.length > 0) {
            try {
                BufferTools.stringIntoByteBuffer(url!!, 0, url!!.length, bytes, marker)
            } catch (e: UnsupportedEncodingException) {
            }
        }
        return bytes
    }

    protected override val length: Int
        protected get() {
            var length = 1
            if (description != null) length += description!!.toBytes(true, true).size else length++
            if (url != null) length += url!!.length
            return length
        }

    override fun hashCode(): Int {
        val prime = 31
        var result = super.hashCode()
        result = prime * result + if (description == null) 0 else description.hashCode()
        result = prime * result + if (url == null) 0 else url.hashCode()
        return result
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) return true
        if (!super.equals(obj)) return false
        if (javaClass != obj.javaClass) return false
        val other = obj as ID3v2UrlFrameData
        if (description == null) {
            if (other.description != null) return false
        } else if (description != other.description) return false
        if (url == null) {
            if (other.url != null) return false
        } else if (url != other.url) return false
        return true
    }
}