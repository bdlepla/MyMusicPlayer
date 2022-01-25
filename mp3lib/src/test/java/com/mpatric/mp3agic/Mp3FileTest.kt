package com.mpatric.mp3agic

import com.mpatric.mp3agic.NotSupportedException
import com.mpatric.mp3agic.TestHelper
import com.mpatric.mp3agic.UnsupportedTagException
import junit.framework.TestCase
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

class Mp3FileTest {
    @Test
    @Throws(IOException::class, UnsupportedTagException::class, InvalidDataException::class)
    fun shouldLoadMp3WithNoTags() {
        loadAndCheckTestMp3WithNoTags(MP3_WITH_NO_TAGS, 41)
        loadAndCheckTestMp3WithNoTags(MP3_WITH_NO_TAGS, 256)
        loadAndCheckTestMp3WithNoTags(MP3_WITH_NO_TAGS, 1024)
        loadAndCheckTestMp3WithNoTags(MP3_WITH_NO_TAGS, 5000)
        loadAndCheckTestMp3WithNoTags(File(MP3_WITH_NO_TAGS), 41)
        loadAndCheckTestMp3WithNoTags(File(MP3_WITH_NO_TAGS), 256)
        loadAndCheckTestMp3WithNoTags(File(MP3_WITH_NO_TAGS), 1024)
        loadAndCheckTestMp3WithNoTags(File(MP3_WITH_NO_TAGS), 5000)
    }

    @Test
    @Throws(IOException::class, UnsupportedTagException::class, InvalidDataException::class)
    fun shouldLoadMp3WithId3Tags() {
        loadAndCheckTestMp3WithTags(MP3_WITH_ID3V1_AND_ID3V23_TAGS, 41)
        loadAndCheckTestMp3WithTags(MP3_WITH_ID3V1_AND_ID3V23_TAGS, 256)
        loadAndCheckTestMp3WithTags(MP3_WITH_ID3V1_AND_ID3V23_TAGS, 1024)
        loadAndCheckTestMp3WithTags(MP3_WITH_ID3V1_AND_ID3V23_TAGS, 5000)
        loadAndCheckTestMp3WithTags(File(MP3_WITH_ID3V1_AND_ID3V23_TAGS), 41)
        loadAndCheckTestMp3WithTags(File(MP3_WITH_ID3V1_AND_ID3V23_TAGS), 256)
        loadAndCheckTestMp3WithTags(File(MP3_WITH_ID3V1_AND_ID3V23_TAGS), 1024)
        loadAndCheckTestMp3WithTags(File(MP3_WITH_ID3V1_AND_ID3V23_TAGS), 5000)
    }

    @Test
    @Throws(IOException::class, UnsupportedTagException::class, InvalidDataException::class)
    fun shouldLoadMp3WithFakeStartAndEndFrames() {
        loadAndCheckTestMp3WithTags(MP3_WITH_DUMMY_START_AND_END_FRAMES, 41)
        loadAndCheckTestMp3WithTags(MP3_WITH_DUMMY_START_AND_END_FRAMES, 256)
        loadAndCheckTestMp3WithTags(MP3_WITH_DUMMY_START_AND_END_FRAMES, 1024)
        loadAndCheckTestMp3WithTags(MP3_WITH_DUMMY_START_AND_END_FRAMES, 5000)
        loadAndCheckTestMp3WithTags(File(MP3_WITH_DUMMY_START_AND_END_FRAMES), 41)
        loadAndCheckTestMp3WithTags(File(MP3_WITH_DUMMY_START_AND_END_FRAMES), 256)
        loadAndCheckTestMp3WithTags(File(MP3_WITH_DUMMY_START_AND_END_FRAMES), 1024)
        loadAndCheckTestMp3WithTags(File(MP3_WITH_DUMMY_START_AND_END_FRAMES), 5000)
    }

