package com.mpatric.mp3agic

import com.mpatric.mp3agic.BufferTools.byteBufferToString
import com.mpatric.mp3agic.BufferTools.copyBuffer
import com.mpatric.mp3agic.NoSuchTagException
import com.mpatric.mp3agic.TestHelper
import com.mpatric.mp3agic.UnsupportedTagException
import org.junit.Assert
import org.junit.Assert.assertArrayEquals
import org.junit.Test
import java.io.IOException

class ID3v2TagTest {
    @Test
    @Throws(NoSuchTagException::class, UnsupportedTagException::class, InvalidDataException::class)
    fun shouldInitialiseFromHeaderBlockWithValidHeaders() {
        val header = copyBuffer(ID3V2_HEADER, 0, ID3V2_HEADER.size)
        header[3] = 2
        header[4] = 0
        var id3v2tag: ID3v2
        id3v2tag = createTag(header)
        Assert.assertEquals("2.0", id3v2tag.version)
        header[3] = 3
        id3v2tag = createTag(header)
        Assert.assertEquals("3.0", id3v2tag.version)
        header[3] = 4
        id3v2tag = createTag(header)
        Assert.assertEquals("4.0", id3v2tag.version)
    }

    @Test
    @Throws(NoSuchTagException::class, UnsupportedTagException::class, InvalidDataException::class)
    fun shouldCalculateCorrectDataLengthsFromHeaderBlock() {
        val header = copyBuffer(ID3V2_HEADER, 0, ID3V2_HEADER.size)
        var id3v2tag = createTag(header)
        Assert.assertEquals(257, id3v2tag.dataLength.toLong())
        header[8] = 0x09
        header[9] = 0x41
        id3v2tag = createTag(header)
        Assert.assertEquals(1217, id3v2tag.dataLength.toLong())
    }

    @Test
    @Throws(NoSuchTagException::class, InvalidDataException::class)
    fun shouldThrowExceptionForNonSupportedVersionInId3v2HeaderBlock() {
        val header = copyBuffer(ID3V2_HEADER, 0, ID3V2_HEADER.size)
        header[3] = 5
        header[4] = 0
        try {
            ID3v2TagFactory.createTag(header)
            Assert.fail("UnsupportedTagException expected but not thrown")
        } catch (e: UnsupportedTagException) {
            // expected
        }
    }

    @Test
    @Throws(Exception::class)
    fun shouldSortId3TagsAlphabetically() {
        val buffer: ByteArray = TestHelper.Companion.loadFile("src/test/resources/v1andv23tags.mp3")
        val id3v2tag: ID3v2 = ID3v2TagFactory.createTag(buffer)
        val frameSets = id3v2tag.frameSets
        val frameSetIterator = frameSets!!.values.iterator()
        var lastKey = ""
        while (frameSetIterator.hasNext()) {
            val frameSet = frameSetIterator.next()
            Assert.assertTrue(frameSet!!.id!!.compareTo(lastKey) > 0)
            lastKey = frameSet.id!!
        }
    }

    @Test
    @Throws(
        IOException::class,
        NoSuchTagException::class,
        UnsupportedTagException::class,
        InvalidDataException::class
    )
    fun shouldReadFramesFromMp3With32Tag() {
        val buffer: ByteArray = TestHelper.Companion.loadFile("src/test/resources/v1andv23tags.mp3")
        val id3v2tag: ID3v2 = ID3v2TagFactory.createTag(buffer)
        Assert.assertEquals("3.0", id3v2tag.version)
        Assert.assertEquals(0x44B, id3v2tag.length.toLong())
        Assert.assertEquals(12, id3v2tag.frameSets!!.size.toLong())
        Assert.assertEquals(1, id3v2tag.frameSets!!["TENC"]!!.frames.size.toLong())
        Assert.assertEquals(1, id3v2tag.frameSets!!["WXXX"]!!.frames.size.toLong())
        Assert.assertEquals(1, id3v2tag.frameSets!!["TCOP"]!!.frames.size.toLong())
        Assert.assertEquals(1, id3v2tag.frameSets!!["TOPE"]!!.frames.size.toLong())
        Assert.assertEquals(1, id3v2tag.frameSets!!["TCOM"]!!.frames.size.toLong())
        Assert.assertEquals(2, id3v2tag.frameSets!!["COMM"]!!.frames.size.toLong())
        Assert.assertEquals(1, id3v2tag.frameSets!!["TPE1"]!!.frames.size.toLong())
        Assert.assertEquals(1, id3v2tag.frameSets!!["TALB"]!!.frames.size.toLong())
        Assert.assertEquals(1, id3v2tag.frameSets!!["TRCK"]!!.frames.size.toLong())
        Assert.assertEquals(1, id3v2tag.frameSets!!["TYER"]!!.frames.size.toLong())
        Assert.assertEquals(1, id3v2tag.frameSets!!["TCON"]!!.frames.size.toLong())
        Assert.assertEquals(1, id3v2tag.frameSets!!["TIT2"]!!.frames.size.toLong())
    }

