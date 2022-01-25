package com.mpatric.mp3agic

open class ID3v24Tag : AbstractID3v2Tag {
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
        footer =
            BufferTools.checkBit(buffer[AbstractID3v2Tag.FLAGS_OFFSET], AbstractID3v2Tag.FOOTER_BIT)
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
        bytes[offset + AbstractID3v2Tag.FLAGS_OFFSET] = BufferTools.setBit(
            bytes[offset + AbstractID3v2Tag.FLAGS_OFFSET],
            AbstractID3v2Tag.FOOTER_BIT,
            footer
        )
    }

    protected override fun useFrameUnsynchronisation(): Boolean {
        return unsynchronisation
    }

    @Throws(InvalidDataException::class)
    protected override fun createFrame(buffer: ByteArray, currentOffset: Int): ID3v2Frame {
        return ID3v24Frame(buffer, currentOffset)
    }

    protected override fun createFrame(id: String, data: ByteArray): ID3v2Frame {
        return ID3v24Frame(id, data)
    }

    override var genreDescription : String?
    get() = super.genreDescription
    set(value) {
        val frameData = ID3v2TextFrameData(useFrameUnsynchronisation(),
            value?.let { EncodedText(it) })
        var frameSet: ID3v2FrameSet? = frameSets?.get(AbstractID3v2Tag.ID_GENRE)
        if (frameSet == null) {
            frameSets?.put(
                AbstractID3v2Tag.ID_GENRE,
                ID3v2FrameSet(AbstractID3v2Tag.ID_GENRE).also { frameSet = it })
        }
        frameSet?.clear()
        frameSet?.addFrame(createFrame(AbstractID3v2Tag.ID_GENRE, frameData.toBytes()))
    }

    /*
	 * 'recording time' (TDRC) replaces the deprecated frames 'TDAT - Date', 'TIME - Time',
	 * 'TRDA - Recording dates' and 'TYER - Year' in 4.0
	 */
    var recordingTime: String?
        get() {
            val frameData: ID3v2TextFrameData? = extractTextFrameData(ID_RECTIME)
            return if (frameData != null && frameData.text != null) frameData.text
                .toString() else null
        }
        set(recTime) {
            if (recTime != null && recTime.length > 0) {
                invalidateDataLength()
                val frameData =
                    ID3v2TextFrameData(useFrameUnsynchronisation(), EncodedText(recTime))
                addFrame(createFrame(ID_RECTIME, frameData.toBytes()), true)
            }
        }

    companion object {
        const val VERSION = "4.0"
        const val ID_RECTIME = "TDRC"
    }
}