    @Test
    @Throws(IOException::class, UnsupportedTagException::class, InvalidDataException::class)
    fun shouldLoadMp3WithCustomTag() {
        loadAndCheckTestMp3WithCustomTag(MP3_WITH_ID3V1_AND_ID3V23_AND_CUSTOM_TAGS, 41)
        loadAndCheckTestMp3WithCustomTag(MP3_WITH_ID3V1_AND_ID3V23_AND_CUSTOM_TAGS, 256)
        loadAndCheckTestMp3WithCustomTag(MP3_WITH_ID3V1_AND_ID3V23_AND_CUSTOM_TAGS, 1024)
        loadAndCheckTestMp3WithCustomTag(MP3_WITH_ID3V1_AND_ID3V23_AND_CUSTOM_TAGS, 5000)
        loadAndCheckTestMp3WithCustomTag(File(MP3_WITH_ID3V1_AND_ID3V23_AND_CUSTOM_TAGS), 41)
        loadAndCheckTestMp3WithCustomTag(File(MP3_WITH_ID3V1_AND_ID3V23_AND_CUSTOM_TAGS), 256)
        loadAndCheckTestMp3WithCustomTag(File(MP3_WITH_ID3V1_AND_ID3V23_AND_CUSTOM_TAGS), 1024)
        loadAndCheckTestMp3WithCustomTag(File(MP3_WITH_ID3V1_AND_ID3V23_AND_CUSTOM_TAGS), 5000)
    }

    @Test
    @Throws(Exception::class)
    fun shouldThrowExceptionForFileThatIsNotAnMp3() {
        try {
            Mp3File(NOT_AN_MP3)
            Assert.fail("InvalidDataException expected but not thrown")
        } catch (e: InvalidDataException) {
            Assert.assertEquals("No mpegs frames found", e.message)
        }
    }

    @Test
    @Throws(Exception::class)
    fun shouldThrowExceptionForFileThatIsNotAnMp3ForFileConstructor() {
        try {
            Mp3File(File(NOT_AN_MP3))
            Assert.fail("InvalidDataException expected but not thrown")
        } catch (e: InvalidDataException) {
            Assert.assertEquals("No mpegs frames found", e.message)
        }
    }

    @Test
    @Throws(IOException::class)
    fun shouldFindProbableStartOfMpegFramesWithPrescan() {
        val mp3File = Mp3FileForTesting(MP3_WITH_ID3V1_AND_ID3V23_TAGS)
        testShouldFindProbableStartOfMpegFramesWithPrescan(mp3File)
    }

    @Test
    @Throws(IOException::class)
    fun shouldFindProbableStartOfMpegFramesWithPrescanForFileConstructor() {
        val mp3File = Mp3FileForTesting(File(MP3_WITH_ID3V1_AND_ID3V23_TAGS))
        testShouldFindProbableStartOfMpegFramesWithPrescan(mp3File)
    }

    private fun testShouldFindProbableStartOfMpegFramesWithPrescan(mp3File: Mp3FileForTesting) {
        Assert.assertEquals(0x44B, mp3File.preScanResult.toLong())
    }

    @Test
    @Throws(Exception::class)
    fun shouldThrowExceptionIfSavingMp3WithSameNameAsSourceFile() {
        val mp3File = Mp3File(MP3_WITH_ID3V1_AND_ID3V23_AND_CUSTOM_TAGS)
        testShouldThrowExceptionIfSavingMp3WithSameNameAsSourceFile(mp3File)
    }

    @Test
    @Throws(Exception::class)
    fun shouldThrowExceptionIfSavingMp3WithSameNameAsSourceFileForFileConstructor() {
        val mp3File = Mp3File(File(MP3_WITH_ID3V1_AND_ID3V23_AND_CUSTOM_TAGS))
        testShouldThrowExceptionIfSavingMp3WithSameNameAsSourceFile(mp3File)
    }

