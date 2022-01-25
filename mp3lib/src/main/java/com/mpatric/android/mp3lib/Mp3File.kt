package com.mpatric.mp3agic

import java.io.File
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.SeekableByteChannel
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.*
import kotlin.experimental.and

open class Mp3File : FileWrapper {
    protected var bufferLength = 0
    var xingOffset = -1
        private set
    var startOffset = -1
        private set
    var endOffset = -1
        private set
    var frameCount = 0
        private set
    private val bitrates: MutableMap<Int, MutableInteger> = HashMap()
    var xingBitrate = 0
        private set
    private var bitrate = 0.0
    var channelMode: String? = null
        private set
    var emphasis: String? = null
        private set
    var layer: String? = null
        private set
    var modeExtension: String? = null
        private set
    var sampleRate = 0
        private set
    var isCopyright = false
        private set
    var isOriginal = false
        private set
    var version: String? = null
        private set
    private var id3v1Tag: ID3v1? = null
    private var id3v2Tag: ID3v2? = null
    var customTag: ByteArray? = null
    private var scanFile = false

    protected constructor() {}
    constructor(filename: String?, scanFile: Boolean) : this(
        filename,
        DEFAULT_BUFFER_LENGTH,
        scanFile
    ) {
    }

    @JvmOverloads
    constructor(
        filename: String?,
        bufferLength: Int = DEFAULT_BUFFER_LENGTH,
        scanFile: Boolean = true
    ) : super(filename) {
        init(bufferLength, scanFile)
    }

    @JvmOverloads
    constructor(
        file: File?,
        bufferLength: Int = DEFAULT_BUFFER_LENGTH,
        scanFile: Boolean = true
    ) : super(file) {
        init(bufferLength, scanFile)
    }

    @JvmOverloads
    constructor(
        path: Path?,
        bufferLength: Int = DEFAULT_BUFFER_LENGTH,
        scanFile: Boolean = true
    ) : super(path) {
        init(bufferLength, scanFile)
    }

    @Throws(
        IOException::class,
        UnsupportedTagException::class,
        com.mpatric.mp3agic.InvalidDataException::class
    )
    private fun init(bufferLength: Int, scanFile: Boolean) {
        require(bufferLength >= MINIMUM_BUFFER_LENGTH + 1) { "Buffer too small" }
        this.bufferLength = bufferLength
        this.scanFile = scanFile
        Files.newByteChannel(path, StandardOpenOption.READ).use { seekableByteChannel ->
            initId3v1Tag(seekableByteChannel)
            scanFile(seekableByteChannel)
            if (startOffset < 0) {
                throw com.mpatric.mp3agic.InvalidDataException("No mpegs frames found")
            }
            initId3v2Tag(seekableByteChannel)
            if (scanFile) {
                initCustomTag(seekableByteChannel)
            }
        }
    }

    protected fun preScanFile(seekableByteChannel: SeekableByteChannel): Int {
        val byteBuffer = ByteBuffer.allocate(AbstractID3v2Tag.HEADER_LENGTH)
        try {
            seekableByteChannel.position(0)
            byteBuffer.clear()
            val bytesRead = seekableByteChannel.read(byteBuffer)
            if (bytesRead == AbstractID3v2Tag.HEADER_LENGTH) {
                try {
                    val bytes = byteBuffer.array()
                    ID3v2TagFactory.sanityCheckTag(bytes)
                    return AbstractID3v2Tag.HEADER_LENGTH + BufferTools.unpackSynchsafeInteger(
                        bytes[AbstractID3v2Tag.DATA_LENGTH_OFFSET],
                        bytes[AbstractID3v2Tag.DATA_LENGTH_OFFSET + 1],
                        bytes[AbstractID3v2Tag.DATA_LENGTH_OFFSET + 2],
                        bytes[AbstractID3v2Tag.DATA_LENGTH_OFFSET + 3]
                    )
                } catch (e: NoSuchTagException) {
                    // do nothing
                } catch (e: UnsupportedTagException) {
                }
            }
        } catch (e: IOException) {
            // do nothing
        }
        return 0
    }

