package com.mpatric.mp3agic

import com.mpatric.mp3agic.BufferTools.byteBufferToStringIgnoringEncodingIssues
import com.mpatric.mp3agic.BufferTools.copyBuffer
import com.mpatric.mp3agic.BufferTools.copyIntoByteBuffer
import com.mpatric.mp3agic.BufferTools.packSynchsafeInteger
import com.mpatric.mp3agic.BufferTools.stringIntoByteBuffer
import com.mpatric.mp3agic.BufferTools.unpackSynchsafeInteger
import com.mpatric.mp3agic.ID3v1Genres.matchGenreDescription
import com.mpatric.mp3agic.ID3v2TagFactory.sanityCheckTag
import java.io.UnsupportedEncodingException
import java.util.*
import kotlin.experimental.and

abstract class AbstractID3v2Tag : ID3v2 {
    override var unsynchronisation = false
    protected var extendedHeader = false
    protected var experimental = false
    override var footer = false
    protected var compression = false
    protected var _padding = false
    override fun getPadding() = _padding
    override fun setPadding(padding: Boolean){
        _padding = padding
    }
    override var version: String? = null
        protected set
    override var dataLength = 0
    private var extendedHeaderLength = 0
    private lateinit var extendedHeaderData: ByteArray
    final override var obsoleteFormat = false
    final override val frameSets: MutableMap<String?, ID3v2FrameSet?>?

    constructor() {
        frameSets = TreeMap()
    }

    @JvmOverloads
    constructor(bytes: ByteArray, obseleteFormat: Boolean = false) {
        frameSets = TreeMap()
        this.obsoleteFormat = obseleteFormat
        unpackTag(bytes)
    }

    @Throws(NoSuchTagException::class, UnsupportedTagException::class, InvalidDataException::class)
    private fun unpackTag(bytes: ByteArray) {
        sanityCheckTag(bytes)
        var offset = unpackHeader(bytes)
        try {
            if (extendedHeader) {
                offset = unpackExtendedHeader(bytes, offset)
            }
            var framesLength = dataLength
            if (footer) framesLength -= 10
            offset = unpackFrames(bytes, offset, framesLength)
            if (footer) {
                offset = unpackFooter(bytes, dataLength)
            }
        } catch (e: ArrayIndexOutOfBoundsException) {
            throw InvalidDataException("Premature end of tag", e)
        }
    }

    @Throws(UnsupportedTagException::class, InvalidDataException::class)
    private fun unpackHeader(bytes: ByteArray): Int {
        val majorVersion = bytes[MAJOR_VERSION_OFFSET]
            .toInt()
        val minorVersion = bytes[MINOR_VERSION_OFFSET]
            .toInt()
        version = "$majorVersion.$minorVersion"
        if (majorVersion != 2 && majorVersion != 3 && majorVersion != 4) {
            throw UnsupportedTagException("Unsupported version $version")
        }
        unpackFlags(bytes)
        if (bytes[FLAGS_OFFSET] and 0x0F != 0.toByte()) throw UnsupportedTagException("Unrecognised bits in header")
        dataLength = unpackSynchsafeInteger(
            bytes[DATA_LENGTH_OFFSET],
            bytes[DATA_LENGTH_OFFSET + 1],
            bytes[DATA_LENGTH_OFFSET + 2],
            bytes[DATA_LENGTH_OFFSET + 3]
        )
        if (dataLength < 1) throw InvalidDataException("Zero size tag")
        return HEADER_LENGTH
    }

    protected abstract fun unpackFlags(bytes: ByteArray)
    private fun unpackExtendedHeader(bytes: ByteArray, offset: Int): Int {
        extendedHeaderLength = unpackSynchsafeInteger(
            bytes[offset],
            bytes[offset + 1],
            bytes[offset + 2],
            bytes[offset + 3]
        ) + 4
        extendedHeaderData = copyBuffer(bytes, offset + 4, extendedHeaderLength)
        return extendedHeaderLength
    }

    protected open fun unpackFrames(bytes: ByteArray, offset: Int, framesLength: Int): Int {
        var currentOffset = offset
        while (currentOffset <= framesLength) {
            var frame: ID3v2Frame
            try {
                frame = createFrame(bytes, currentOffset)
                addFrame(frame, false)
                currentOffset += frame.length
            } catch (e: InvalidDataException) {
                break
            }
        }
        return currentOffset
    }

    protected fun addFrame(frame: ID3v2Frame, replace: Boolean) {
        var frameSet = frameSets!![frame.id]
        if (frameSet == null) {
            frameSet = ID3v2FrameSet(frame.id)
            frameSet.addFrame(frame)
            frameSets!![frame.id] = frameSet
        } else if (replace) {
            frameSet.clear()
            frameSet.addFrame(frame)
        } else {
            frameSet.addFrame(frame)
        }
    }

    @Throws(InvalidDataException::class)
    protected open fun createFrame(bytes: ByteArray, currentOffset: Int): ID3v2Frame {
        return if (obsoleteFormat) ID3v2ObseleteFrame(bytes, currentOffset)
        else ID3v2Frame(bytes, currentOffset)
    }

    protected open fun createFrame(id: String, data: ByteArray): ID3v2Frame {
        return if (obsoleteFormat) ID3v2ObseleteFrame(id, data) else ID3v2Frame(id, data)
    }

