package com.mpatric.mp3agic

import java.io.UnsupportedEncodingException

class ID3v2WWWFrameData : AbstractID3v2FrameData {
    var url: String? = null

    constructor(unsynchronisation: Boolean) : super(unsynchronisation) {}
    constructor(unsynchronisation: Boolean, url: String?) : super(unsynchronisation) {
        this.url = url
    }

    constructor(unsynchronisation: Boolean, bytes: ByteArray) : super(unsynchronisation) {
        synchroniseAndUnpackFrameData(bytes)
    }

    @Throws(InvalidDataException::class)
    protected override fun unpackFrameData(bytes: ByteArray) {
        url = try {
            BufferTools.byteBufferToString(bytes, 0, bytes.size)
        } catch (e: UnsupportedEncodingException) {
            ""
        }
    }

    protected override fun packFrameData(): ByteArray {
        val bytes = ByteArray(length)
        if (url != null && url!!.length > 0) {
            try {
                BufferTools.stringIntoByteBuffer(url!!, 0, url!!.length, bytes, 0)
            } catch (e: UnsupportedEncodingException) {
            }
        }
        return bytes
    }

    protected override val length: Int
        protected get() {
            var length = 0
            if (url != null) length = url!!.length
            return length
        }
}