package com.mpatric.mp3agic

class ID3v24Frame : ID3v2Frame {
    constructor(buffer: ByteArray, offset: Int) : super(buffer, offset) {}
    constructor(id: String, data: ByteArray) : super(id, data) {}

    protected override fun unpackDataLength(buffer: ByteArray, offset: Int) {
        dataLength = BufferTools.unpackSynchsafeInteger(
            buffer[offset + ID3v2Frame.DATA_LENGTH_OFFSET],
            buffer[offset + ID3v2Frame.DATA_LENGTH_OFFSET + 1],
            buffer[offset + ID3v2Frame.DATA_LENGTH_OFFSET + 2],
            buffer[offset + ID3v2Frame.DATA_LENGTH_OFFSET + 3]
        )
    }

    protected override fun packDataLength(): ByteArray {
        return BufferTools.packSynchsafeInteger(dataLength)
    }

    override fun equals(obj: Any?): Boolean {
        return if (obj !is ID3v24Frame) false else super.equals(obj)
    }
}