    @Throws(InvalidDataException::class)
    private fun unpackFooter(bytes: ByteArray, offset: Int): Int {
        if (FOOTER_TAG != byteBufferToStringIgnoringEncodingIssues(
                bytes,
                offset,
                FOOTER_TAG.length
            )
        ) {
            throw InvalidDataException("Invalid footer")
        }
        return FOOTER_LENGTH
    }

    @Throws(NotSupportedException::class)
    override fun toBytes(): ByteArray? {
        val bytes = ByteArray(length)
        packTag(bytes)
        return bytes
    }

    @Throws(NotSupportedException::class)
    fun packTag(bytes: ByteArray) {
        var offset = packHeader(bytes, 0)
        if (extendedHeader) {
            offset = packExtendedHeader(bytes, offset)
        }
        offset = packFrames(bytes, offset)
        if (footer) {
            offset = packFooter(bytes, dataLength)
        }
    }

    private fun packHeader(bytes: ByteArray, offset: Int): Int {
        try {
            stringIntoByteBuffer(TAG, 0, TAG.length, bytes, offset)
        } catch (e: UnsupportedEncodingException) {
        }
        val s = version!!.split(".").toTypedArray()
        if (s.size > 0) {
            val majorVersion = s[0].toByte()
            bytes[offset + MAJOR_VERSION_OFFSET] = majorVersion
        }
        if (s.size > 1) {
            val minorVersion = s[1].toByte()
            bytes[offset + MINOR_VERSION_OFFSET] = minorVersion
        }
        packFlags(bytes, offset)
        packSynchsafeInteger(dataLength, bytes, offset + DATA_LENGTH_OFFSET)
        return offset + HEADER_LENGTH
    }

    protected abstract fun packFlags(bytes: ByteArray, i: Int)
    private fun packExtendedHeader(bytes: ByteArray, offset: Int): Int {
        packSynchsafeInteger(extendedHeaderLength, bytes, offset)
        copyIntoByteBuffer(extendedHeaderData, 0, extendedHeaderData.size, bytes, offset + 4)
        return offset + 4 + extendedHeaderData.size
    }

    @Throws(NotSupportedException::class)
    fun packFrames(bytes: ByteArray, offset: Int): Int {
        var newOffset = packSpecifiedFrames(bytes, offset, null, "APIC")
        newOffset = packSpecifiedFrames(bytes, newOffset, "APIC", null)
        return newOffset
    }

    @Throws(NotSupportedException::class)
    private fun packSpecifiedFrames(
        bytes: ByteArray,
        offset: Int,
        onlyId: String?,
        notId: String?
    ): Int {
        var offset = offset
        for (frameSet in frameSets!!.values) {
            if ((onlyId == null || onlyId == frameSet!!.id) && (notId == null || notId != frameSet!!.id)) {
                for (frame in frameSet!!.frames!!) {
                    if (frame.dataLength > 0) {
                        val frameData = frame.toBytes()
                        copyIntoByteBuffer(frameData, 0, frameData.size, bytes, offset)
                        offset += frameData.size
                    }
                }
            }
        }
        return offset
    }

    private fun packFooter(bytes: ByteArray, offset: Int): Int {
        try {
            stringIntoByteBuffer(FOOTER_TAG, 0, FOOTER_TAG.length, bytes, offset)
        } catch (e: UnsupportedEncodingException) {
        }
        val s = version!!.split(".").toTypedArray()
        if (s.size > 0) {
            val majorVersion = s[0].toByte()
            bytes[offset + MAJOR_VERSION_OFFSET] = majorVersion
        }
        if (s.size > 1) {
            val minorVersion = s[1].toByte()
            bytes[offset + MINOR_VERSION_OFFSET] = minorVersion
        }
        packFlags(bytes, offset)
        packSynchsafeInteger(length, bytes, offset + DATA_LENGTH_OFFSET)
        return offset + FOOTER_LENGTH
    }

    private fun calculateDataLength(): Int {
        var length = 0
        if (extendedHeader) length += extendedHeaderLength
        if (footer) length += FOOTER_LENGTH else if (getPadding()) length += PADDING_LENGTH
        for (frameSet in frameSets!!.values) {
            for (frame in frameSet!!.frames) {
                length += frame.length
            }
        }
        return length
    }

    protected open fun useFrameUnsynchronisation(): Boolean {
        return false
    }

    protected fun invalidateDataLength() {
        dataLength = 0
    }


    override val length: Int
        get() = myDataLength() + HEADER_LENGTH

    private fun myDataLength(): Int {
        if (dataLength == 0)
        {
            dataLength = calculateDataLength()
        }
        return dataLength
    }



 /*   override fun setPadding(padding: Boolean) {
        if (this.padding != padding) {
            invalidateDataLength()
            this.padding = padding
        }
    }
*/

    override var track: String?
        get() {
            val frameData =
                extractTextFrameData(if (obsoleteFormat) ID_TRACK_OBSELETE else ID_TRACK)
            return if (frameData != null && frameData.text != null) frameData.text
                .toString() else null
        }
        set(track) {
            if (track != null && track.length > 0) {
                invalidateDataLength()
                val frameData = ID3v2TextFrameData(useFrameUnsynchronisation(), EncodedText(track))
                addFrame(createFrame(ID_TRACK, frameData.toBytes()), true)
            }
        }
    override var partOfSet: String?
        get() {
            val frameData =
                extractTextFrameData(if (obsoleteFormat) ID_PART_OF_SET_OBSELETE else ID_PART_OF_SET)
            return if (frameData != null && frameData.text != null) frameData.text
                .toString() else null
        }
        set(partOfSet) {
            if (partOfSet != null && partOfSet.length > 0) {
                invalidateDataLength()
                val frameData =
                    ID3v2TextFrameData(useFrameUnsynchronisation(), EncodedText(partOfSet))
                addFrame(createFrame(ID_PART_OF_SET, frameData.toBytes()), true)
            }
        }

