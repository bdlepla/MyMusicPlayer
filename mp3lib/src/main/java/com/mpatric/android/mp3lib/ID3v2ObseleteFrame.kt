package com.mpatric.mp3agic

import com.mpatric.mp3agic.ID3v2Frame
import com.mpatric.mp3agic.BufferTools
import com.mpatric.mp3agic.ID3v2ObseleteFrame
import kotlin.Throws
import com.mpatric.mp3agic.NotSupportedException

class ID3v2ObseleteFrame : ID3v2Frame {
    constructor(buffer: ByteArray, offset: Int) : super(buffer, offset) {}
    constructor(id: String, data: ByteArray) : super(id, data) {}

    protected override fun unpackHeader(buffer: ByteArray, offset: Int): Int {
        id = BufferTools.byteBufferToStringIgnoringEncodingIssues(
            buffer,
            offset + ID_OFFSET,
            ID_LENGTH
        )
        unpackDataLength(buffer, offset)
        return offset + HEADER_LENGTH
    }

    protected override fun unpackDataLength(buffer: ByteArray, offset: Int) {
        dataLength = BufferTools.unpackInteger(
            0.toByte(),
            buffer[offset + DATA_LENGTH_OFFSET],
            buffer[offset + DATA_LENGTH_OFFSET + 1],
            buffer[offset + DATA_LENGTH_OFFSET + 2]
        )
    }

    @Throws(NotSupportedException::class)
    override fun packFrame(bytes: ByteArray, offset: Int) {
        throw NotSupportedException("Packing Obselete frames is not supported")
    }

    override val length: Int
        get() = dataLength + HEADER_LENGTH

    companion object {
        private const val HEADER_LENGTH = 6
        private const val ID_OFFSET = 0
        private const val ID_LENGTH = 3
        protected const val DATA_LENGTH_OFFSET = 3
    }
}