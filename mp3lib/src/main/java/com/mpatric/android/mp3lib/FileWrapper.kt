package com.mpatric.mp3agic

import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.lang.NullPointerException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.TimeUnit
import kotlin.Throws

open class FileWrapper {
    protected var path: Path? = null
    var length: Long = 0
        protected set
    var lastModified: Long = 0
        protected set

    protected constructor() {}
    constructor(filename: String?) {
        path = Paths.get(filename)
        init()
    }

    constructor(file: File?) {
        if (file == null) throw NullPointerException()
        path = Paths.get(file.path)
        init()
    }

    constructor(path: Path?) {
        if (path == null) throw NullPointerException()
        this.path = path
        init()
    }

    @Throws(IOException::class)
    private fun init() {
        if (!Files.exists(path)) throw FileNotFoundException("File not found $path")
        if (!Files.isReadable(path)) throw IOException("File not readable")
        length = Files.size(path)
        lastModified = Files.getLastModifiedTime(path).to(TimeUnit.MILLISECONDS)
    }

    val filename: String
        get() = path.toString()
}