    // unofficial frame used by iTunes
    override var isCompilation: Boolean
        get() {
            // unofficial frame used by iTunes
            val frameData =
                extractTextFrameData(if (obsoleteFormat) ID_COMPILATION_OBSELETE else ID_COMPILATION)
            return if (frameData != null && frameData.text != null) "1" == frameData.text
                .toString() else false
        }
        set(compilation) {
            invalidateDataLength()
            val frameData = ID3v2TextFrameData(
                useFrameUnsynchronisation(),
                EncodedText(if (compilation) "1" else "0")
            )
            addFrame(createFrame(ID_COMPILATION, frameData.toBytes()), true)
        }
    override var grouping: String?
        get() {
            val frameData =
                extractTextFrameData(if (obsoleteFormat) ID_GROUPING_OBSELETE else ID_GROUPING)
            return if (frameData != null && frameData.text != null) frameData.text
                .toString() else null
        }
        set(grouping) {
            if (grouping != null && grouping.length > 0) {
                invalidateDataLength()
                val frameData =
                    ID3v2TextFrameData(useFrameUnsynchronisation(), EncodedText(grouping))
                addFrame(createFrame(ID_GROUPING, frameData.toBytes()), true)
            }
        }
    override var artist: String?
        get() {
            val frameData =
                extractTextFrameData(if (obsoleteFormat) ID_ARTIST_OBSELETE else ID_ARTIST)
            return if (frameData != null && frameData.text != null) frameData.text
                .toString() else null
        }
        set(artist) {
            if (artist != null && artist.length > 0) {
                invalidateDataLength()
                val frameData = ID3v2TextFrameData(useFrameUnsynchronisation(), EncodedText(artist))
                addFrame(createFrame(ID_ARTIST, frameData.toBytes()), true)
            }
        }
    override var albumArtist: String?
        get() {
            val frameData =
                extractTextFrameData(if (obsoleteFormat) ID_ALBUM_ARTIST_OBSELETE else ID_ALBUM_ARTIST)
            return if (frameData != null && frameData.text != null) frameData.text
                .toString() else null
        }
        set(albumArtist) {
            if (albumArtist != null && albumArtist.length > 0) {
                invalidateDataLength()
                val frameData =
                    ID3v2TextFrameData(useFrameUnsynchronisation(), EncodedText(albumArtist))
                addFrame(createFrame(ID_ALBUM_ARTIST, frameData.toBytes()), true)
            }
        }
    override var title: String?
        get() {
            val frameData =
                extractTextFrameData(if (obsoleteFormat) ID_TITLE_OBSELETE else ID_TITLE)
            return if (frameData != null && frameData.text != null) frameData.text
                .toString() else null
        }
        set(title) {
            if (title != null && title.length > 0) {
                invalidateDataLength()
                val frameData = ID3v2TextFrameData(useFrameUnsynchronisation(), EncodedText(title))
                addFrame(createFrame(ID_TITLE, frameData.toBytes()), true)
            }
        }
    override var album: String?
        get() {
            val frameData =
                extractTextFrameData(if (obsoleteFormat) ID_ALBUM_OBSELETE else ID_ALBUM)
            return if (frameData != null && frameData.text != null) frameData.text
                .toString() else null
        }
        set(album) {
            if (album != null && album.length > 0) {
                invalidateDataLength()
                val frameData = ID3v2TextFrameData(useFrameUnsynchronisation(), EncodedText(album))
                addFrame(createFrame(ID_ALBUM, frameData.toBytes()), true)
            }
        }
    override var year: String?
        get() {
            val frameData = extractTextFrameData(if (obsoleteFormat) ID_YEAR_OBSELETE else ID_YEAR)
            return if (frameData != null && frameData.text != null) frameData.text
                .toString() else null
        }
        set(year) {
            if (year != null && year.length > 0) {
                invalidateDataLength()
                val frameData = ID3v2TextFrameData(useFrameUnsynchronisation(), EncodedText(year))
                addFrame(createFrame(ID_YEAR, frameData.toBytes()), true)
            }
        }
    override var date: String?
        get() {
            val frameData = extractTextFrameData(if (obsoleteFormat) ID_DATE_OBSELETE else ID_DATE)
            return if (frameData != null && frameData.text != null) frameData.text
                .toString() else null
        }
        set(date) {
            if (date != null && date.length > 0) {
                invalidateDataLength()
                val frameData = ID3v2TextFrameData(useFrameUnsynchronisation(), EncodedText(date))
                addFrame(createFrame(ID_DATE, frameData.toBytes()), true)
            }
        }

    private fun getGenre(text: String?): Int {
        return if (text != null && text.length > 0) {
            try {
                extractGenreNumber(text)
            } catch (e: NumberFormatException) { // match genre description
                val description = extractGenreDescription(text)
                matchGenreDescription(description)
            }
        } else -1
    }