    @Throws(NotSupportedException::class, IOException::class)
    private fun testShouldThrowExceptionIfSavingMp3WithSameNameAsSourceFile(mp3File: Mp3File) {
        println(mp3File.filename)
        println(MP3_WITH_ID3V1_AND_ID3V23_AND_CUSTOM_TAGS)
        try {
            mp3File.save(MP3_WITH_ID3V1_AND_ID3V23_AND_CUSTOM_TAGS)
            Assert.fail("IllegalArgumentException expected but not thrown")
        } catch (e: IllegalArgumentException) {
            Assert.assertEquals("Save filename same as source filename", e.message)
        }
    }

    @Test
    @Throws(Exception::class)
    fun shouldSaveLoadedMp3WhichIsEquivalentToOriginal() {
        copyAndCheckTestMp3WithCustomTag(MP3_WITH_ID3V1_AND_ID3V23_AND_CUSTOM_TAGS, 41)
        copyAndCheckTestMp3WithCustomTag(MP3_WITH_ID3V1_AND_ID3V23_AND_CUSTOM_TAGS, 256)
        copyAndCheckTestMp3WithCustomTag(MP3_WITH_ID3V1_AND_ID3V23_AND_CUSTOM_TAGS, 1024)
        copyAndCheckTestMp3WithCustomTag(MP3_WITH_ID3V1_AND_ID3V23_AND_CUSTOM_TAGS, 5000)
        copyAndCheckTestMp3WithCustomTag(File(MP3_WITH_ID3V1_AND_ID3V23_AND_CUSTOM_TAGS), 41)
        copyAndCheckTestMp3WithCustomTag(File(MP3_WITH_ID3V1_AND_ID3V23_AND_CUSTOM_TAGS), 256)
        copyAndCheckTestMp3WithCustomTag(File(MP3_WITH_ID3V1_AND_ID3V23_AND_CUSTOM_TAGS), 1024)
        copyAndCheckTestMp3WithCustomTag(File(MP3_WITH_ID3V1_AND_ID3V23_AND_CUSTOM_TAGS), 5000)
    }

    @Test
    @Throws(Exception::class)
    fun shouldLoadAndCheckMp3ContainingUnicodeFields() {
        loadAndCheckTestMp3WithUnicodeFields(MP3_WITH_ID3V23_UNICODE_TAGS, 41)
        loadAndCheckTestMp3WithUnicodeFields(MP3_WITH_ID3V23_UNICODE_TAGS, 256)
        loadAndCheckTestMp3WithUnicodeFields(MP3_WITH_ID3V23_UNICODE_TAGS, 1024)
        loadAndCheckTestMp3WithUnicodeFields(MP3_WITH_ID3V23_UNICODE_TAGS, 5000)
        loadAndCheckTestMp3WithUnicodeFields(File(MP3_WITH_ID3V23_UNICODE_TAGS), 41)
        loadAndCheckTestMp3WithUnicodeFields(File(MP3_WITH_ID3V23_UNICODE_TAGS), 256)
        loadAndCheckTestMp3WithUnicodeFields(File(MP3_WITH_ID3V23_UNICODE_TAGS), 1024)
        loadAndCheckTestMp3WithUnicodeFields(File(MP3_WITH_ID3V23_UNICODE_TAGS), 5000)
    }

    @Test
    @Throws(Exception::class)
    fun shouldSaveLoadedMp3WithUnicodeFieldsWhichIsEquivalentToOriginal() {
        copyAndCheckTestMp3WithUnicodeFields(MP3_WITH_ID3V23_UNICODE_TAGS, 41)
        copyAndCheckTestMp3WithUnicodeFields(MP3_WITH_ID3V23_UNICODE_TAGS, 256)
        copyAndCheckTestMp3WithUnicodeFields(MP3_WITH_ID3V23_UNICODE_TAGS, 1024)
        copyAndCheckTestMp3WithUnicodeFields(MP3_WITH_ID3V23_UNICODE_TAGS, 5000)
        copyAndCheckTestMp3WithUnicodeFields(File(MP3_WITH_ID3V23_UNICODE_TAGS), 41)
        copyAndCheckTestMp3WithUnicodeFields(File(MP3_WITH_ID3V23_UNICODE_TAGS), 256)
        copyAndCheckTestMp3WithUnicodeFields(File(MP3_WITH_ID3V23_UNICODE_TAGS), 1024)
        copyAndCheckTestMp3WithUnicodeFields(File(MP3_WITH_ID3V23_UNICODE_TAGS), 5000)
    }

