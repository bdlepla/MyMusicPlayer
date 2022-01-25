package com.mpatric.mp3agic

class ID3Wrapper(id3v1Tag: ID3v1?, id3v2Tag: ID3v2?) {
    private val id3v1Tag: ID3v1?
    private val id3v2Tag: ID3v2?
    fun getId3v1Tag(): ID3v1? {
        return id3v1Tag
    }

    fun getId3v2Tag(): ID3v2? {
        return id3v2Tag
    }

    var track: String?
        get() = if (id3v2Tag != null && id3v2Tag.track != null && id3v2Tag.track!!.length > 0) {
            id3v2Tag.track
        } else if (id3v1Tag != null) {
            id3v1Tag.track
        } else {
            null
        }
        set(track) {
            if (id3v2Tag != null) {
                id3v2Tag.track = track
            }
            if (id3v1Tag != null) {
                id3v1Tag.track = track
            }
        }
    var artist: String?
        get() = if (id3v2Tag != null && id3v2Tag.artist != null && id3v2Tag.artist!!.length > 0) {
            id3v2Tag.artist
        } else if (id3v1Tag != null) {
            id3v1Tag.artist
        } else {
            null
        }
        set(artist) {
            if (id3v2Tag != null) {
                id3v2Tag.artist = artist
            }
            if (id3v1Tag != null) {
                id3v1Tag.artist = artist
            }
        }
    var title: String?
        get() = if (id3v2Tag != null && id3v2Tag.title != null && id3v2Tag.title!!.length > 0) {
            id3v2Tag.title
        } else if (id3v1Tag != null) {
            id3v1Tag.title
        } else {
            null
        }
        set(title) {
            if (id3v2Tag != null) {
                id3v2Tag.title = title
            }
            if (id3v1Tag != null) {
                id3v1Tag.title = title
            }
        }
    var album: String?
        get() = if (id3v2Tag != null && id3v2Tag.album != null && id3v2Tag.album!!.length > 0) {
            id3v2Tag.album
        } else if (id3v1Tag != null) {
            id3v1Tag.album
        } else {
            null
        }
        set(album) {
            if (id3v2Tag != null) {
                id3v2Tag.album = album
            }
            if (id3v1Tag != null) {
                id3v1Tag.album = album
            }
        }
    var year: String?
        get() = if (id3v2Tag != null && id3v2Tag.year != null && id3v2Tag.year!!.length > 0) {
            id3v2Tag.year
        } else if (id3v1Tag != null) {
            id3v1Tag.year
        } else {
            null
        }
        set(year) {
            if (id3v2Tag != null) {
                id3v2Tag.year = year
            }
            if (id3v1Tag != null) {
                id3v1Tag.year = year
            }
        }
    var genre: Int
        get() = if (id3v2Tag != null && id3v2Tag.genre != -1) {
            id3v2Tag.genre
        } else if (id3v1Tag != null) {
            id3v1Tag.genre
        } else {
            -1
        }
        set(genre) {
            if (id3v2Tag != null) {
                id3v2Tag.genre = genre
            }
            if (id3v1Tag != null) {
                id3v1Tag.genre = genre
            }
        }
    val genreDescription: String?
        get() = if (id3v2Tag != null) {
            id3v2Tag.genreDescription
        } else if (id3v1Tag != null) {
            id3v1Tag.genreDescription
        } else {
            null
        }
    var comment: String?
        get() = if (id3v2Tag != null && id3v2Tag.comment != null && id3v2Tag.comment!!.length > 0) {
            id3v2Tag.comment
        } else if (id3v1Tag != null) {
            id3v1Tag.comment
        } else {
            null
        }
        set(comment) {
            if (id3v2Tag != null) {
                id3v2Tag.comment = comment
            }
            if (id3v1Tag != null) {
                id3v1Tag.comment = comment
            }
        }
    var composer: String?
        get() = if (id3v2Tag != null) {
            id3v2Tag.composer
        } else {
            null
        }
        set(composer) {
            if (id3v2Tag != null) {
                id3v2Tag.composer = composer
            }
        }
    var originalArtist: String?
        get() = if (id3v2Tag != null) {
            id3v2Tag.originalArtist
        } else {
            null
        }
        set(originalArtist) {
            if (id3v2Tag != null) {
                id3v2Tag.originalArtist = originalArtist
            }
        }
    var albumArtist: String?
        get() = if (id3v2Tag != null) {
            id3v2Tag.albumArtist
        } else {
            null
        }
        set(albumArtist) {
            if (id3v2Tag != null) {
                id3v2Tag.albumArtist = albumArtist
            }
        }
    var copyright: String?
        get() = if (id3v2Tag != null) {
            id3v2Tag.copyright
        } else {
            null
        }
        set(copyright) {
            if (id3v2Tag != null) {
                id3v2Tag.copyright = copyright
            }
        }
    var url: String?
        get() = if (id3v2Tag != null) {
            id3v2Tag.url
        } else {
            null
        }
        set(url) {
            if (id3v2Tag != null) {
                id3v2Tag.url = url
            }
        }
    var encoder: String?
        get() = if (id3v2Tag != null) {
            id3v2Tag.encoder
        } else {
            null
        }
        set(encoder) {
            if (id3v2Tag != null) {
                id3v2Tag.encoder = encoder
            }
        }
    val albumImage: ByteArray?
        get() = if (id3v2Tag != null) {
            id3v2Tag.albumImage
        } else {
            null
        }

    fun setAlbumImage(albumImage: ByteArray?, mimeType: String?) {
        if (id3v2Tag != null) {
            id3v2Tag.setAlbumImage(albumImage, mimeType)
        }
    }

    val albumImageMimeType: String?
        get() = if (id3v2Tag != null) {
            id3v2Tag.albumImageMimeType
        } else {
            null
        }
    var lyrics: String?
        get() = if (id3v2Tag != null) {
            id3v2Tag.lyrics
        } else {
            null
        }
        set(lyrics) {
            if (id3v2Tag != null) {
                id3v2Tag.lyrics = lyrics
            }
        }

    fun clearComment() {
        if (id3v2Tag != null) {
            id3v2Tag.clearFrameSet(AbstractID3v2Tag.ID_COMMENT)
        }
        if (id3v1Tag != null) {
            id3v1Tag.comment = null
        }
    }

    fun clearCopyright() {
        if (id3v2Tag != null) {
            id3v2Tag.clearFrameSet(AbstractID3v2Tag.ID_COPYRIGHT)
        }
    }

    fun clearEncoder() {
        if (id3v2Tag != null) {
            id3v2Tag.clearFrameSet(AbstractID3v2Tag.ID_ENCODER)
        }
    }

    init {
        this.id3v1Tag = id3v1Tag
        this.id3v2Tag = id3v2Tag
    }
}