    // TODO remove frame?
    override var genre: Int
        get() {
            val frameData =
                extractTextFrameData(if (obsoleteFormat) ID_GENRE_OBSELETE else ID_GENRE)
            return if (frameData == null || frameData.text == null) {
                -1
            } else getGenre(frameData.text.toString())
        }
        set(genre) {
            if (genre >= 0) {
                invalidateDataLength()
                val genreDescription =
                    if (genre < ID3v1Genres.GENRES.size) ID3v1Genres.GENRES[genre] else ""
                val combinedGenre = "(" + Integer.toString(genre) + ")" + genreDescription
                val frameData =
                    ID3v2TextFrameData(useFrameUnsynchronisation(), EncodedText(combinedGenre))
                addFrame(createFrame(ID_GENRE, frameData.toBytes()), true)
            } else {
                // TODO remove frame?
            }
        }

    // try float as some utilities add BPM like 67.8, or 67,8
    override var bPM: Int
        get() {
            val frameData = extractTextFrameData(if (obsoleteFormat) ID_BPM_OBSELETE else ID_BPM)
            if (frameData == null || frameData.text == null) {
                return -1
            }
            val bpmStr = frameData.text.toString()
            return try {
                bpmStr.toInt()
            } catch (e: NumberFormatException) {
                // try float as some utilities add BPM like 67.8, or 67,8
                bpmStr.trim { it <= ' ' }.replace(",".toRegex(), ".").toFloat().toInt()
            }
        }
        set(bpm) {
            if (bpm >= 0) {
                invalidateDataLength()
                val frameData = ID3v2TextFrameData(
                    useFrameUnsynchronisation(),
                    EncodedText(Integer.toString(bpm))
                )
                addFrame(createFrame(ID_BPM, frameData.toBytes()), true)
            }
        }
    override var key: String?
        get() {
            val frameData = extractTextFrameData(if (obsoleteFormat) ID_KEY_OBSELETE else ID_KEY)
            return if (frameData == null || frameData.text == null) {
                null
            } else frameData.text.toString()
        }
        set(key) {
            if (key != null && key.length > 0) {
                invalidateDataLength()
                val frameData = ID3v2TextFrameData(useFrameUnsynchronisation(), EncodedText(key))
                addFrame(createFrame(ID_KEY, frameData.toBytes()), true)
            }
        }

    override var genreDescription: String?
        get() {
            val frameData = extractTextFrameData(if (obsoleteFormat) ID_GENRE_OBSELETE else ID_GENRE)
            if (frameData == null || frameData.text == null) {
                return null
            }
            val text = frameData.text.toString()
            if (text != null) {
                val genreNum = getGenre(text)
                if (genreNum >= 0 && genreNum < ID3v1Genres.GENRES.size) {
                    return ID3v1Genres.GENRES[genreNum]
                } else {
                    val description = extractGenreDescription(text)
                    if (description != null && description.length > 0) {
                        return description
                    }
                }
            }
            return null
        }
        set(value) {
            val genreNum = matchGenreDescription(value)
            require(genreNum >= 0) { "Unknown genre: $value" }
            genre = genreNum
        }

    @Throws(NumberFormatException::class) fun extractGenreNumber(genreValue: String): Int {
        val value = genreValue.trim { it <= ' ' }
        if (value.length > 0) {
            if (value[0] == '(') {
                val pos = value.indexOf(')')
                if (pos > 0) {
                    return value.substring(1, pos).toInt()
                }
            }
        }
        return value.toInt()
    }

    @Throws(NumberFormatException::class) fun extractGenreDescription(genreValue: String): String? {
        val value = genreValue.trim { it <= ' ' }
        if (value.length > 0) {
            if (value[0] == '(') {
                val pos = value.indexOf(')')
                if (pos > 0) {
                    return value.substring(pos + 1)
                }
            }
            return value
        }
        return null
    }

    override var comment: String?
        get() {
            val frameData = extractCommentFrameData(
                if (obsoleteFormat) ID_COMMENT_OBSELETE else ID_COMMENT,
                false
            )
            return if (frameData != null && frameData.getComment() != null) frameData.getComment()
                .toString() else null
        }
        set(comment) {
            if (comment != null && comment.length > 0) {
                invalidateDataLength()
                val frameData = ID3v2CommentFrameData(
                    useFrameUnsynchronisation(),
                    "eng",
                    null,
                    EncodedText(comment)
                )
                addFrame(createFrame(ID_COMMENT, frameData.toBytes()), true)
            }
        }
    override var itunesComment: String?
        get() {
            val frameData = extractCommentFrameData(
                if (obsoleteFormat) ID_COMMENT_OBSELETE else ID_COMMENT,
                true
            )
            return if (frameData != null && frameData.getComment() != null) frameData.getComment()
                .toString() else null
        }
        set(itunesComment) {
            if (itunesComment != null && itunesComment.length > 0) {
                invalidateDataLength()
                val frameData = ID3v2CommentFrameData(
                    useFrameUnsynchronisation(), "eng", EncodedText(
                        ITUNES_COMMENT_DESCRIPTION
                    ), EncodedText(itunesComment)
                )
                addFrame(createFrame(ID_COMMENT, frameData.toBytes()), true)
            }
        }

    protected fun extractLyricsFrameData(id: String?): ID3v2CommentFrameData? {
        val frameSet = frameSets!![id]
        if (frameSet != null) {
            for (frame in frameSet.frames!!) {
                var frameData: ID3v2CommentFrameData
                try {
                    frameData = ID3v2CommentFrameData(useFrameUnsynchronisation(), frame.data)
                    return frameData
                } catch (e: InvalidDataException) {
                    // Do nothing
                }
            }
        }
        return null
    }