    @Test
    @Throws(Exception::class)
    fun shouldIgnoreIncompleteMpegFrame() {
        val mp3File = Mp3File(MP3_WITH_INCOMPLETE_MPEG_FRAME, 256)
        testShouldIgnoreIncompleteMpegFrame(mp3File)
    }

    @Test
    @Throws(Exception::class)
    fun shouldIgnoreIncompleteMpegFrameForFileConstructor() {
        val mp3File = Mp3File(File(MP3_WITH_INCOMPLETE_MPEG_FRAME), 256)
        testShouldIgnoreIncompleteMpegFrame(mp3File)
    }

    @Throws(Exception::class)
    private fun testShouldIgnoreIncompleteMpegFrame(mp3File: Mp3File) {
        Assert.assertEquals(0x44B, mp3File.xingOffset)
        Assert.assertEquals(0x5EC, mp3File.startOffset)
        Assert.assertEquals(0xF17, mp3File.endOffset)
        Assert.assertTrue(mp3File.hasId3v1Tag())
        Assert.assertTrue(mp3File.hasId3v2Tag())
        Assert.assertEquals(5, mp3File.frameCount)
    }

    @Test
    @Throws(Exception::class)
    fun shouldInitialiseProperlyWhenNotScanningFile() {
        val mp3File = Mp3File(MP3_WITH_INCOMPLETE_MPEG_FRAME, 256, false)
        testShouldInitialiseProperlyWhenNotScanningFile(mp3File)
    }

    @Test
    @Throws(Exception::class)
    fun shouldInitialiseProperlyWhenNotScanningFileForFileConstructor() {
        val mp3File = Mp3File(File(MP3_WITH_INCOMPLETE_MPEG_FRAME), 256, false)
        testShouldInitialiseProperlyWhenNotScanningFile(mp3File)
    }

    @Throws(Exception::class)
    private fun testShouldInitialiseProperlyWhenNotScanningFile(mp3File: Mp3File) {
        Assert.assertTrue(mp3File.hasId3v1Tag())
        Assert.assertTrue(mp3File.hasId3v2Tag())
    }

    @Test
    @Throws(Exception::class)
    fun shouldRemoveId3v1Tag() {
        val filename = MP3_WITH_ID3V1_AND_ID3V23_AND_CUSTOM_TAGS
        testShouldRemoveId3v1Tag(Mp3File(filename))
    }

    @Test
    @Throws(Exception::class)
    fun shouldRemoveId3v1TagForFileConstructor() {
        val filename = File(MP3_WITH_ID3V1_AND_ID3V23_AND_CUSTOM_TAGS)
        testShouldRemoveId3v1Tag(Mp3File(filename))
    }

    @Throws(Exception::class)
    private fun testShouldRemoveId3v1Tag(mp3File: Mp3File) {
        val saveFilename = mp3File.filename + ".copy"
        try {
            mp3File.removeId3v1Tag()
            mp3File.save(saveFilename)
            val newMp3File = Mp3File(saveFilename)
            TestCase.assertFalse(newMp3File.hasId3v1Tag())
            Assert.assertTrue(newMp3File.hasId3v2Tag())
            Assert.assertTrue(newMp3File.hasCustomTag())
        } finally {
            TestHelper.Companion.deleteFile(saveFilename)
        }
    }

