package com.mpatric.mp3agic

import com.mpatric.mp3agic.MpegFrame
import com.mpatric.mp3agic.BufferTools
import kotlin.Throws

open class MpegFrame {
    var version: String? = null
        private set
    private var layer = 0
    var isProtection = false
        private set
    var bitrate = 0
        private set
    private var sampleRate = 0
    private var padding = false
    var isPrivate = false
        private set
    var channelMode: String? = null
        private set
    var modeExtension: String? = null
        private set
    var isCopyright = false
        private set
    var isOriginal = false
        private set
    var emphasis: String? = null
        private set

    constructor(frameData: ByteArray) {
        if (frameData.size < FRAME_DATA_LENGTH) throw com.mpatric.mp3agic.InvalidDataException("Mpeg frame too short")
        val frameHeader: Long =
            BufferTools.unpackInteger(frameData[0], frameData[1], frameData[2], frameData[3])
                .toLong()
        setFields(frameHeader)
    }

    constructor(frameData1: Byte, frameData2: Byte, frameData3: Byte, frameData4: Byte) {
        val frameHeader: Long =
            BufferTools.unpackInteger(frameData1, frameData2, frameData3, frameData4).toLong()
        setFields(frameHeader)
    }

    protected constructor() {}

    @Throws(com.mpatric.mp3agic.InvalidDataException::class)
    private fun setFields(frameHeader: Long) {
        val frameSync = extractField(frameHeader, BITMASK_FRAME_SYNC).toLong()
        if (frameSync != FRAME_SYNC.toLong()) throw com.mpatric.mp3agic.InvalidDataException("Frame sync missing")
        setVersion(extractField(frameHeader, BITMASK_VERSION))
        setLayer(extractField(frameHeader, BITMASK_LAYER))
        setProtection(extractField(frameHeader, BITMASK_PROTECTION))
        setBitRate(extractField(frameHeader, BITMASK_BITRATE))
        setSampleRate(extractField(frameHeader, BITMASK_SAMPLE_RATE))
        setPadding(extractField(frameHeader, BITMASK_PADDING))
        setPrivate(extractField(frameHeader, BITMASK_PRIVATE))
        setChannelMode(extractField(frameHeader, BITMASK_CHANNEL_MODE))
        setModeExtension(extractField(frameHeader, BITMASK_MODE_EXTENSION))
        setCopyright(extractField(frameHeader, BITMASK_COPYRIGHT))
        setOriginal(extractField(frameHeader, BITMASK_ORIGINAL))
        setEmphasis(extractField(frameHeader, BITMASK_EMPHASIS))
    }

    fun extractField(frameHeader: Long, bitMask: Long): Int {
        var shiftBy = 0
        for (i in 0..31) {
            if (bitMask shr i and 1 != 0L) {
                shiftBy = i
                break
            }
        }
        return (frameHeader shr shiftBy and (bitMask shr shiftBy)).toInt()
    }

    @Throws(com.mpatric.mp3agic.InvalidDataException::class)
    private fun setVersion(version: Int) {
        when (version) {
            0 -> this.version = MPEG_VERSION_2_5
            2 -> this.version = MPEG_VERSION_2_0
            3 -> this.version = MPEG_VERSION_1_0
            else -> throw com.mpatric.mp3agic.InvalidDataException("Invalid mpeg audio version in frame header")
        }
    }

    @Throws(com.mpatric.mp3agic.InvalidDataException::class)
    private fun setLayer(layer: Int) {
        when (layer) {
            1 -> this.layer = 3
            2 -> this.layer = 2
            3 -> this.layer = 1
            else -> throw com.mpatric.mp3agic.InvalidDataException("Invalid mpeg layer description in frame header")
        }
    }

    private fun setProtection(protectionBit: Int) {
        isProtection = protectionBit == 1
    }