    @Test
    @Throws(
        IOException::class,
        NoSuchTagException::class,
        UnsupportedTagException::class,
        InvalidDataException::class
    )
    fun shouldReadId3v2WithFooter() {
        val buffer: ByteArray = TestHelper.Companion.loadFile("src/test/resources/v1andv24tags.mp3")
        val id3v2tag: ID3v2 = ID3v2TagFactory.createTag(buffer)
        Assert.assertEquals("4.0", id3v2tag.version)
        Assert.assertEquals(0x44B, id3v2tag.length.toLong())
    }

    @Test
    @Throws(Exception::class)
    fun shouldReadTagFieldsFromMp3With24tag() {
        val buffer: ByteArray =
            TestHelper.Companion.loadFile("src/test/resources/v24tagswithalbumimage.mp3")
        val id3v24tag: ID3v2 = ID3v2TagFactory.createTag(buffer)
        Assert.assertEquals("4.0", id3v24tag.version)
        Assert.assertEquals("1", id3v24tag.track)
        Assert.assertEquals("ARTIST123456789012345678901234", id3v24tag.artist)
        Assert.assertEquals("TITLE1234567890123456789012345", id3v24tag.title)
        Assert.assertEquals("ALBUM1234567890123456789012345", id3v24tag.album)
        Assert.assertEquals(0x0d, id3v24tag.genre.toLong())
        Assert.assertEquals("Pop", id3v24tag.genreDescription)
        Assert.assertEquals("COMMENT123456789012345678901", id3v24tag.comment)
        Assert.assertEquals("LYRICS1234567890123456789012345", id3v24tag.lyrics)
        Assert.assertEquals("COMPOSER23456789012345678901234", id3v24tag.composer)
        Assert.assertEquals("ORIGARTIST234567890123456789012", id3v24tag.originalArtist)
        Assert.assertEquals("COPYRIGHT2345678901234567890123", id3v24tag.copyright)
        Assert.assertEquals("URL2345678901234567890123456789", id3v24tag.url)
        Assert.assertEquals("COMMERCIALURL234567890123456789", id3v24tag.commercialUrl)
        Assert.assertEquals("COPYRIGHTURL2345678901234567890", id3v24tag.copyrightUrl)
        Assert.assertEquals("OFFICIALARTISTURL23456789012345", id3v24tag.artistUrl)
        Assert.assertEquals("OFFICIALAUDIOFILE23456789012345", id3v24tag.audiofileUrl)
        Assert.assertEquals("OFFICIALAUDIOSOURCE234567890123", id3v24tag.audioSourceUrl)
        Assert.assertEquals("INTERNETRADIOSTATIONURL23456783", id3v24tag.radiostationUrl)
        Assert.assertEquals("PAYMENTURL234567890123456789012", id3v24tag.paymentUrl)
        Assert.assertEquals("PUBLISHERURL2345678901234567890", id3v24tag.publisherUrl)
        Assert.assertEquals("ENCODER234567890123456789012345", id3v24tag.encoder)
        Assert.assertEquals(1885, id3v24tag.albumImage?.size)
        Assert.assertEquals("image/png", id3v24tag.albumImageMimeType)
    }