    override var lyrics: String?
        get() {
            val frameData: ID3v2CommentFrameData?
            frameData =
                if (obsoleteFormat) return null else extractLyricsFrameData(ID_TEXT_LYRICS)
            return frameData?.getComment()?.toString()
        }
        set(lyrics) {
            if (lyrics != null && lyrics.length > 0) {
                invalidateDataLength()
                val frameData = ID3v2CommentFrameData(
                    useFrameUnsynchronisation(),
                    "eng",
                    null,
                    EncodedText(lyrics)
                )
                addFrame(createFrame(ID_TEXT_LYRICS, frameData.toBytes()), true)
            }
        }
    override var composer: String?
        get() {
            val frameData =
                extractTextFrameData(if (obsoleteFormat) ID_COMPOSER_OBSELETE else ID_COMPOSER)
            return if (frameData != null && frameData.text != null) frameData.text
                .toString() else null
        }
        set(composer) {
            if (composer != null && composer.length > 0) {
                invalidateDataLength()
                val frameData =
                    ID3v2TextFrameData(useFrameUnsynchronisation(), EncodedText(composer))
                addFrame(createFrame(ID_COMPOSER, frameData.toBytes()), true)
            }
        }
    override var publisher: String?
        get() {
            val frameData =
                extractTextFrameData(if (obsoleteFormat) ID_PUBLISHER_OBSELETE else ID_PUBLISHER)
            return if (frameData != null && frameData.text != null) frameData.text
                .toString() else null
        }
        set(publisher) {
            if (publisher != null && publisher.length > 0) {
                invalidateDataLength()
                val frameData =
                    ID3v2TextFrameData(useFrameUnsynchronisation(), EncodedText(publisher))
                addFrame(createFrame(ID_PUBLISHER, frameData.toBytes()), true)
            }
        }
    override var originalArtist: String?
        get() {
            val frameData =
                extractTextFrameData(if (obsoleteFormat) ID_ORIGINAL_ARTIST_OBSELETE else ID_ORIGINAL_ARTIST)
            return if (frameData != null && frameData.text != null) frameData.text
                .toString() else null
        }
        set(originalArtist) {
            if (originalArtist != null && originalArtist.length > 0) {
                invalidateDataLength()
                val frameData =
                    ID3v2TextFrameData(useFrameUnsynchronisation(), EncodedText(originalArtist))
                addFrame(createFrame(ID_ORIGINAL_ARTIST, frameData.toBytes()), true)
            }
        }
    override var copyright: String?
        get() {
            val frameData =
                extractTextFrameData(if (obsoleteFormat) ID_COPYRIGHT_OBSELETE else ID_COPYRIGHT)
            return if (frameData != null && frameData.text != null) frameData.text
                .toString() else null
        }
        set(copyright) {
            if (copyright != null && copyright.length > 0) {
                invalidateDataLength()
                val frameData =
                    ID3v2TextFrameData(useFrameUnsynchronisation(), EncodedText(copyright))
                addFrame(createFrame(ID_COPYRIGHT, frameData.toBytes()), true)
            }
        }
    override var artistUrl: String?
        get() {
            val frameData = extractWWWFrameData(ID_ARTIST_URL)
            return frameData?.url
        }
        set(url) {
            if (url != null && url.length > 0) {
                invalidateDataLength()
                val frameData = ID3v2WWWFrameData(useFrameUnsynchronisation(), url)
                addFrame(createFrame(ID_ARTIST_URL, frameData.toBytes()), true)
            }
        }
    override var commercialUrl: String?
        get() {
            val frameData = extractWWWFrameData(ID_COMMERCIAL_URL)
            return frameData?.url
        }
        set(url) {
            if (url != null && url.length > 0) {
                invalidateDataLength()
                val frameData = ID3v2WWWFrameData(useFrameUnsynchronisation(), url)
                addFrame(createFrame(ID_COMMERCIAL_URL, frameData.toBytes()), true)
            }
        }
    override var copyrightUrl: String?
        get() {
            val frameData = extractWWWFrameData(ID_COPYRIGHT_URL)
            return frameData?.url
        }
        set(url) {
            if (url != null && url.length > 0) {
                invalidateDataLength()
                val frameData = ID3v2WWWFrameData(useFrameUnsynchronisation(), url)
                addFrame(createFrame(ID_COPYRIGHT_URL, frameData.toBytes()), true)
            }
        }
    override var audiofileUrl: String?
        get() {
            val frameData = extractWWWFrameData(ID_AUDIOFILE_URL)
            return frameData?.url
        }
        set(url) {
            if (url != null && url.length > 0) {
                invalidateDataLength()
                val frameData = ID3v2WWWFrameData(useFrameUnsynchronisation(), url)
                addFrame(createFrame(ID_AUDIOFILE_URL, frameData.toBytes()), true)
            }
        }
    override var audioSourceUrl: String?
        get() {
            val frameData = extractWWWFrameData(ID_AUDIOSOURCE_URL)
            return frameData?.url
        }
        set(url) {
            if (url != null && url.length > 0) {
                invalidateDataLength()
                val frameData = ID3v2WWWFrameData(useFrameUnsynchronisation(), url)
                addFrame(createFrame(ID_AUDIOSOURCE_URL, frameData.toBytes()), true)
            }
        }
    override var radiostationUrl: String?
        get() {
            val frameData = extractWWWFrameData(ID_RADIOSTATION_URL)
            return frameData?.url
        }
        set(url) {
            if (url != null && url.length > 0) {
                invalidateDataLength()
                val frameData = ID3v2WWWFrameData(useFrameUnsynchronisation(), url)
                addFrame(createFrame(ID_RADIOSTATION_URL, frameData.toBytes()), true)
            }
        }
    override var paymentUrl: String?
        get() {
            val frameData = extractWWWFrameData(ID_PAYMENT_URL)
            return frameData?.url
        }
        set(url) {
            if (url != null && url.length > 0) {
                invalidateDataLength()
                val frameData = ID3v2WWWFrameData(useFrameUnsynchronisation(), url)
                addFrame(createFrame(ID_PAYMENT_URL, frameData.toBytes()), true)
            }
        }
    override var publisherUrl: String?
        get() {
            val frameData = extractWWWFrameData(ID_PUBLISHER_URL)
            return frameData?.url
        }
        set(url) {
            if (url != null && url.length > 0) {
                invalidateDataLength()
                val frameData = ID3v2WWWFrameData(useFrameUnsynchronisation(), url)
                addFrame(createFrame(ID_PUBLISHER_URL, frameData.toBytes()), true)
            }
        }
    override var url: String?
        get() {
            val frameData = extractUrlFrameData(if (obsoleteFormat) ID_URL_OBSELETE else ID_URL)
            return frameData?.url
        }
        set(url) {
            if (url != null && url.length > 0) {
                invalidateDataLength()
                val frameData = ID3v2UrlFrameData(useFrameUnsynchronisation(), null, url)
                addFrame(createFrame(ID_URL, frameData.toBytes()), true)
            }
        }
    override var chapters: ArrayList<ID3v2ChapterFrameData>?
        get() = if (obsoleteFormat) {
            null
        } else extractChapterFrameData(ID_CHAPTER)
        set(chapters) {
            if (chapters != null) {
                invalidateDataLength()
                var first = true
                for (chapter in chapters) {
                    if (first) {
                        first = false
                        addFrame(createFrame(ID_CHAPTER, chapter.toBytes()), true)
                    } else {
                        addFrame(createFrame(ID_CHAPTER, chapter.toBytes()), false)
                    }
                }
            }
        }
    override var chapterTOC: ArrayList<ID3v2ChapterTOCFrameData>?
        get() = if (obsoleteFormat) {
            null
        } else extractChapterTOCFrameData(ID_CHAPTER_TOC)
        set(toc) {
            if (toc != null) {
                invalidateDataLength()
                var first = true
                for (ct in toc) {
                    if (first) {
                        first = false
                        addFrame(createFrame(ID_CHAPTER_TOC, ct.toBytes()), true)
                    } else {
                        addFrame(createFrame(ID_CHAPTER_TOC, ct.toBytes()), false)
                    }
                }
            }
        }
    override var encoder: String?
        get() {
            val frameData =
                extractTextFrameData(if (obsoleteFormat) ID_ENCODER_OBSELETE else ID_ENCODER)
            return if (frameData != null && frameData.text != null) frameData.text
                .toString() else null
        }
        set(encoder) {
            if (encoder != null && encoder.length > 0) {
                invalidateDataLength()
                val frameData =
                    ID3v2TextFrameData(useFrameUnsynchronisation(), EncodedText(encoder))
                addFrame(createFrame(ID_ENCODER, frameData.toBytes()), true)
            }
        }
    override val albumImage: ByteArray?
        get() {
            val frameData =
                createPictureFrameData(if (obsoleteFormat) ID_IMAGE_OBSELETE else ID_IMAGE)
            return frameData?.imageData
        }

