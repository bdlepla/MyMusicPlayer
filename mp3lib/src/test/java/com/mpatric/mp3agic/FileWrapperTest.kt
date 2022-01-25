package com.mpatric.mp3agic

import org.junit.Assert
import org.junit.Test
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.file.InvalidPathException
import java.nio.file.Path
import java.nio.file.Paths

class FileWrapperTest {
    @Test
    @Throws(IOException::class)
    fun shouldReadValidFilename() {
        val fileWrapper = FileWrapper(VALID_FILENAME)
        println(fileWrapper.filename)
        println(VALID_FILENAME)
        Assert.assertEquals(fileWrapper.filename, VALID_FILENAME)
        Assert.assertTrue(fileWrapper.lastModified > 0)
        Assert.assertEquals(fileWrapper.length, VALID_FILE_LENGTH)
    }

    @Test
    @Throws(IOException::class)
    fun shouldReadValidFile() {
        val fileWrapper = FileWrapper(File(VALID_FILENAME))
        println(fileWrapper.filename)
        println(VALID_FILENAME)
        Assert.assertEquals(fileWrapper.filename, VALID_FILENAME)
        Assert.assertTrue(fileWrapper.lastModified > 0)
        Assert.assertEquals(fileWrapper.length, VALID_FILE_LENGTH)
    }

    @Test
    @Throws(IOException::class)
    fun shouldReadValidPath() {
        val fileWrapper = FileWrapper(Paths.get(VALID_FILENAME))
        println(fileWrapper.filename)
        println(VALID_FILENAME)
        Assert.assertEquals(fileWrapper.filename, VALID_FILENAME)
        Assert.assertTrue(fileWrapper.lastModified > 0)
        Assert.assertEquals(fileWrapper.length, VALID_FILE_LENGTH)
    }

    @Test(expected = FileNotFoundException::class)
    @Throws(IOException::class)
    fun shouldFailForNonExistentFile() {
        FileWrapper(NON_EXISTENT_FILENAME)
    }

    @Test(expected = InvalidPathException::class)
    @Throws(IOException::class)
    fun shouldFailForMalformedFilename() {
        FileWrapper(MALFORMED_FILENAME)
    }

    @Test(expected = NullPointerException::class)
    @Throws(IOException::class)
    fun shouldFailForNullFilename() {
        FileWrapper(null as String?)
    }

    @Test(expected = NullPointerException::class)
    @Throws(IOException::class)
    fun shouldFailForNullFilenameFile() {
        FileWrapper(null as File?)
    }

    @Test(expected = NullPointerException::class)
    @Throws(IOException::class)
    fun shouldFailForNullPath() {
        FileWrapper(null as Path?)
    }

    companion object {
        private val fs = File.separator
        private val VALID_FILENAME = "src" + fs + "test" + fs + "resources" + fs + "notags.mp3"
        private const val VALID_FILE_LENGTH: Long = 2869
        private const val NON_EXISTENT_FILENAME = "just-not.there"
        private const val MALFORMED_FILENAME = "malformed.\u0000"
    }
}