    @Throws(IOException::class, com.mpatric.mp3agic.InvalidDataException::class)
    private fun scanFile(seekableByteChannel: SeekableByteChannel) {
        val byteBuffer = ByteBuffer.allocate(bufferLength)
        var fileOffset = preScanFile(seekableByteChannel)
        seekableByteChannel.position(fileOffset.toLong())
        var lastBlock = false
        var lastOffset = fileOffset
        while (!lastBlock) {
            byteBuffer.clear()
            val bytesRead = seekableByteChannel.read(byteBuffer)
            val bytes = byteBuffer.array()
            if (bytesRead < bufferLength) lastBlock = true
            if (bytesRead >= MINIMUM_BUFFER_LENGTH) {
                while (true) {
                    try {
                        var offset = 0
                        if (startOffset < 0) {
                            offset = scanBlockForStart(bytes, bytesRead, fileOffset, offset)
                            if (startOffset >= 0 && !scanFile) {
                                return
                            }
                            lastOffset = startOffset
                        }
                        offset = scanBlock(bytes, bytesRead, fileOffset, offset)
                        fileOffset += offset
                        seekableByteChannel.position(fileOffset.toLong())
                        break
                    } catch (e: com.mpatric.mp3agic.InvalidDataException) {
                        if (frameCount < 2) {
                            startOffset = -1
                            xingOffset = -1
                            frameCount = 0
                            bitrates.clear()
                            lastBlock = false
                            fileOffset = lastOffset + 1
                            if (fileOffset == 0) throw com.mpatric.mp3agic.InvalidDataException(
                                "Valid start of mpeg frames not found",
                                e
                            )
                            seekableByteChannel.position(fileOffset.toLong())
                            break
                        }
                        return
                    }
                }
            }
        }
    }

    private fun scanBlockForStart(
        bytes: ByteArray,
        bytesRead: Int,
        absoluteOffset: Int,
        offset: Int
    ): Int {
        var offset = offset
        while (offset < bytesRead - MINIMUM_BUFFER_LENGTH) {
            if (bytes[offset] == 0xFF.toByte() && bytes[offset + 1] and 0xE0.toByte() == 0xE0.toByte()){
                try {
                    val frame = MpegFrame(
                        bytes[offset],
                        bytes[offset + 1],
                        bytes[offset + 2],
                        bytes[offset + 3]
                    )
                    if (xingOffset < 0 && isXingFrame(bytes, offset)) {
                        xingOffset = absoluteOffset + offset
                        xingBitrate = frame.bitrate
                        offset += frame.lengthInBytes
                    } else {
                        startOffset = absoluteOffset + offset
                        channelMode = frame.channelMode
                        emphasis = frame.emphasis
                        layer = frame.getLayer()
                        modeExtension = frame.modeExtension
                        sampleRate = frame.getSampleRate()
                        version = frame.version
                        isCopyright = frame.isCopyright
                        isOriginal = frame.isOriginal
                        frameCount++
                        addBitrate(frame.bitrate)
                        offset += frame.lengthInBytes
                        return offset
                    }
                } catch (e: com.mpatric.mp3agic.InvalidDataException) {
                    offset++
                }
            } else {
                offset++
            }
        }
        return offset
    }

    @Throws(com.mpatric.mp3agic.InvalidDataException::class)
    private fun scanBlock(bytes: ByteArray, bytesRead: Int, absoluteOffset: Int, offset: Int): Int {
        var offset = offset
        while (offset < bytesRead - MINIMUM_BUFFER_LENGTH) {
            val frame =
                MpegFrame(bytes[offset], bytes[offset + 1], bytes[offset + 2], bytes[offset + 3])
            sanityCheckFrame(frame, absoluteOffset + offset)
            val newEndOffset = absoluteOffset + offset + frame.lengthInBytes - 1
            if (newEndOffset < maxEndOffset()) {
                endOffset = absoluteOffset + offset + frame.lengthInBytes - 1
                frameCount++
                addBitrate(frame.bitrate)
                offset += frame.lengthInBytes
            } else {
                break
            }
        }
        return offset
    }

    private fun maxEndOffset(): Int {
        var maxEndOffset = length.toInt()
        if (hasId3v1Tag()) maxEndOffset -= ID3v1Tag.TAG_LENGTH
        return maxEndOffset
    }