    override fun setAlbumImage(albumImage: ByteArray?, mimeType: String?) {
        setAlbumImage(albumImage, mimeType, 0.toByte(), null)
    }

    override fun setAlbumImage(
        albumImage: ByteArray?,
        mimeType: String?,
        imageType: Byte,
        imageDescription: String?
    ) {
        if (albumImage != null && albumImage.size > 0 && mimeType != null && mimeType.length > 0) {
            invalidateDataLength()
            val frameData = ID3v2PictureFrameData(
                useFrameUnsynchronisation(), mimeType, imageType,
                imageDescription?.let { EncodedText(it) },
                albumImage
            )
            addFrame(createFrame(ID_IMAGE, frameData.toBytes()), true)
        }
    }

    override fun clearAlbumImage() {
        clearFrameSet(if (obsoleteFormat) ID_IMAGE_OBSELETE else ID_IMAGE)
    }

    override val albumImageMimeType: String?
        get() {
            val frameData =
                createPictureFrameData(if (obsoleteFormat) ID_IMAGE_OBSELETE else ID_IMAGE)
            return if (frameData != null && frameData.mimeType != null) frameData.mimeType else null
        }

    override fun clearFrameSet(id: String?) {
        if (frameSets!!.remove(id) != null) {
            invalidateDataLength()
        }
    }

    override var wmpRating: Int
        get() {
            val frameData = extractPopmFrameData(ID_RATING)
            return if (frameData != null && frameData.address != null) {
                frameData.rating
            } else -1
        }
        set(rating) {
            if (rating >= 0 && rating < 6) {
                invalidateDataLength()
                val frameData = ID3v2PopmFrameData(useFrameUnsynchronisation(), rating)
                val bytes: ByteArray = frameData.toBytes()
                addFrame(createFrame(ID_RATING, bytes), true)
            }
        }