    @Test
    @Throws(Exception::class)
    fun shouldRemoveId3v2Tag() {
        val filename = MP3_WITH_ID3V1_AND_ID3V23_AND_CUSTOM_TAGS
        testShouldRemoveId3v2Tag(Mp3File(filename))
    }

    @Test
    @Throws(Exception::class)
    fun shouldRemoveId3v2TagForFileConstructor() {
        val filename = File(MP3_WITH_ID3V1_AND_ID3V23_AND_CUSTOM_TAGS)
        testShouldRemoveId3v2Tag(Mp3File(filename))
    }

    @Throws(Exception::class)
    private fun testShouldRemoveId3v2Tag(mp3File: Mp3File) {
        val saveFilename = mp3File.filename + ".copy"
        try {
            mp3File.removeId3v2Tag()
            mp3File.save(saveFilename)
            val newMp3File = Mp3File(saveFilename)
            Assert.assertTrue(newMp3File.hasId3v1Tag())
            TestCase.assertFalse(newMp3File.hasId3v2Tag())
            Assert.assertTrue(newMp3File.hasCustomTag())
        } finally {
            TestHelper.Companion.deleteFile(saveFilename)
        }
    }

    @Test
    @Throws(Exception::class)
    fun shouldRemoveCustomTag() {
        val filename = MP3_WITH_ID3V1_AND_ID3V23_AND_CUSTOM_TAGS
        testShouldRemoveCustomTag(Mp3File(filename))
    }

    @Test
    @Throws(Exception::class)
    fun shouldRemoveCustomTagForFileConstructor() {
        val filename = File(MP3_WITH_ID3V1_AND_ID3V23_AND_CUSTOM_TAGS)
        testShouldRemoveCustomTag(Mp3File(filename))
    }

    @Throws(Exception::class)
    private fun testShouldRemoveCustomTag(mp3File: Mp3File) {
        val saveFilename = mp3File.filename + ".copy"
        try {
            mp3File.removeCustomTag()
            mp3File.save(saveFilename)
            val newMp3File = Mp3File(saveFilename)
            Assert.assertTrue(newMp3File.hasId3v1Tag())
            Assert.assertTrue(newMp3File.hasId3v2Tag())
            TestCase.assertFalse(newMp3File.hasCustomTag())
        } finally {
            TestHelper.Companion.deleteFile(saveFilename)
        }
    }

    @Test
    @Throws(Exception::class)
    fun shouldRemoveId3v1AndId3v2AndCustomTags() {
        val filename = MP3_WITH_ID3V1_AND_ID3V23_AND_CUSTOM_TAGS
        testShouldRemoveId3v1AndId3v2AndCustomTags(Mp3File(filename))
    }

    @Test
    @Throws(Exception::class)
    fun shouldRemoveId3v1AndId3v2AndCustomTagsForFileConstructor() {
        val filename = File(MP3_WITH_ID3V1_AND_ID3V23_AND_CUSTOM_TAGS)
        testShouldRemoveId3v1AndId3v2AndCustomTags(Mp3File(filename))
    }

    @Throws(Exception::class)
    private fun testShouldRemoveId3v1AndId3v2AndCustomTags(mp3File: Mp3File) {
        val saveFilename = mp3File.filename + ".copy"
        try {
            mp3File.removeId3v1Tag()
            mp3File.removeId3v2Tag()
            mp3File.removeCustomTag()
            mp3File.save(saveFilename)
            val newMp3File = Mp3File(saveFilename)
            TestCase.assertFalse(newMp3File.hasId3v1Tag())
            TestCase.assertFalse(newMp3File.hasId3v2Tag())
            TestCase.assertFalse(newMp3File.hasCustomTag())
        } finally {
            TestHelper.Companion.deleteFile(saveFilename)
        }
    }

    @Throws(
        IOException::class,
        UnsupportedTagException::class,
        InvalidDataException::class,
        NotSupportedException::class
    )
    private fun copyAndCheckTestMp3WithCustomTag(filename: String, bufferLength: Int): Mp3File {
        val mp3File = loadAndCheckTestMp3WithCustomTag(filename, bufferLength)
        return copyAndCheckTestMp3WithCustomTag(mp3File)
    }

