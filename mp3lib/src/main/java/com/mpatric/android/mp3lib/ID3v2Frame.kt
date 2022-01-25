package com.mpatric.mp3agic

import java.io.UnsupportedEncodingException
import java.util.*

open class ID3v2Frame {
    var id: String = ""
        protected set
    var dataLength = 0
        protected set
    var data: ByteArray = ByteArray(0)
    private var preserveTag = false
    private var preserveFile = false
    var isReadOnly = false
        private set
    private var group = false
    private var compression = false
    private var encryption = false
    private var unsynchronisation = false
    private var dataLengthIndicator = false

    constructor(buffer: ByteArray, offset: Int) {
        unpackFrame(buffer, offset)
    }

    constructor(id: String, data: ByteArray) {
        this.id = id
        this.data = data
        dataLength = data.size
    }

    @Throws(InvalidDataException::class)
    protected fun unpackFrame(buffer: ByteArray, offset: Int) {
        val dataOffset = unpackHeader(buffer, offset)
        sanityCheckUnpackedHeader()
        data = BufferTools.copyBuffer(buffer, dataOffset, dataLength)
    }

    protected open fun unpackHeader(buffer: ByteArray, offset: Int): Int {
        id = BufferTools.byteBufferToStringIgnoringEncodingIssues(
            buffer,
            offset + ID_OFFSET,
            ID_LENGTH
        )
        unpackDataLength(buffer, offset)
        unpackFlags(buffer, offset)
        return offset + HEADER_LENGTH
    }

    protected open fun unpackDataLength(buffer: ByteArray, offset: Int) {
        dataLength = BufferTools.unpackInteger(
            buffer[offset + DATA_LENGTH_OFFSET],
            buffer[offset + DATA_LENGTH_OFFSET + 1],
            buffer[offset + DATA_LENGTH_OFFSET + 2],
            buffer[offset + DATA_LENGTH_OFFSET + 3]
        )
    }

    private fun unpackFlags(buffer: ByteArray, offset: Int) {
        preserveTag = BufferTools.checkBit(buffer[offset + FLAGS1_OFFSET], PRESERVE_TAG_BIT)
        preserveFile = BufferTools.checkBit(buffer[offset + FLAGS1_OFFSET], PRESERVE_FILE_BIT)
        isReadOnly = BufferTools.checkBit(buffer[offset + FLAGS1_OFFSET], READ_ONLY_BIT)
        group = BufferTools.checkBit(buffer[offset + FLAGS2_OFFSET], GROUP_BIT)
        compression = BufferTools.checkBit(buffer[offset + FLAGS2_OFFSET], COMPRESSION_BIT)
        encryption = BufferTools.checkBit(buffer[offset + FLAGS2_OFFSET], ENCRYPTION_BIT)
        unsynchronisation =
            BufferTools.checkBit(buffer[offset + FLAGS2_OFFSET], UNSYNCHRONISATION_BIT)
        dataLengthIndicator =
            BufferTools.checkBit(buffer[offset + FLAGS2_OFFSET], DATA_LENGTH_INDICATOR_BIT)
    }

    @Throws(InvalidDataException::class)
    protected fun sanityCheckUnpackedHeader() {
        for (i in id.indices) {
            if (!(id[i] in 'A'..'Z' || id[i] in '0'..'9')) {
                throw InvalidDataException("Not a valid frame - invalid tag $id")
            }
        }
    }

    @Throws(NotSupportedException::class)
    fun toBytes(): ByteArray {
        val bytes = ByteArray(length)
        packFrame(bytes, 0)
        return bytes
    }

    @Throws(NotSupportedException::class)
    fun toBytes(bytes: ByteArray, offset: Int) {
        packFrame(bytes, offset)
    }

    @Throws(NotSupportedException::class)
    open fun packFrame(bytes: ByteArray, offset: Int) {
        packHeader(bytes)
        BufferTools.copyIntoByteBuffer(data, 0, data.size, bytes, offset + HEADER_LENGTH)
    }

    private fun packHeader(bytes: ByteArray) {
        try {
            BufferTools.stringIntoByteBuffer(id, 0, id.length, bytes, 0)
        } catch (e: UnsupportedEncodingException) {
        }
        BufferTools.copyIntoByteBuffer(packDataLength(), 0, 4, bytes, 4)
        BufferTools.copyIntoByteBuffer(packFlags(), 0, 2, bytes, 8)
    }

    protected open fun packDataLength(): ByteArray {
        return BufferTools.packInteger(dataLength)
    }

    private fun packFlags(): ByteArray {
        val bytes = ByteArray(2)
        bytes[0] = BufferTools.setBit(bytes[0], PRESERVE_TAG_BIT, preserveTag)
        bytes[0] = BufferTools.setBit(bytes[0], PRESERVE_FILE_BIT, preserveFile)
        bytes[0] = BufferTools.setBit(bytes[0], READ_ONLY_BIT, isReadOnly)
        bytes[1] = BufferTools.setBit(bytes[1], GROUP_BIT, group)
        bytes[1] = BufferTools.setBit(bytes[1], COMPRESSION_BIT, compression)
        bytes[1] = BufferTools.setBit(bytes[1], ENCRYPTION_BIT, encryption)
        bytes[1] = BufferTools.setBit(bytes[1], UNSYNCHRONISATION_BIT, unsynchronisation)
        bytes[1] = BufferTools.setBit(bytes[1], DATA_LENGTH_INDICATOR_BIT, dataLengthIndicator)
        return bytes
    }

    open val length: Int
        get() = dataLength + HEADER_LENGTH


    fun hasDataLengthIndicator(): Boolean {
        return dataLengthIndicator
    }

    fun hasCompression(): Boolean {
        return compression
    }

    fun hasEncryption(): Boolean {
        return encryption
    }

    fun hasGroup(): Boolean {
        return group
    }

    fun hasPreserveFile(): Boolean {
        return preserveFile
    }

    fun hasPreserveTag(): Boolean {
        return preserveTag
    }

    fun hasUnsynchronisation(): Boolean {
        return unsynchronisation
    }

    override fun hashCode(): Int {
        return 31 * Objects.hash(
            compression, dataLength, dataLengthIndicator, encryption, group,
            id, preserveFile, preserveTag, isReadOnly, unsynchronisation
        ) + data.contentHashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null) return false
        if (javaClass != other.javaClass) return false
        val other2 = other as ID3v2Frame
        return data.contentEquals(other2.data) &&
                compression == other2.compression &&
                dataLength == other2.dataLength &&
                dataLengthIndicator == other2.dataLengthIndicator &&
                encryption == other2.encryption &&
                group == other2.group &&
                id == other2.id &&
                preserveFile == other2.preserveFile &&
                preserveTag == other2.preserveTag &&
                isReadOnly == other2.isReadOnly &&
                unsynchronisation == other2.unsynchronisation
    }

    companion object {
        private const val HEADER_LENGTH = 10
        private const val ID_OFFSET = 0
        private const val ID_LENGTH = 4
        const val DATA_LENGTH_OFFSET = 4
        private const val FLAGS1_OFFSET = 8
        private const val FLAGS2_OFFSET = 9
        private const val PRESERVE_TAG_BIT = 6
        private const val PRESERVE_FILE_BIT = 5
        private const val READ_ONLY_BIT = 4
        private const val GROUP_BIT = 6
        private const val COMPRESSION_BIT = 3
        private const val ENCRYPTION_BIT = 2
        private const val UNSYNCHRONISATION_BIT = 1
        private const val DATA_LENGTH_INDICATOR_BIT = 0
    }
}