package com.mpatric.mp3agic

open class ID3v22Tag : AbstractID3v2Tag {
    constructor() : super() {
        version = VERSION
    }

    constructor(buffer: ByteArray) : super(buffer) {}
    constructor(buffer: ByteArray, obseleteFormat: Boolean) : super(buffer, obseleteFormat) {}

    public override fun unpackFlags(bytes: ByteArray) {
        unsynchronisation = BufferTools.checkBit(
            bytes[AbstractID3v2Tag.FLAGS_OFFSET],
            AbstractID3v2Tag.UNSYNCHRONISATION_BIT
        )
        compression = BufferTools.checkBit(
            bytes[AbstractID3v2Tag.FLAGS_OFFSET],
            AbstractID3v2Tag.COMPRESSION_BIT
        )
    }

    public override fun packFlags(bytes: ByteArray, offset: Int) {
        bytes[offset + AbstractID3v2Tag.FLAGS_OFFSET] = BufferTools.setBit(
            bytes[offset + AbstractID3v2Tag.FLAGS_OFFSET],
            AbstractID3v2Tag.UNSYNCHRONISATION_BIT,
            unsynchronisation
        )
        bytes[offset + AbstractID3v2Tag.FLAGS_OFFSET] = BufferTools.setBit(
            bytes[offset + AbstractID3v2Tag.FLAGS_OFFSET],
            AbstractID3v2Tag.COMPRESSION_BIT,
            compression
        )
    }

    companion object {
        const val VERSION = "2.0"
    }
}