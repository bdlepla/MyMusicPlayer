package com.mpatric.mp3agic

import java.io.UnsupportedEncodingException

class ID3v2CommentFrameData : AbstractID3v2FrameData {
    var language: String? = null
    private var description: EncodedText? = null
    private var comment: EncodedText? = null

    constructor(unsynchronisation: Boolean) : super(unsynchronisation)
    constructor(
        unsynchronisation: Boolean,
        language: String?,
        description: EncodedText?,
        comment: EncodedText?
    ) : super(unsynchronisation) {
        require(!(description != null && comment != null && description.getTextEncoding() != comment.getTextEncoding())) { "description and comment must have same text encoding" }
        this.language = language
        this.description = description
        this.comment = comment
    }

    constructor(unsynchronisation: Boolean, bytes: ByteArray) : super(unsynchronisation) {
        synchroniseAndUnpackFrameData(bytes)
    }

    @Throws(InvalidDataException::class)
    override fun unpackFrameData(bytes: ByteArray) {
        language = try {
            BufferTools.byteBufferToString(bytes, 1, 3)
        } catch (e: UnsupportedEncodingException) {
            ""
        }
        var marker: Int = BufferTools.indexOfTerminatorForEncoding(bytes, 4, bytes[0].toInt())
        if (marker >= 4) {
            description = EncodedText(bytes[0], BufferTools.copyBuffer(bytes, 4, marker - 4))
            marker += description!!.terminator.size
        } else {
            description = EncodedText(bytes[0], "")
            marker = 4
        }
        comment = EncodedText(bytes[0], BufferTools.copyBuffer(bytes, marker, bytes.size - marker))
    }

    override fun packFrameData(): ByteArray {
        val bytes = ByteArray(length)
        if (comment != null) bytes[0] = comment!!.getTextEncoding() else bytes[0] = 0
        val langPadded: String = when {
            language == null -> {
                DEFAULT_LANGUAGE
            }
            language!!.length > 3 -> {
                language!!.substring(0, 3)
            }
            else -> {
                BufferTools.padStringRight(language!!, 3, '\u0000')
            }
        }
        try {
            BufferTools.stringIntoByteBuffer(langPadded, 0, 3, bytes, 1)
        } catch (e: UnsupportedEncodingException) {
        }
        var marker = 4
        marker += if (description != null) {
            val descriptionBytes: ByteArray = description!!.toBytes(true, true)
            BufferTools.copyIntoByteBuffer(
                descriptionBytes,
                0,
                descriptionBytes.size,
                bytes,
                marker
            )
            descriptionBytes.size
        } else {
            val terminatorBytes = comment?.terminator ?: byteArrayOf(0)
            BufferTools.copyIntoByteBuffer(terminatorBytes, 0, terminatorBytes.size, bytes, marker)
            terminatorBytes.size
        }
        if (comment != null) {
            val commentBytes: ByteArray = comment!!.toBytes(true, false)
            BufferTools.copyIntoByteBuffer(commentBytes, 0, commentBytes.size, bytes, marker)
        }
        return bytes
    }

    override val length: Int
        get() {
            var length = 4
            if (description != null) length += description!!.toBytes(
                true,
                true
            ).size else length += comment?.terminator?.size ?: 1
            if (comment != null) length += comment!!.toBytes(true, false).size
            return length
        }

    fun getComment(): EncodedText? {
        return comment
    }

    fun setComment(comment: EncodedText?) {
        this.comment = comment
    }

    fun getDescription(): EncodedText? {
        return description
    }

    fun setDescription(description: EncodedText?) {
        this.description = description
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = super.hashCode()
        result = prime * result + if (comment == null) 0 else comment.hashCode()
        result = (prime * result
                + if (description == null) 0 else description.hashCode())
        result = (prime * result
                + if (language == null) 0 else language.hashCode())
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (!super.equals(other)) return false
        if (javaClass != other.javaClass) return false
        val other2 = other as ID3v2CommentFrameData
        if (comment == null) {
            if (other2.comment != null) return false
        } else if (comment != other2.comment) return false
        if (description == null) {
            if (other2.description != null) return false
        } else if (description != other2.description) return false
        if (language == null) {
            if (other2.language != null) return false
        } else if (language != other2.language) return false
        return true
    }

    companion object {
        private const val DEFAULT_LANGUAGE = "eng"
    }
}