    @Test
    @Throws(Exception::class)
    fun shouldReadTagFieldsFromMp3With32tag() {
        val buffer: ByteArray =
            TestHelper.Companion.loadFile("src/test/resources/v1andv23tagswithalbumimage.mp3")
        val id3tag: ID3v2 = ID3v2TagFactory.createTag(buffer)
        Assert.assertEquals("1", id3tag.track)
        Assert.assertEquals("ARTIST123456789012345678901234", id3tag.artist)
        Assert.assertEquals("TITLE1234567890123456789012345", id3tag.title)
        Assert.assertEquals("ALBUM1234567890123456789012345", id3tag.album)
        Assert.assertEquals("2001", id3tag.year)
        Assert.assertEquals(0x0d, id3tag.genre.toLong())
        Assert.assertEquals("Pop", id3tag.genreDescription)
        Assert.assertEquals("COMMENT123456789012345678901", id3tag.comment)
        Assert.assertEquals("LYRICS1234567890123456789012345", id3tag.lyrics)
        Assert.assertEquals("COMPOSER23456789012345678901234", id3tag.composer)
        Assert.assertEquals("ORIGARTIST234567890123456789012", id3tag.originalArtist)
        Assert.assertEquals("COPYRIGHT2345678901234567890123", id3tag.copyright)
        Assert.assertEquals("URL2345678901234567890123456789", id3tag.url)
        Assert.assertEquals("ENCODER234567890123456789012345", id3tag.encoder)
        Assert.assertEquals(1885, id3tag.albumImage?.size)
        Assert.assertEquals("image/png", id3tag.albumImageMimeType)
    }

    @Test
    @Throws(Exception::class)
    fun shouldConvert23TagToBytesAndBackToEquivalentTag() {
        val id3tag: ID3v2 = ID3v23Tag()
        setTagFields(id3tag)
        val data = id3tag.toBytes()
        val id3tagCopy: ID3v2 = ID3v23Tag(data!!)
        Assert.assertEquals(2340, data.size.toLong())
        Assert.assertEquals(id3tag, id3tagCopy)
    }

    @Test
    @Throws(Exception::class)
    fun shouldConvert24TagWithFooterToBytesAndBackToEquivalentTag() {
        val id3tag: ID3v2 = ID3v24Tag()
        setTagFields(id3tag)
        id3tag.footer = true
        val data = id3tag.toBytes()
        val id3tagCopy: ID3v2 = ID3v24Tag(data!!)
        Assert.assertEquals(2350, data.size.toLong())
        Assert.assertEquals(id3tag, id3tagCopy)
    }

    @Test
    @Throws(Exception::class)
    fun shouldConvert24TagWithPaddingToBytesAndBackToEquivalentTag() {
        val id3tag: ID3v2 = ID3v24Tag()
        setTagFields(id3tag)
        id3tag.setPadding(true)
        val data = id3tag.toBytes()
        val id3tagCopy: ID3v2 = ID3v24Tag(data!!)
        Assert.assertEquals((2340 + AbstractID3v2Tag.PADDING_LENGTH).toLong(), data.size.toLong())
        Assert.assertEquals(id3tag, id3tagCopy)
    }

    @Test
    @Throws(Exception::class)
    fun shouldNotUsePaddingOnA24TagIfItHasAFooter() {
        val id3tag: ID3v2 = ID3v24Tag()
        setTagFields(id3tag)
        id3tag.footer = true
        id3tag.setPadding(true)
        val data = id3tag.toBytes()
        Assert.assertEquals(2350, data!!.size.toLong())
    }

    @Test
    @Throws(Exception::class)
    fun shouldExtractGenreNumberFromCombinedGenreStringsCorrectly() {
        val id3tag = ID3v23TagForTesting()
        try {
            id3tag.extractGenreNumber("")
            Assert.fail("NumberFormatException expected but not thrown")
        } catch (e: NumberFormatException) {
            // expected
        }
        Assert.assertEquals(13, id3tag.extractGenreNumber("13").toLong())
        Assert.assertEquals(13, id3tag.extractGenreNumber("(13)").toLong())
        Assert.assertEquals(13, id3tag.extractGenreNumber("(13)Pop").toLong())
    }