    @Throws(com.mpatric.mp3agic.InvalidDataException::class)
    private fun setBitRate(bitrate: Int) {
        if (MPEG_VERSION_1_0 == version) {
            if (layer == 1) {
                when (bitrate) {
                    1 -> {
                        this.bitrate = 32
                        return
                    }
                    2 -> {
                        this.bitrate = 64
                        return
                    }
                    3 -> {
                        this.bitrate = 96
                        return
                    }
                    4 -> {
                        this.bitrate = 128
                        return
                    }
                    5 -> {
                        this.bitrate = 160
                        return
                    }
                    6 -> {
                        this.bitrate = 192
                        return
                    }
                    7 -> {
                        this.bitrate = 224
                        return
                    }
                    8 -> {
                        this.bitrate = 256
                        return
                    }
                    9 -> {
                        this.bitrate = 288
                        return
                    }
                    10 -> {
                        this.bitrate = 320
                        return
                    }
                    11 -> {
                        this.bitrate = 352
                        return
                    }
                    12 -> {
                        this.bitrate = 384
                        return
                    }
                    13 -> {
                        this.bitrate = 416
                        return
                    }
                    14 -> {
                        this.bitrate = 448
                        return
                    }
                }
            } else if (layer == 2) {
                when (bitrate) {
                    1 -> {
                        this.bitrate = 32
                        return
                    }
                    2 -> {
                        this.bitrate = 48
                        return
                    }
                    3 -> {
                        this.bitrate = 56
                        return
                    }
                    4 -> {
                        this.bitrate = 64
                        return
                    }
                    5 -> {
                        this.bitrate = 80
                        return
                    }
                    6 -> {
                        this.bitrate = 96
                        return
                    }
                    7 -> {
                        this.bitrate = 112
                        return
                    }
                    8 -> {
                        this.bitrate = 128
                        return
                    }
                    9 -> {
                        this.bitrate = 160
                        return
                    }
                    10 -> {
                        this.bitrate = 192
                        return
                    }
                    11 -> {
                        this.bitrate = 224
                        return
                    }
                    12 -> {
                        this.bitrate = 256
                        return
                    }
                    13 -> {
                        this.bitrate = 320
                        return
                    }
                    14 -> {
                        this.bitrate = 384
                        return
                    }
                }
            } else if (layer == 3) {
                when (bitrate) {
                    1 -> {
                        this.bitrate = 32
                        return
                    }
                    2 -> {
                        this.bitrate = 40
                        return
                    }
                    3 -> {
                        this.bitrate = 48
                        return
                    }
                    4 -> {
                        this.bitrate = 56
                        return
                    }
                    5 -> {
                        this.bitrate = 64
                        return
                    }
                    6 -> {
                        this.bitrate = 80
                        return
                    }
                    7 -> {
                        this.bitrate = 96
                        return
                    }
                    8 -> {
                        this.bitrate = 112
                        return
                    }
                    9 -> {
                        this.bitrate = 128
                        return
                    }
                    10 -> {
                        this.bitrate = 160
                        return
                    }
                    11 -> {
                        this.bitrate = 192
                        return
                    }
                    12 -> {
                        this.bitrate = 224
                        return
                    }
                    13 -> {
                        this.bitrate = 256
                        return
                    }
                    14 -> {
                        this.bitrate = 320
                        return
                    }
                }
            }
        } else if (MPEG_VERSION_2_0 == version || MPEG_VERSION_2_5 == version) {
            if (layer == 1) {
                when (bitrate) {
                    1 -> {
                        this.bitrate = 32
                        return
                    }
                    2 -> {
                        this.bitrate = 48
                        return
                    }
                    3 -> {
                        this.bitrate = 56
                        return
                    }
                    4 -> {
                        this.bitrate = 64
                        return
                    }
                    5 -> {
                        this.bitrate = 80
                        return
                    }
                    6 -> {
                        this.bitrate = 96
                        return
                    }
                    7 -> {
                        this.bitrate = 112
                        return
                    }
                    8 -> {
                        this.bitrate = 128
                        return
                    }
                    9 -> {
                        this.bitrate = 144
                        return
                    }
                    10 -> {
                        this.bitrate = 160
                        return
                    }
                    11 -> {
                        this.bitrate = 176
                        return
                    }
                    12 -> {
                        this.bitrate = 192
                        return
                    }
                    13 -> {
                        this.bitrate = 224
                        return
                    }
                    14 -> {
                        this.bitrate = 256
                        return
                    }
                }
            } else if (layer == 2 || layer == 3) {
                when (bitrate) {
                    1 -> {
                        this.bitrate = 8
                        return
                    }
                    2 -> {
                        this.bitrate = 16
                        return
                    }
                    3 -> {
                        this.bitrate = 24
                        return
                    }
                    4 -> {
                        this.bitrate = 32
                        return
                    }
                    5 -> {
                        this.bitrate = 40
                        return
                    }
                    6 -> {
                        this.bitrate = 48
                        return
                    }
                    7 -> {
                        this.bitrate = 56
                        return
                    }
                    8 -> {
                        this.bitrate = 64
                        return
                    }
                    9 -> {
                        this.bitrate = 80
                        return
                    }
                    10 -> {
                        this.bitrate = 96
                        return
                    }
                    11 -> {
                        this.bitrate = 112
                        return
                    }
                    12 -> {
                        this.bitrate = 128
                        return
                    }
                    13 -> {
                        this.bitrate = 144
                        return
                    }
                    14 -> {
                        this.bitrate = 160
                        return
                    }
                }
            }
        }
        throw com.mpatric.mp3agic.InvalidDataException("Invalid bitrate in frame header")
    }