    @Throws(
        IOException::class,
        UnsupportedTagException::class,
        InvalidDataException::class,
        NotSupportedException::class
    )
    private fun copyAndCheckTestMp3WithCustomTag(filename: File, bufferLength: Int): Mp3File {
        val mp3File = loadAndCheckTestMp3WithCustomTag(filename, bufferLength)
        return copyAndCheckTestMp3WithCustomTag(mp3File)
    }

    @Throws(
        NotSupportedException::class,
        IOException::class,
        UnsupportedTagException::class,
        InvalidDataException::class
    )
    private fun copyAndCheckTestMp3WithCustomTag(mp3File: Mp3File): Mp3File {
        val saveFilename = mp3File.filename + ".copy"
        return try {
            mp3File.save(saveFilename)
            val copyMp3file = loadAndCheckTestMp3WithCustomTag(saveFilename, 5000)
            Assert.assertEquals(mp3File.getId3v1Tag(), copyMp3file.getId3v1Tag())
            Assert.assertEquals(mp3File.getId3v2Tag(), copyMp3file.getId3v2Tag())
            Assert.assertArrayEquals(mp3File.customTag, copyMp3file.customTag)
            copyMp3file
        } finally {
            TestHelper.Companion.deleteFile(saveFilename)
        }
    }

    @Throws(
        IOException::class,
        UnsupportedTagException::class,
        InvalidDataException::class,
        NotSupportedException::class
    )
    private fun copyAndCheckTestMp3WithUnicodeFields(filename: String, bufferLength: Int): Mp3File {
        val mp3File = loadAndCheckTestMp3WithUnicodeFields(filename, bufferLength)
        return copyAndCheckTestMp3WithUnicodeFields(mp3File)
    }

    @Throws(
        IOException::class,
        UnsupportedTagException::class,
        InvalidDataException::class,
        NotSupportedException::class
    )
    private fun copyAndCheckTestMp3WithUnicodeFields(filename: File, bufferLength: Int): Mp3File {
        val mp3File = loadAndCheckTestMp3WithUnicodeFields(filename, bufferLength)
        return copyAndCheckTestMp3WithUnicodeFields(mp3File)
    }

    @Throws(
        NotSupportedException::class,
        IOException::class,
        UnsupportedTagException::class,
        InvalidDataException::class
    )
    private fun copyAndCheckTestMp3WithUnicodeFields(mp3File: Mp3File): Mp3File {
        val saveFilename = mp3File.filename + ".copy"
        return try {
            mp3File.save(saveFilename)
            val copyMp3file = loadAndCheckTestMp3WithUnicodeFields(saveFilename, 5000)
            Assert.assertEquals(mp3File.getId3v2Tag(), copyMp3file.getId3v2Tag())
            copyMp3file
        } finally {
            TestHelper.Companion.deleteFile(saveFilename)
        }
    }

    @Throws(IOException::class, UnsupportedTagException::class, InvalidDataException::class)
    private fun loadAndCheckTestMp3WithNoTags(filename: String, bufferLength: Int): Mp3File {
        val mp3File = loadAndCheckTestMp3(filename, bufferLength)
        return loadAndCheckTestMp3WithNoTags(mp3File)
    }

    @Throws(IOException::class, UnsupportedTagException::class, InvalidDataException::class)
    private fun loadAndCheckTestMp3WithNoTags(filename: File, bufferLength: Int): Mp3File {
        val mp3File = loadAndCheckTestMp3(filename, bufferLength)
        return loadAndCheckTestMp3WithNoTags(mp3File)
    }