    @Test
    @Throws(Exception::class)
    fun shouldExtractGenreDescriptionFromCombinedGenreStringsCorrectly() {
        val id3tag = ID3v23TagForTesting()
        Assert.assertNull(id3tag.extractGenreDescription(""))
        Assert.assertEquals("", id3tag.extractGenreDescription("(13)"))
        Assert.assertEquals("Pop", id3tag.extractGenreDescription("(13)Pop"))
    }

    @Test
    @Throws(Exception::class)
    fun shouldSetCombinedGenreOnTag() {
        val id3tag: ID3v2 = ID3v23Tag()
        setTagFields(id3tag)
        val frameSets = id3tag.frameSets
        val frameSet = frameSets!!["TCON"]
        val frames = frameSet!!.frames
        val frame = frames!![0]
        val bytes = frame.data
        val genre = byteBufferToString(bytes, 1, bytes.size - 1)
        Assert.assertEquals("(13)Pop", genre)
    }

    @Test
    @Throws(Exception::class)
    fun testSetGenreDescriptionOn23Tag() {
        val id3tag: ID3v2 = ID3v23Tag()
        setTagFields(id3tag)
        id3tag.genreDescription = "Jazz"
        Assert.assertEquals("Jazz", id3tag.genreDescription)
        Assert.assertEquals(8, id3tag.genre.toLong())
        val frameSets = id3tag.frameSets
        val frameSet = frameSets!!["TCON"]
        val frames = frameSet!!.frames
        val frame = frames!![0]
        val bytes = frame.data
        val genre = byteBufferToString(bytes, 1, bytes.size - 1)
        Assert.assertEquals("(8)Jazz", genre)
    }