    private fun isXingFrame(bytes: ByteArray, offset: Int): Boolean {
        if (bytes.size >= offset + XING_MARKER_OFFSET_1 + 3) {
            if ("Xing" == BufferTools.byteBufferToStringIgnoringEncodingIssues(
                    bytes,
                    offset + XING_MARKER_OFFSET_1,
                    4
                )
            ) return true
            if ("Info" == BufferTools.byteBufferToStringIgnoringEncodingIssues(
                    bytes,
                    offset + XING_MARKER_OFFSET_1,
                    4
                )
            ) return true
            if (bytes.size >= offset + XING_MARKER_OFFSET_2 + 3) {
                if ("Xing" == BufferTools.byteBufferToStringIgnoringEncodingIssues(
                        bytes,
                        offset + XING_MARKER_OFFSET_2,
                        4
                    )
                ) return true
                if ("Info" == BufferTools.byteBufferToStringIgnoringEncodingIssues(
                        bytes,
                        offset + XING_MARKER_OFFSET_2,
                        4
                    )
                ) return true
                if (bytes.size >= offset + XING_MARKER_OFFSET_3 + 3) {
                    if ("Xing" == BufferTools.byteBufferToStringIgnoringEncodingIssues(
                            bytes,
                            offset + XING_MARKER_OFFSET_3,
                            4
                        )
                    ) return true
                    if ("Info" == BufferTools.byteBufferToStringIgnoringEncodingIssues(
                            bytes,
                            offset + XING_MARKER_OFFSET_3,
                            4
                        )
                    ) return true
                }
            }
        }
        return false
    }

    @Throws(com.mpatric.mp3agic.InvalidDataException::class)
    private fun sanityCheckFrame(frame: MpegFrame, offset: Int) {
        if (sampleRate != frame.getSampleRate()) throw com.mpatric.mp3agic.InvalidDataException("Inconsistent frame header")
        if (layer != frame.getLayer()) throw com.mpatric.mp3agic.InvalidDataException("Inconsistent frame header")
        if (version != frame.version) throw com.mpatric.mp3agic.InvalidDataException("Inconsistent frame header")
        if (offset + frame.lengthInBytes > length) throw com.mpatric.mp3agic.InvalidDataException(
            "Frame would extend beyond end of file"
        )
    }

    private fun addBitrate(bitrate: Int) {
        val count = bitrates[bitrate]
        if (count != null) {
            count.increment()
        } else {
            bitrates[bitrate] = MutableInteger(1)
        }
        this.bitrate = (this.bitrate * (frameCount - 1) + bitrate) / frameCount
    }

    @Throws(IOException::class)
    private fun initId3v1Tag(seekableByteChannel: SeekableByteChannel) {
        val byteBuffer = ByteBuffer.allocate(ID3v1Tag.TAG_LENGTH)
        seekableByteChannel.position(length - ID3v1Tag.TAG_LENGTH)
        byteBuffer.clear()
        val bytesRead = seekableByteChannel.read(byteBuffer)
        if (bytesRead < ID3v1Tag.TAG_LENGTH) throw IOException("Not enough bytes read")
        try {
            id3v1Tag = ID3v1Tag(byteBuffer.array())
        } catch (e: NoSuchTagException) {
            id3v1Tag = null
        }
    }

    @Throws(
        IOException::class,
        UnsupportedTagException::class,
        com.mpatric.mp3agic.InvalidDataException::class
    )
    private fun initId3v2Tag(seekableByteChannel: SeekableByteChannel) {
        if (xingOffset == 0 || startOffset == 0) {
            id3v2Tag = null
        } else {
            val bufferLength: Int
            bufferLength = if (hasXingFrame()) xingOffset else startOffset
            val byteBuffer = ByteBuffer.allocate(bufferLength)
            seekableByteChannel.position(0)
            byteBuffer.clear()
            val bytesRead = seekableByteChannel.read(byteBuffer)
            if (bytesRead < bufferLength) throw IOException("Not enough bytes read")
            id3v2Tag = try {
                ID3v2TagFactory.createTag(byteBuffer.array())
            } catch (e: NoSuchTagException) {
                null
            }
        }
    }