    private fun loadAndCheckTestMp3WithNoTags(mp3File: Mp3File): Mp3File {
        Assert.assertEquals(0x000, mp3File.xingOffset)
        Assert.assertEquals(0x1A1, mp3File.startOffset)
        Assert.assertEquals(0xB34, mp3File.endOffset)
        TestCase.assertFalse(mp3File.hasId3v1Tag())
        TestCase.assertFalse(mp3File.hasId3v2Tag())
        TestCase.assertFalse(mp3File.hasCustomTag())
        return mp3File
    }

    @Throws(IOException::class, UnsupportedTagException::class, InvalidDataException::class)
    private fun loadAndCheckTestMp3WithTags(filename: String, bufferLength: Int): Mp3File {
        val mp3File = loadAndCheckTestMp3(filename, bufferLength)
        return loadAndCheckTestMp3WithTags(mp3File)
    }

    @Throws(IOException::class, UnsupportedTagException::class, InvalidDataException::class)
    private fun loadAndCheckTestMp3WithTags(filename: File, bufferLength: Int): Mp3File {
        val mp3File = loadAndCheckTestMp3(filename, bufferLength)
        return loadAndCheckTestMp3WithTags(mp3File)
    }

    private fun loadAndCheckTestMp3WithTags(mp3File: Mp3File): Mp3File {
        Assert.assertEquals(0x44B, mp3File.xingOffset)
        Assert.assertEquals(0x5EC, mp3File.startOffset)
        Assert.assertEquals(0xF7F, mp3File.endOffset)
        Assert.assertTrue(mp3File.hasId3v1Tag())
        Assert.assertTrue(mp3File.hasId3v2Tag())
        TestCase.assertFalse(mp3File.hasCustomTag())
        return mp3File
    }

    @Throws(IOException::class, UnsupportedTagException::class, InvalidDataException::class)
    private fun loadAndCheckTestMp3WithUnicodeFields(filename: String, bufferLength: Int): Mp3File {
        val mp3File = loadAndCheckTestMp3(filename, bufferLength)
        return loadAndCheckTestMp3WithUnicodeFields(mp3File)
    }

    @Throws(IOException::class, UnsupportedTagException::class, InvalidDataException::class)
    private fun loadAndCheckTestMp3WithUnicodeFields(filename: File, bufferLength: Int): Mp3File {
        val mp3File = loadAndCheckTestMp3(filename, bufferLength)
        return loadAndCheckTestMp3WithUnicodeFields(mp3File)
    }

    private fun loadAndCheckTestMp3WithUnicodeFields(mp3File: Mp3File): Mp3File {
        Assert.assertEquals(0x0CA, mp3File.xingOffset)
        Assert.assertEquals(0x26B, mp3File.startOffset)
        Assert.assertEquals(0xBFE, mp3File.endOffset)
        TestCase.assertFalse(mp3File.hasId3v1Tag())
        Assert.assertTrue(mp3File.hasId3v2Tag())
        TestCase.assertFalse(mp3File.hasCustomTag())
        return mp3File
    }

    @Throws(IOException::class, UnsupportedTagException::class, InvalidDataException::class)
    private fun loadAndCheckTestMp3WithCustomTag(filename: String, bufferLength: Int): Mp3File {
        val mp3File = loadAndCheckTestMp3(filename, bufferLength)
        return loadAndCheckTestMp3WithCustomTag(mp3File)
    }

    @Throws(IOException::class, UnsupportedTagException::class, InvalidDataException::class)
    private fun loadAndCheckTestMp3WithCustomTag(filename: File, bufferLength: Int): Mp3File {
        val mp3File = loadAndCheckTestMp3(filename, bufferLength)
        return loadAndCheckTestMp3WithCustomTag(mp3File)
    }

    private fun loadAndCheckTestMp3WithCustomTag(mp3File: Mp3File): Mp3File {
        Assert.assertEquals(0x44B, mp3File.xingOffset)
        Assert.assertEquals(0x5EC, mp3File.startOffset)
        Assert.assertEquals(0xF7F, mp3File.endOffset)
        Assert.assertTrue(mp3File.hasId3v1Tag())
        Assert.assertTrue(mp3File.hasId3v2Tag())
        Assert.assertTrue(mp3File.hasCustomTag())
        return mp3File
    }

