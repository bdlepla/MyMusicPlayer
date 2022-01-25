package com.mpatric.mp3agic

class ID3v2TextFrameData : AbstractID3v2FrameData {
    var text: EncodedText? = null

    constructor(unsynchronisation: Boolean) : super(unsynchronisation) {}
    constructor(unsynchronisation: Boolean, text: EncodedText?) : super(unsynchronisation) {
        this.text = text
    }

    constructor(unsynchronisation: Boolean, bytes: ByteArray) : super(unsynchronisation) {
        synchroniseAndUnpackFrameData(bytes)
    }

    @Throws(InvalidDataException::class)
    protected override fun unpackFrameData(bytes: ByteArray) {
        text = EncodedText(bytes[0], BufferTools.copyBuffer(bytes, 1, bytes.size - 1))
    }

    protected override fun packFrameData(): ByteArray {
        val bytes = ByteArray(length)
        if (text != null) {
            bytes[0] = text!!.getTextEncoding()
            val textBytes: ByteArray = text!!.toBytes(true, false)
            if (textBytes.size > 0) {
                BufferTools.copyIntoByteBuffer(textBytes, 0, textBytes.size, bytes, 1)
            }
        }
        return bytes
    }

    protected override val length: Int
        protected get() {
            var length = 1
            if (text != null) length += text!!.toBytes(true, false).size
            return length
        }


    override fun hashCode(): Int {
        val prime = 31
        var result = super.hashCode()
        result = prime * result + if (text == null) 0 else text.hashCode()
        return result
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) return true
        if (!super.equals(obj)) return false
        if (javaClass != obj.javaClass) return false
        val other = obj as ID3v2TextFrameData
        if (text == null) {
            if (other.text != null) return false
        } else if (text != other.text) return false
        return true
    }
}