    private fun extractChapterFrameData(id: String): ArrayList<ID3v2ChapterFrameData>? {
        val frameSet = frameSets!![id]
        if (frameSet != null) {
            val chapterData = ArrayList<ID3v2ChapterFrameData>()
            val frames = frameSet.frames
            for (frame in frames!!) {
                var frameData: ID3v2ChapterFrameData
                try {
                    frameData = ID3v2ChapterFrameData(
                        useFrameUnsynchronisation(),
                        frame.data
                    )
                    chapterData.add(frameData)
                } catch (e: InvalidDataException) {
                    // do nothing
                }
            }
            return chapterData
        }
        return null
    }

    private fun extractChapterTOCFrameData(id: String): ArrayList<ID3v2ChapterTOCFrameData>? {
        val frameSet = frameSets!![id]
        if (frameSet != null) {
            val chapterData = ArrayList<ID3v2ChapterTOCFrameData>()
            val frames = frameSet.frames
            for (frame in frames!!) {
                var frameData: ID3v2ChapterTOCFrameData
                try {
                    frameData = ID3v2ChapterTOCFrameData(
                        useFrameUnsynchronisation(),
                        frame.data
                    )
                    chapterData.add(frameData)
                } catch (e: InvalidDataException) {
                    // do nothing
                }
            }
            return chapterData
        }
        return null
    }

    protected fun extractTextFrameData(id: String?): ID3v2TextFrameData? {
        val frameSet = frameSets!![id]
        if (frameSet != null) {
            val frame = frameSet.frames!![0]
            val frameData: ID3v2TextFrameData
            try {
                frameData = ID3v2TextFrameData(useFrameUnsynchronisation(), frame.data)
                return frameData
            } catch (e: InvalidDataException) {
                // do nothing
            }
        }
        return null
    }

    private fun extractWWWFrameData(id: String): ID3v2WWWFrameData? {
        val frameSet = frameSets!![id]
        if (frameSet != null) {
            val frame = frameSet.frames!![0]
            val frameData: ID3v2WWWFrameData
            try {
                frameData = ID3v2WWWFrameData(useFrameUnsynchronisation(), frame.data)
                return frameData
            } catch (e: InvalidDataException) {
                // do nothing
            }
        }
        return null
    }

    private fun extractUrlFrameData(id: String): ID3v2UrlFrameData? {
        val frameSet = frameSets!![id]
        if (frameSet != null) {
            val frame = frameSet.frames!![0]
            val frameData: ID3v2UrlFrameData
            try {
                frameData = ID3v2UrlFrameData(useFrameUnsynchronisation(), frame.data)
                return frameData
            } catch (e: InvalidDataException) {
                // do nothing
            }
        }
        return null
    }

    private fun extractCommentFrameData(id: String, itunes: Boolean): ID3v2CommentFrameData? {
        val frameSet = frameSets!![id]
        if (frameSet != null) {
            for (frame in frameSet.frames!!) {
                var frameData: ID3v2CommentFrameData
                try {
                    frameData = ID3v2CommentFrameData(useFrameUnsynchronisation(), frame.data)
                    if (itunes && ITUNES_COMMENT_DESCRIPTION == frameData.getDescription()
                            .toString()
                    ) {
                        return frameData
                    } else if (!itunes) {
                        return frameData
                    }
                } catch (e: InvalidDataException) {
                    // Do nothing
                }
            }
        }
        return null
    }

    private fun createPictureFrameData(id: String): ID3v2PictureFrameData? {
        val frameSet = frameSets!![id]
        if (frameSet != null) {
            val frame = frameSet.frames!![0]
            val frameData: ID3v2PictureFrameData
            try {
                frameData = if (obsoleteFormat) ID3v2ObseletePictureFrameData(
                    useFrameUnsynchronisation(),
                    frame.data
                ) else ID3v2PictureFrameData(useFrameUnsynchronisation(), frame.data)
                return frameData
            } catch (e: InvalidDataException) {
                // do nothing
            }
        }
        return null
    }

    private fun extractPopmFrameData(id: String): ID3v2PopmFrameData? {
        val frameSet = frameSets!![id]
        if (frameSet != null) {
            val frame = frameSet.frames!![0]
            val frameData: ID3v2PopmFrameData
            try {
                frameData = ID3v2PopmFrameData(useFrameUnsynchronisation(), frame.data)
                return frameData
            } catch (e: InvalidDataException) {
                // do nothing
            }
        }
        return null
    }

    override fun equals(obj: Any?): Boolean {
        if (obj !is AbstractID3v2Tag) return false
        if (super.equals(obj)) return true
        val other = obj
        if (unsynchronisation != other.unsynchronisation) return false
        if (extendedHeader != other.extendedHeader) return false
        if (experimental != other.experimental) return false
        if (footer != other.footer) return false
        if (compression != other.compression) return false
        if (dataLength != other.dataLength) return false
        if (extendedHeaderLength != other.extendedHeaderLength) return false
        if (version == null) {
            if (other.version != null) return false
        } else if (other.version == null) return false else if (version != other.version) return false
        if (frameSets == null) {
            if (other.frameSets != null) return false
        } else if (other.frameSets == null) return false else if (frameSets != other.frameSets) return false
        return true
    }

