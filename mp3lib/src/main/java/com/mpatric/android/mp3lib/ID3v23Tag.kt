package com.mpatric.mp3agic

open class ID3v23Tag : AbstractID3v2Tag {
    constructor() : super() {
        version = VERSION
    }

    constructor(buffer: ByteArray) : super(buffer) {}

    protected override fun unpackFlags(buffer: ByteArray) {
        unsynchronisation = BufferTools.checkBit(
            buffer[AbstractID3v2Tag.FLAGS_OFFSET],
            AbstractID3v2Tag.UNSYNCHRONISATION_BIT
        )
        extendedHeader = BufferTools.checkBit(
            buffer[AbstractID3v2Tag.FLAGS_OFFSET],
            AbstractID3v2Tag.EXTENDED_HEADER_BIT
        )
        experimental = BufferTools.checkBit(
            buffer[AbstractID3v2Tag.FLAGS_OFFSET],
            AbstractID3v2Tag.EXPERIMENTAL_BIT
        )
    }

    protected override fun packFlags(bytes: ByteArray, offset: Int) {
        bytes[offset + AbstractID3v2Tag.FLAGS_OFFSET] = BufferTools.setBit(
            bytes[offset + AbstractID3v2Tag.FLAGS_OFFSET],
            AbstractID3v2Tag.UNSYNCHRONISATION_BIT,
            unsynchronisation
        )
        bytes[offset + AbstractID3v2Tag.FLAGS_OFFSET] = BufferTools.setBit(
            bytes[offset + AbstractID3v2Tag.FLAGS_OFFSET],
            AbstractID3v2Tag.EXTENDED_HEADER_BIT,
            extendedHeader
        )
        bytes[offset + AbstractID3v2Tag.FLAGS_OFFSET] = BufferTools.setBit(
            bytes[offset + AbstractID3v2Tag.FLAGS_OFFSET],
            AbstractID3v2Tag.EXPERIMENTAL_BIT,
            experimental
        )
    }

    companion object {
        const val VERSION = "3.0"
        const val PICTURETYPE_OTHER:Byte = 1
    }
}