    @Throws(com.mpatric.mp3agic.InvalidDataException::class)
    private fun setSampleRate(sampleRate: Int) {
        if (MPEG_VERSION_1_0 == version) {
            when (sampleRate) {
                0 -> {
                    this.sampleRate = 44100
                    return
                }
                1 -> {
                    this.sampleRate = 48000
                    return
                }
                2 -> {
                    this.sampleRate = 32000
                    return
                }
            }
        } else if (MPEG_VERSION_2_0 == version) {
            when (sampleRate) {
                0 -> {
                    this.sampleRate = 22050
                    return
                }
                1 -> {
                    this.sampleRate = 24000
                    return
                }
                2 -> {
                    this.sampleRate = 16000
                    return
                }
            }
        } else if (MPEG_VERSION_2_5 == version) {
            when (sampleRate) {
                0 -> {
                    this.sampleRate = 11025
                    return
                }
                1 -> {
                    this.sampleRate = 12000
                    return
                }
                2 -> {
                    this.sampleRate = 8000
                    return
                }
            }
        }
        throw com.mpatric.mp3agic.InvalidDataException("Invalid sample rate in frame header")
    }

    private fun setPadding(paddingBit: Int) {
        padding = paddingBit == 1
    }

    private fun setPrivate(privateBit: Int) {
        isPrivate = privateBit == 1
    }

    @Throws(com.mpatric.mp3agic.InvalidDataException::class)
    private fun setChannelMode(channelMode: Int) {
        when (channelMode) {
            0 -> this.channelMode = CHANNEL_MODE_STEREO
            1 -> this.channelMode = CHANNEL_MODE_JOINT_STEREO
            2 -> this.channelMode = CHANNEL_MODE_DUAL_MONO
            3 -> this.channelMode = CHANNEL_MODE_MONO
            else -> throw com.mpatric.mp3agic.InvalidDataException("Invalid channel mode in frame header")
        }
    }

    @Throws(com.mpatric.mp3agic.InvalidDataException::class)
    private fun setModeExtension(modeExtension: Int) {
        if (CHANNEL_MODE_JOINT_STEREO != channelMode) {
            this.modeExtension = MODE_EXTENSION_NA
        } else {
            if (layer == 1 || layer == 2) {
                when (modeExtension) {
                    0 -> {
                        this.modeExtension = MODE_EXTENSION_BANDS_4_31
                        return
                    }
                    1 -> {
                        this.modeExtension = MODE_EXTENSION_BANDS_8_31
                        return
                    }
                    2 -> {
                        this.modeExtension = MODE_EXTENSION_BANDS_12_31
                        return
                    }
                    3 -> {
                        this.modeExtension = MODE_EXTENSION_BANDS_16_31
                        return
                    }
                }
            } else if (layer == 3) {
                when (modeExtension) {
                    0 -> {
                        this.modeExtension = MODE_EXTENSION_NONE
                        return
                    }
                    1 -> {
                        this.modeExtension = MODE_EXTENSION_INTENSITY_STEREO
                        return
                    }
                    2 -> {
                        this.modeExtension = MODE_EXTENSION_M_S_STEREO
                        return
                    }
                    3 -> {
                        this.modeExtension = MODE_EXTENSION_INTENSITY_M_S_STEREO
                        return
                    }
                }
            }
            throw com.mpatric.mp3agic.InvalidDataException("Invalid mode extension in frame header")
        }
    }