    @Throws(IOException::class, UnsupportedTagException::class, InvalidDataException::class)
    private fun loadAndCheckTestMp3(filename: String, bufferLength: Int): Mp3File {
        val mp3File = Mp3File(filename, bufferLength)
        return loadAndCheckTestMp3(mp3File)
    }

    @Throws(IOException::class, UnsupportedTagException::class, InvalidDataException::class)
    private fun loadAndCheckTestMp3(filename: File, bufferLength: Int): Mp3File {
        val mp3File = Mp3File(filename, bufferLength)
        return loadAndCheckTestMp3(mp3File)
    }

    private fun loadAndCheckTestMp3(mp3File: Mp3File): Mp3File {
        Assert.assertTrue(mp3File.hasXingFrame())
        Assert.assertEquals(6, mp3File.frameCount)
        Assert.assertEquals(MpegFrame.MPEG_VERSION_1_0, mp3File.version)
        Assert.assertEquals(MpegFrame.MPEG_LAYER_3, mp3File.layer)
        Assert.assertEquals(44100, mp3File.sampleRate)
        Assert.assertEquals(MpegFrame.CHANNEL_MODE_JOINT_STEREO, mp3File.channelMode)
        Assert.assertEquals(MpegFrame.EMPHASIS_NONE, mp3File.emphasis)
        Assert.assertTrue(mp3File.isOriginal)
        TestCase.assertFalse(mp3File.isCopyright)
        Assert.assertEquals(128, mp3File.xingBitrate)
        Assert.assertEquals(125, mp3File.getBitrate().toLong())
        Assert.assertEquals(1, mp3File.getBitrates()[224]!!.value.toLong())
        Assert.assertEquals(1, mp3File.getBitrates()[112]!!.value.toLong())
        Assert.assertEquals(2, mp3File.getBitrates()[96]!!.value.toLong())
        Assert.assertEquals(1, mp3File.getBitrates()[192]!!.value.toLong())
        Assert.assertEquals(1, mp3File.getBitrates()[32]!!.value.toLong())
        Assert.assertEquals(156, mp3File.lengthInMilliseconds)
        return mp3File
    }

    private inner class Mp3FileForTesting : Mp3File {
        var preScanResult: Int

        constructor(filename: String?) {
            val file = Files.newByteChannel(Paths.get(filename), StandardOpenOption.READ)
            preScanResult = preScanFile(file)
        }

        constructor(filename: File) {
            val file = Files.newByteChannel(filename.toPath(), StandardOpenOption.READ)
            preScanResult = preScanFile(file)
        }
    }

    companion object {
        private val fs = File.separator
        private val MP3_WITH_NO_TAGS = "src" + fs + "test" + fs + "resources" + fs + "notags.mp3"
        private val MP3_WITH_ID3V1_AND_ID3V23_TAGS =
            "src" + fs + "test" + fs + "resources" + fs + "v1andv23tags.mp3"
        private val MP3_WITH_DUMMY_START_AND_END_FRAMES =
            "src" + fs + "test" + fs + "resources" + fs + "dummyframes.mp3"
        private val MP3_WITH_ID3V1_AND_ID3V23_AND_CUSTOM_TAGS =
            "src" + fs + "test" + fs + "resources" + fs + "v1andv23andcustomtags.mp3"
        private val MP3_WITH_ID3V23_UNICODE_TAGS =
            "src" + fs + "test" + fs + "resources" + fs + "v23unicodetags.mp3"
        private val NOT_AN_MP3 = "src" + fs + "test" + fs + "resources" + fs + "notanmp3.mp3"
        private val MP3_WITH_INCOMPLETE_MPEG_FRAME =
            "src" + fs + "test" + fs + "resources" + fs + "incompletempegframe.mp3"
    }
}