    companion object {
        const val ID_IMAGE = "APIC"
        const val ID_ENCODER = "TENC"
        const val ID_URL = "WXXX"
        const val ID_ARTIST_URL = "WOAR"
        const val ID_COMMERCIAL_URL = "WCOM"
        const val ID_COPYRIGHT_URL = "WCOP"
        const val ID_AUDIOFILE_URL = "WOAF"
        const val ID_AUDIOSOURCE_URL = "WOAS"
        const val ID_RADIOSTATION_URL = "WORS"
        const val ID_PAYMENT_URL = "WPAY"
        const val ID_PUBLISHER_URL = "WPUB"
        const val ID_COPYRIGHT = "TCOP"
        const val ID_ORIGINAL_ARTIST = "TOPE"
        const val ID_BPM = "TBPM"
        const val ID_COMPOSER = "TCOM"
        const val ID_PUBLISHER = "TPUB"
        const val ID_COMMENT = "COMM"
        const val ID_TEXT_LYRICS = "USLT"
        const val ID_GENRE = "TCON"
        const val ID_YEAR = "TYER"
        const val ID_DATE = "TDAT"
        const val ID_ALBUM = "TALB"
        const val ID_TITLE = "TIT2"
        const val ID_KEY = "TKEY"
        const val ID_ARTIST = "TPE1"
        const val ID_ALBUM_ARTIST = "TPE2"
        const val ID_TRACK = "TRCK"
        const val ID_PART_OF_SET = "TPOS"
        const val ID_COMPILATION = "TCMP"
        const val ID_CHAPTER_TOC = "CTOC"
        const val ID_CHAPTER = "CHAP"
        const val ID_GROUPING = "TIT1"
        const val ID_RATING = "POPM"
        const val ID_IMAGE_OBSELETE = "PIC"
        const val ID_ENCODER_OBSELETE = "TEN"
        const val ID_URL_OBSELETE = "WXX"
        const val ID_COPYRIGHT_OBSELETE = "TCR"
        const val ID_ORIGINAL_ARTIST_OBSELETE = "TOA"
        const val ID_BPM_OBSELETE = "TBP"
        const val ID_COMPOSER_OBSELETE = "TCM"
        const val ID_PUBLISHER_OBSELETE = "TBP"
        const val ID_COMMENT_OBSELETE = "COM"
        const val ID_GENRE_OBSELETE = "TCO"
        const val ID_YEAR_OBSELETE = "TYE"
        const val ID_DATE_OBSELETE = "TDA"
        const val ID_ALBUM_OBSELETE = "TAL"
        const val ID_TITLE_OBSELETE = "TT2"
        const val ID_KEY_OBSELETE = "TKE"
        const val ID_ARTIST_OBSELETE = "TP1"
        const val ID_ALBUM_ARTIST_OBSELETE = "TP2"
        const val ID_TRACK_OBSELETE = "TRK"
        const val ID_PART_OF_SET_OBSELETE = "TPA"
        const val ID_COMPILATION_OBSELETE = "TCP"
        const val ID_GROUPING_OBSELETE = "TT1"
        const val PICTURETYPE_OTHER: Byte = 0x0
        const val PICTURETYPE_32PXICON: Byte = 0x1
        const val PICTURETYPE_OTHERICON: Byte = 0x2
        const val PICTURETYPE_FRONTCOVER: Byte = 0x3
        const val PICTURETYPE_BACKCOVER: Byte = 0x4
        const val PICTURETYPE_LEAFLET: Byte = 0x5
        const val PICTURETYPE_MEDIA: Byte = 0x6
        const val PICTURETYPE_LEADARTIST: Byte = 0x7
        const val PICTURETYPE_ARTIST: Byte = 0x8
        const val PICTURETYPE_CONDUCTOR: Byte = 0x9
        const val PICTURETYPE_BAND: Byte = 0xA
        const val PICTURETYPE_COMPOSER: Byte = 0xB
        const val PICTURETYPE_LYRICIST: Byte = 0xC
        const val PICTURETYPE_RECORDINGLOCATION: Byte = 0xD
        const val PICTURETYPE_DURING_RECORDING: Byte = 0xE
        const val PICTURETYPE_DURING_PERFORMANCE: Byte = 0xF
        const val PICTURETYPE_SCREEN_CAPTURE: Byte = 0x10
        const val PICTURETYPE_ILLUSTRATION: Byte = 0x12
        const val PICTURETYPE_BAND_LOGOTYPE: Byte = 0x13
        const val PICTURETYPE_PUBLISHER_LOGOTYPE: Byte = 0x14
        const val TAG = "ID3"
        protected const val FOOTER_TAG = "3DI"
        const val HEADER_LENGTH = 10
        protected const val FOOTER_LENGTH = 10
        const val MAJOR_VERSION_OFFSET = 3
        const val MINOR_VERSION_OFFSET = 4
        const val FLAGS_OFFSET = 5
        const val DATA_LENGTH_OFFSET = 6
        const val FOOTER_BIT = 4
        const val EXPERIMENTAL_BIT = 5
        const val EXTENDED_HEADER_BIT = 6
        const val COMPRESSION_BIT = 6
        const val UNSYNCHRONISATION_BIT = 7
        const val PADDING_LENGTH = 256
        private const val ITUNES_COMMENT_DESCRIPTION = "iTunNORM"
    }
}