    @Throws(IOException::class)
    private fun initCustomTag(seekableByteChannel: SeekableByteChannel) {
        var bufferLength = (length - (endOffset + 1)).toInt()
        if (hasId3v1Tag()) bufferLength -= ID3v1Tag.TAG_LENGTH
        if (bufferLength <= 0) {
            customTag = null
        } else {
            val byteBuffer = ByteBuffer.allocate(bufferLength)
            seekableByteChannel.position((endOffset + 1).toLong())
            byteBuffer.clear()
            val bytesRead = seekableByteChannel.read(byteBuffer)
            customTag = byteBuffer.array()
            if (bytesRead < bufferLength) throw IOException("Not enough bytes read")
        }
    }

    val lengthInMilliseconds: Long
        get() = ((endOffset - startOffset) * (8.0 / bitrate) + 0.5).toLong()
    val lengthInSeconds: Long
        get() = (lengthInMilliseconds + 500) / 1000
    val isVbr: Boolean
        get() = bitrates.size > 1

    fun getBitrate(): Int {
        return (bitrate + 0.5).toInt()
    }

    fun getBitrates(): Map<Int, MutableInteger> {
        return bitrates
    }

    fun hasXingFrame(): Boolean {
        return xingOffset >= 0
    }

    fun hasId3v1Tag(): Boolean {
        return id3v1Tag != null
    }

    fun getId3v1Tag(): ID3v1? {
        return id3v1Tag
    }

    fun setId3v1Tag(id3v1Tag: ID3v1?) {
        this.id3v1Tag = id3v1Tag
    }

    fun removeId3v1Tag() {
        id3v1Tag = null
    }

    fun hasId3v2Tag(): Boolean {
        return id3v2Tag != null
    }

    fun getId3v2Tag(): ID3v2? {
        return id3v2Tag
    }

    fun setId3v2Tag(id3v2Tag: ID3v2?) {
        this.id3v2Tag = id3v2Tag
    }

    fun removeId3v2Tag() {
        id3v2Tag = null
    }

    fun hasCustomTag(): Boolean {
        return customTag != null
    }

    fun removeCustomTag() {
        customTag = null
    }

    @Throws(IOException::class, NotSupportedException::class)
    fun save(newFilename: String?) {
        require(
            path?.toAbsolutePath()
                ?.compareTo(Paths.get(newFilename).toAbsolutePath()) !=  0
        ) { "Save filename same as source filename" }
        Files.newByteChannel(
            Paths.get(newFilename),
            EnumSet.of(
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING,
                StandardOpenOption.WRITE
            )
        ).use { saveFile ->
            if (hasId3v2Tag()) {
                val byteBuffer = ByteBuffer.wrap(id3v2Tag?.toBytes())
                byteBuffer.rewind()
                saveFile.write(byteBuffer)
            }
            saveMpegFrames(saveFile)
            if (hasCustomTag()) {
                val byteBuffer = ByteBuffer.wrap(customTag)
                byteBuffer.rewind()
                saveFile.write(byteBuffer)
            }
            if (hasId3v1Tag()) {
                val byteBuffer = ByteBuffer.wrap(id3v1Tag?.toBytes())
                byteBuffer.rewind()
                saveFile.write(byteBuffer)
            }
            saveFile.close()
        }
    }

    @Throws(IOException::class)
    private fun saveMpegFrames(saveFile: SeekableByteChannel) {
        var filePos = xingOffset
        if (filePos < 0) filePos = startOffset
        if (filePos < 0) return
        if (endOffset < filePos) return
        val byteBuffer = ByteBuffer.allocate(bufferLength)
        Files.newByteChannel(path, StandardOpenOption.READ).use { seekableByteChannel ->
            seekableByteChannel.position(filePos.toLong())
            while (true) {
                byteBuffer.clear()
                val bytesRead = seekableByteChannel.read(byteBuffer)
                byteBuffer.rewind()
                filePos += if (filePos + bytesRead <= endOffset) {
                    byteBuffer.limit(bytesRead)
                    saveFile.write(byteBuffer)
                    bytesRead
                } else {
                    byteBuffer.limit(endOffset - filePos + 1)
                    saveFile.write(byteBuffer)
                    break
                }
            }
        }
    }

    companion object {
        private const val DEFAULT_BUFFER_LENGTH = 65536
        private const val MINIMUM_BUFFER_LENGTH = 40
        private const val XING_MARKER_OFFSET_1 = 13
        private const val XING_MARKER_OFFSET_2 = 21
        private const val XING_MARKER_OFFSET_3 = 36
    }
}