    private fun setCopyright(copyrightBit: Int) {
        isCopyright = copyrightBit == 1
    }

    private fun setOriginal(originalBit: Int) {
        isOriginal = originalBit == 1
    }

    @Throws(com.mpatric.mp3agic.InvalidDataException::class)
    private fun setEmphasis(emphasis: Int) {
        when (emphasis) {
            0 -> this.emphasis = EMPHASIS_NONE
            1 -> this.emphasis = EMPHASIS__50_15_MS
            3 -> this.emphasis = EMPHASIS_CCITT_J_17
            else -> throw com.mpatric.mp3agic.InvalidDataException("Invalid emphasis in frame header")
        }
    }

    fun getLayer(): String? {
        return MPEG_LAYERS[layer]
    }

    fun hasPadding(): Boolean {
        return padding
    }

    fun getSampleRate(): Int {
        return sampleRate
    }

    val lengthInBytes: Int
        get() {
            val length: Long
            val pad: Int
            pad = if (padding) 1 else 0
            length = if (layer == 1) {
                (48000 * bitrate / sampleRate + pad * 4).toLong()
            } else {
                (144000 * bitrate / sampleRate + pad).toLong()
            }
            return length.toInt()
        }

    companion object {
        const val MPEG_VERSION_1_0 = "1.0"
        const val MPEG_VERSION_2_0 = "2.0"
        const val MPEG_VERSION_2_5 = "2.5"
        const val MPEG_LAYER_1 = "I"
        const val MPEG_LAYER_2 = "II"
        const val MPEG_LAYER_3 = "III"
        val MPEG_LAYERS = arrayOf(null, MPEG_LAYER_1, MPEG_LAYER_2, MPEG_LAYER_3)
        const val CHANNEL_MODE_MONO = "Mono"
        const val CHANNEL_MODE_DUAL_MONO = "Dual mono"
        const val CHANNEL_MODE_JOINT_STEREO = "Joint stereo"
        const val CHANNEL_MODE_STEREO = "Stereo"
        const val MODE_EXTENSION_BANDS_4_31 = "Bands 4-31"
        const val MODE_EXTENSION_BANDS_8_31 = "Bands 8-31"
        const val MODE_EXTENSION_BANDS_12_31 = "Bands 12-31"
        const val MODE_EXTENSION_BANDS_16_31 = "Bands 16-31"
        const val MODE_EXTENSION_NONE = "None"
        const val MODE_EXTENSION_INTENSITY_STEREO = "Intensity stereo"
        const val MODE_EXTENSION_M_S_STEREO = "M/S stereo"
        const val MODE_EXTENSION_INTENSITY_M_S_STEREO = "Intensity & M/S stereo"
        const val MODE_EXTENSION_NA = "n/a"
        const val EMPHASIS_NONE = "None"
        const val EMPHASIS__50_15_MS = "50/15 ms"
        const val EMPHASIS_CCITT_J_17 = "CCITT J.17"
        private const val FRAME_DATA_LENGTH = 4
        private const val FRAME_SYNC = 0x7FF
        private const val BITMASK_FRAME_SYNC = 0xFFE00000L
        private const val BITMASK_VERSION = 0x180000L
        private const val BITMASK_LAYER = 0x60000L
        private const val BITMASK_PROTECTION = 0x10000L
        private const val BITMASK_BITRATE = 0xF000L
        private const val BITMASK_SAMPLE_RATE = 0xC00L
        private const val BITMASK_PADDING = 0x200L
        private const val BITMASK_PRIVATE = 0x100L
        private const val BITMASK_CHANNEL_MODE = 0xC0L
        private const val BITMASK_MODE_EXTENSION = 0x30L
        private const val BITMASK_COPYRIGHT = 0x8L
        private const val BITMASK_ORIGINAL = 0x4L
        private const val BITMASK_EMPHASIS = 0x3L
    }
}