    @Test
    @Throws(Exception::class)
    fun testSetGenreDescriptionOn23TagWithUnknownGenre() {
        val id3tag: ID3v2 = ID3v23Tag()
        setTagFields(id3tag)
        try {
            id3tag.genreDescription = "Bebop"
            Assert.fail("expected IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            // fine
        }
    }

    @Test
    @Throws(Exception::class)
    fun testSetGenreDescriptionOn24Tag() {
        val id3tag: ID3v2 = ID3v24Tag()
        setTagFields(id3tag)
        id3tag.genreDescription = "Jazz"
        Assert.assertEquals("Jazz", id3tag.genreDescription)
        Assert.assertEquals(8, id3tag.genre.toLong())
        val frameSets = id3tag.frameSets
        val frameSet = frameSets!!["TCON"]
        val frames = frameSet!!.frames
        val frame = frames!![0]
        val bytes = frame.data
        val genre = byteBufferToString(bytes, 1, bytes.size - 1)
        Assert.assertEquals("Jazz", genre)
    }

    @Test
    @Throws(Exception::class)
    fun testSetGenreDescriptionOn24TagWithUnknownGenre() {
        val id3tag: ID3v2 = ID3v24Tag()
        setTagFields(id3tag)
        id3tag.genreDescription = "Bebop"
        Assert.assertEquals("Bebop", id3tag.genreDescription)
        Assert.assertEquals(-1, id3tag.genre.toLong())
        val frameSets = id3tag.frameSets
        val frameSet = frameSets!!["TCON"]
        val frames = frameSet!!.frames
        val frame = frames!![0]
        val bytes = frame.data
        val genre = byteBufferToString(bytes, 1, bytes.size - 1)
        Assert.assertEquals("Bebop", genre)
    }

    @Test
    @Throws(Exception::class)
    fun shouldReadCombinedGenreInTag() {
        val id3tag: ID3v2 = ID3v23Tag()
        setTagFields(id3tag)
        val bytes = id3tag.toBytes()
        val id3tagFromData: ID3v2 = ID3v23Tag(bytes!!)
        Assert.assertEquals(13, id3tagFromData.genre.toLong())
        Assert.assertEquals("Pop", id3tagFromData.genreDescription)
    }

    @Test
    @Throws(Exception::class)
    fun shouldGetCommentAndItunesComment() {
        val buffer: ByteArray =
            TestHelper.Companion.loadFile("src/test/resources/withitunescomment.mp3")
        val id3tag: ID3v2 = ID3v2TagFactory.createTag(buffer)
        Assert.assertEquals("COMMENT123456789012345678901", id3tag.comment)
        Assert.assertEquals(
            " 00000A78 00000A74 00000C7C 00000C6C 00000000 00000000 000051F7 00005634 00000000 00000000",
            id3tag.itunesComment
        )
    }

    @Test
    @Throws(Exception::class)
    fun shouldReadFramesFromMp3WithObselete32Tag() {
        val buffer: ByteArray = TestHelper.Companion.loadFile("src/test/resources/obsolete.mp3")
        val id3v2tag: ID3v2 = ID3v2TagFactory.createTag(buffer)
        Assert.assertEquals("2.0", id3v2tag.version)
        Assert.assertEquals(0x3c5a2, id3v2tag.length.toLong())
        Assert.assertEquals(10, id3v2tag.frameSets!!.size.toLong())
        Assert.assertEquals(1, id3v2tag.frameSets!!["TCM"]!!.frames.size.toLong())
        Assert.assertEquals(2, id3v2tag.frameSets!!["COM"]!!.frames.size.toLong())
        Assert.assertEquals(1, id3v2tag.frameSets!!["TP1"]!!.frames.size.toLong())
        Assert.assertEquals(1, id3v2tag.frameSets!!["TAL"]!!.frames.size.toLong())
        Assert.assertEquals(1, id3v2tag.frameSets!!["TRK"]!!.frames.size.toLong())
        Assert.assertEquals(1, id3v2tag.frameSets!!["TPA"]!!.frames.size.toLong())
        Assert.assertEquals(1, id3v2tag.frameSets!!["TYE"]!!.frames.size.toLong())
        Assert.assertEquals(1, id3v2tag.frameSets!!["PIC"]!!.frames.size.toLong())
        Assert.assertEquals(1, id3v2tag.frameSets!!["TCO"]!!.frames.size.toLong())
        Assert.assertEquals(1, id3v2tag.frameSets!!["TT2"]!!.frames.size.toLong())
    }

    @Test
    @Throws(Exception::class)
    fun shouldReadTagFieldsFromMp3WithObselete32tag() {
        val buffer: ByteArray = TestHelper.Companion.loadFile("src/test/resources/obsolete.mp3")
        val id3tag: ID3v2 = ID3v2TagFactory.createTag(buffer)
        Assert.assertEquals("2009", id3tag.year)
        Assert.assertEquals("4/15", id3tag.track)
        Assert.assertEquals("image/png", id3tag.albumImageMimeType)
        Assert.assertEquals(40, id3tag.genre.toLong())
        Assert.assertEquals("Alt Rock", id3tag.genreDescription)
        Assert.assertEquals("NAME1234567890123456789012345678901234567890", id3tag.title)
        Assert.assertEquals("ARTIST1234567890123456789012345678901234567890", id3tag.artist)
        Assert.assertEquals("COMPOSER1234567890123456789012345678901234567890", id3tag.composer)
        Assert.assertEquals("ALBUM1234567890123456789012345678901234567890", id3tag.album)
        Assert.assertEquals("COMMENTS1234567890123456789012345678901234567890", id3tag.comment)
    }

    @Test
    @Throws(Exception::class)
    fun shouldReadTagFieldsWithUnicodeDataFromMp3() {
        val buffer: ByteArray =
            TestHelper.Companion.loadFile("src/test/resources/v23unicodetags.mp3")
        val id3tag: ID3v2 = ID3v2TagFactory.createTag(buffer)
        Assert.assertEquals("\u03B3\u03B5\u03B9\u03AC \u03C3\u03BF\u03C5", id3tag.artist) // greek
        Assert.assertEquals("\u4E2D\u6587", id3tag.title) // chinese
        Assert.assertEquals("\u3053\u3093\u306B\u3061\u306F", id3tag.album) // japanese
        Assert.assertEquals("\u0AB9\u0AC7\u0AB2\u0ACD\u0AB2\u0ACB", id3tag.composer) // gujarati
    }

    @Test
    @Throws(Exception::class)
    fun shouldSetTagFieldsWithUnicodeDataAndSpecifiedEncodingCorrectly() {
        val id3tag: ID3v2 = ID3v23Tag()
        id3tag.artist = "\u03B3\u03B5\u03B9\u03AC \u03C3\u03BF\u03C5"
        id3tag.title = "\u4E2D\u6587"
        id3tag.album = "\u3053\u3093\u306B\u3061\u306F"
        id3tag.comment = "\u03C3\u03BF\u03C5"
        id3tag.composer = "\u0AB9\u0AC7\u0AB2\u0ACD\u0AB2\u0ACB"
        id3tag.originalArtist = "\u03B3\u03B5\u03B9\u03AC"
        id3tag.copyright = "\u03B3\u03B5"
        id3tag.url = "URL"
        id3tag.encoder = "\u03B9\u03AC"
        val albumImage: ByteArray = TestHelper.Companion.loadFile("src/test/resources/image.png")
        id3tag.setAlbumImage(
            albumImage,
            "image/png",
            ID3v23Tag.PICTURETYPE_OTHER,
            "\u03B3\u03B5\u03B9\u03AC"
        )
    }

    @Test
    @Throws(Exception::class)
    fun shouldExtractChapterTOCFramesFromMp3() {
        val buffer: ByteArray =
            TestHelper.Companion.loadFile("src/test/resources/v23tagwithchapters.mp3")
        val id3tag: ID3v2 = ID3v2TagFactory.createTag(buffer)
        val chapterTOCs = id3tag.chapterTOC
        Assert.assertEquals(1, chapterTOCs!!.size.toLong())
        val tocFrameData = chapterTOCs[0]
        Assert.assertEquals("toc1", tocFrameData.id)
        val expectedChildren = arrayOf("ch1", "ch2", "ch3")
        assertArrayEquals(expectedChildren, tocFrameData.childs)
        val subFrames = tocFrameData.subframes
        Assert.assertEquals(0, subFrames!!.size.toLong())
    }

    @Test
    @Throws(Exception::class)
    fun shouldExtractChapterTOCAndChapterFramesFromMp3() {
        val buffer: ByteArray =
            TestHelper.Companion.loadFile("src/test/resources/v23tagwithchapters.mp3")
        val id3tag: ID3v2 = ID3v2TagFactory.createTag(buffer)
        val chapters = id3tag.chapters
        Assert.assertEquals(3, chapters!!.size.toLong())
        val chapter1 = chapters[0]
        Assert.assertEquals("ch1", chapter1.id)
        Assert.assertEquals(0, chapter1.startTime)
        Assert.assertEquals(5000, chapter1.endTime)
        Assert.assertEquals(-1, chapter1.startOffset)
        Assert.assertEquals(-1, chapter1.endOffset)
        val subFrames1 = chapter1.subframes
        Assert.assertEquals(1, subFrames1!!.size.toLong())
        val subFrame1 = subFrames1[0]
        Assert.assertEquals("TIT2", subFrame1.id)
        val frameData1 = ID3v2TextFrameData(false, subFrame1.data)
        Assert.assertEquals("start", frameData1.text.toString())
        val chapter2 = chapters[1]
        Assert.assertEquals("ch2", chapter2.id)
        Assert.assertEquals(5000, chapter2.startTime)
        Assert.assertEquals(10000, chapter2.endTime)
        Assert.assertEquals(-1, chapter2.startOffset)
        Assert.assertEquals(-1, chapter2.endOffset)
        val subFrames2 = chapter2.subframes
        Assert.assertEquals(1, subFrames2!!.size.toLong())
        val subFrame2 = subFrames2[0]
        Assert.assertEquals("TIT2", subFrame2.id)
        val frameData2 = ID3v2TextFrameData(false, subFrame2.data)
        Assert.assertEquals("5 seconds", frameData2.text.toString())
        val chapter3 = chapters[2]
        Assert.assertEquals("ch3", chapter3.id)
        Assert.assertEquals(10000, chapter3.startTime)
        Assert.assertEquals(15000, chapter3.endTime)
        Assert.assertEquals(-1, chapter3.startOffset)
        Assert.assertEquals(-1, chapter3.endOffset)
        val subFrames3 = chapter3.subframes
        Assert.assertEquals(1, subFrames3!!.size.toLong())
        val subFrame3 = subFrames3[0]
        Assert.assertEquals("TIT2", subFrame3.id)
        val frameData3 = ID3v2TextFrameData(false, subFrame3.data)
        Assert.assertEquals("10 seconds", frameData3.text.toString())
    }

    @Test
    @Throws(Exception::class)
    fun shouldReadTagFieldsFromMp3With32tagResavedByMp3tagWithUTF16Encoding() {
        val buffer: ByteArray =
            TestHelper.Companion.loadFile("src/test/resources/v1andv23tagswithalbumimage-utf16le.mp3")
        val id3tag: ID3v2 = ID3v2TagFactory.createTag(buffer)
        Assert.assertEquals("1", id3tag.track)
        Assert.assertEquals("ARTIST123456789012345678901234", id3tag.artist)
        Assert.assertEquals("TITLE1234567890123456789012345", id3tag.title)
        Assert.assertEquals("ALBUM1234567890123456789012345", id3tag.album)
        Assert.assertEquals("2001", id3tag.year)
        Assert.assertEquals(0x01, id3tag.genre.toLong())
        Assert.assertEquals("Classic Rock", id3tag.genreDescription)
        Assert.assertEquals("COMMENT123456789012345678901", id3tag.comment)
        Assert.assertEquals("COMPOSER23456789012345678901234", id3tag.composer)
        Assert.assertEquals("ORIGARTIST234567890123456789012", id3tag.originalArtist)
        Assert.assertEquals("COPYRIGHT2345678901234567890123", id3tag.copyright)
        Assert.assertEquals("URL2345678901234567890123456789", id3tag.url)
        Assert.assertEquals("ENCODER234567890123456789012345", id3tag.encoder)
        Assert.assertEquals(1885, id3tag.albumImage?.size)
        Assert.assertEquals("image/png", id3tag.albumImageMimeType)
    }

    @Test
    @Throws(Exception::class)
    fun shouldRemoveAlbumImageFrame() {
        val buffer: ByteArray =
            TestHelper.Companion.loadFile("src/test/resources/v1andv23tagswithalbumimage.mp3")
        val id3tag: ID3v2 = ID3v2TagFactory.createTag(buffer)
        Assert.assertEquals(1885, id3tag.albumImage?.size)
        id3tag.clearAlbumImage()
        Assert.assertNull(id3tag.albumImage)
    }

    @Test
    @Throws(Exception::class)
    fun shouldReadBPM() {
        val buffer: ByteArray =
            TestHelper.Companion.loadFile("src/test/resources/v23tagwithbpm.mp3")
        val id3tag: ID3v2 = ID3v2TagFactory.createTag(buffer)
        Assert.assertEquals(84, id3tag.bPM.toLong())
    }

    @Test
    @Throws(Exception::class)
    fun shouldReadFloatingPointBPM() {
        val buffer: ByteArray =
            TestHelper.Companion.loadFile("src/test/resources/v23tagwithbpmfloat.mp3")
        val id3tag: ID3v2 = ID3v2TagFactory.createTag(buffer)
        Assert.assertEquals(84, id3tag.bPM.toLong())
    }

    @Test
    @Throws(Exception::class)
    fun shouldReadFloatingPointBPMWithCommaDelimiter() {
        val buffer: ByteArray =
            TestHelper.Companion.loadFile("src/test/resources/v23tagwithbpmfloatwithcomma.mp3")
        val id3tag: ID3v2 = ID3v2TagFactory.createTag(buffer)
        Assert.assertEquals(84, id3tag.bPM.toLong())
    }

    @Test
    @Throws(Exception::class)
    fun shouldReadWmpRating() {
        val buffer: ByteArray =
            TestHelper.Companion.loadFile("src/test/resources/v23tagwithwmprating.mp3")
        val id3tag: ID3v2 = ID3v2TagFactory.createTag(buffer)
        Assert.assertEquals(3, id3tag.wmpRating.toLong())
    }

    @Test
    @Throws(Exception::class)
    fun shouldWriteWmpRating() {
        val id3tag: ID3v2 = ID3v23Tag()
        setTagFields(id3tag)
        val expectedUnsetValue = -1
        Assert.assertEquals(expectedUnsetValue.toLong(), id3tag.wmpRating.toLong())
        val newValue = 4
        id3tag.wmpRating = newValue
        Assert.assertEquals(newValue.toLong(), id3tag.wmpRating.toLong())
    }

    @Test
    @Throws(Exception::class)
    fun shouldIgnoreInvalidWmpRatingOnWrite() {
        val id3tag: ID3v2 = ID3v23Tag()
        setTagFields(id3tag)
        val originalValue = id3tag.wmpRating
        val invalidValue = 6
        id3tag.wmpRating = invalidValue
        Assert.assertEquals(originalValue.toLong(), id3tag.wmpRating.toLong())
    }

    @Throws(IOException::class)
    private fun setTagFields(id3tag: ID3v2) {
        id3tag.track = "1"
        id3tag.artist = "ARTIST"
        id3tag.title = "TITLE"
        id3tag.album = "ALBUM"
        id3tag.year = "1954"
        id3tag.genre = 0x0d
        id3tag.comment = "COMMENT"
        id3tag.composer = "COMPOSER"
        id3tag.originalArtist = "ORIGINALARTIST"
        id3tag.copyright = "COPYRIGHT"
        id3tag.url = "URL"
        id3tag.commercialUrl = "COMMERCIALURL"
        id3tag.copyrightUrl = "COPYRIGHTURL"
        id3tag.artistUrl = "OFFICIALARTISTURL"
        id3tag.audiofileUrl = "OFFICIALAUDIOFILEURL"
        id3tag.audioSourceUrl = "OFFICIALAUDIOSOURCEURL"
        id3tag.radiostationUrl = "INTERNETRADIOSTATIONURL"
        id3tag.paymentUrl = "PAYMENTURL"
        id3tag.publisherUrl = "PUBLISHERURL"
        id3tag.encoder = "ENCODER"
        val albumImage: ByteArray = TestHelper.Companion.loadFile("src/test/resources/image.png")
        id3tag.setAlbumImage(albumImage, "image/png")
    }

    @Throws(NoSuchTagException::class, UnsupportedTagException::class, InvalidDataException::class)
    private fun createTag(buffer: ByteArray): ID3v2 {
        val factory = ID3v2TagFactoryForTesting()
        return factory.createTag(buffer)
    }

    internal inner class ID3v22TagForTesting(buffer: ByteArray?) : ID3v22Tag(
        buffer!!
    ) {
        override fun unpackFrames(buffer: ByteArray, offset: Int, framesLength: Int): Int {
            return offset
        }
    }

    internal inner class ID3v23TagForTesting : ID3v23Tag {
        constructor() : super() {}
        constructor(buffer: ByteArray?) : super(buffer!!) {}

        override fun unpackFrames(buffer: ByteArray, offset: Int, framesLength: Int): Int {
            return offset
        }
    }

    internal inner class ID3v24TagForTesting(buffer: ByteArray?) : ID3v24Tag(
        buffer!!
    ) {
        override fun unpackFrames(buffer: ByteArray, offset: Int, framesLength: Int): Int {
            return offset
        }
    }

    internal inner class ID3v2TagFactoryForTesting {
        @Throws(
            NoSuchTagException::class,
            UnsupportedTagException::class,
            InvalidDataException::class
        )
        fun createTag(buffer: ByteArray): ID3v2 {
            val majorVersion = buffer[MAJOR_VERSION_OFFSET]
                .toInt()
            when (majorVersion) {
                2 -> return ID3v22TagForTesting(buffer)
                3 -> return ID3v23TagForTesting(buffer)
                4 -> return ID3v24TagForTesting(buffer)
            }
            throw UnsupportedTagException("Tag version not supported")
        }

        val MAJOR_VERSION_OFFSET = 3
    }

    companion object {
        private const val BYTE_I: Byte = 0x49
        private const val BYTE_D: Byte = 0x44
        private const val BYTE_3: Byte = 0x33
        private val ID3V2_HEADER = byteArrayOf(BYTE_I, BYTE_D, BYTE_3, 4, 0, 0, 0, 0, 2, 1)
    }
}