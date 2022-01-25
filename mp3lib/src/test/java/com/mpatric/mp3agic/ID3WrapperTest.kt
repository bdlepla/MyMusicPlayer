package com.mpatric.mp3agic

import org.junit.Assert
import org.junit.Test
import java.util.*

class ID3WrapperTest {
    //region getId3v1Tag
    @Test
    fun returnsV1Tag() {
        val id3v1Tag: ID3v1 = ID3v1TagForTesting()
        val id3v2Tag: ID3v2 = ID3v2TagForTesting()
        val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
        Assert.assertEquals(id3v1Tag, wrapper.getId3v1Tag())
    }

    @Test
    fun returnsNullV1Tag() {
        val id3v2Tag: ID3v2 = ID3v2TagForTesting()
        val wrapper = ID3Wrapper(null, id3v2Tag)
        Assert.assertEquals(null, wrapper.getId3v1Tag())
    }

    //endregion
    //region getId3v2Tag
    @Test
    fun returnsV2Tag() {
        val id3v1Tag: ID3v1 = ID3v1TagForTesting()
        val id3v2Tag: ID3v2 = ID3v2TagForTesting()
        val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
        Assert.assertEquals(id3v2Tag, wrapper.getId3v2Tag())
    }

    @Test
    fun returnsNullV2Tag() {
        val id3v1Tag: ID3v1 = ID3v1TagForTesting()
        val wrapper = ID3Wrapper(id3v1Tag, null)
        Assert.assertEquals(null, wrapper.getId3v2Tag())
    }

    //endregion
    //region getTrack
    @Test
    fun trackReturnsV2TagsTrackBeforeV1TagsTrack() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            id3v1Tag.track = "V1 Track"
            val id3v2Tag: ID3v2 = ID3v2TagForTesting()
            id3v2Tag.track = "V2 Track"
            val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
            Assert.assertEquals("V2 Track", wrapper.track)
        }

    @Test
    fun trackReturnsV1TagsTrackIfV2TagsTrackIsEmpty() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            id3v1Tag.track = "V1 Track"
            val id3v2Tag: ID3v2 = ID3v2TagForTesting()
            id3v2Tag.track = ""
            val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
            Assert.assertEquals("V1 Track", wrapper.track)
        }

    @Test
    fun trackReturnsV1TagsTrackIfV2TagsTrackDoesNotExist() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            id3v1Tag.track = "V1 Track"
            val id3v2Tag: ID3v2 = ID3v2TagForTesting()
            id3v2Tag.track = null
            val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
            Assert.assertEquals("V1 Track", wrapper.track)
        }

    @Test
    fun trackReturnsV1TagsTrackIfV2TagDoesNotExist() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            id3v1Tag.track = "V1 Track"
            val wrapper = ID3Wrapper(id3v1Tag, null)
            Assert.assertEquals("V1 Track", wrapper.track)
        }

    @Test
    fun trackReturnsNullIfBothTagsDoNotExist() {
            val wrapper = ID3Wrapper(null, null)
            Assert.assertNull(wrapper.track)
        }

    //endregion
    //region setTrack
    @Test
    fun setsTrackOnBothV1AndV2Tags() {
        val id3v1Tag: ID3v1 = ID3v1TagForTesting()
        val id3v2Tag: ID3v2 = ID3v2TagForTesting()
        val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
        wrapper.track = "a track"
        Assert.assertEquals("a track", id3v1Tag.track)
        Assert.assertEquals("a track", id3v2Tag.track)
    }

    @Test
    fun setsTrackOnV1TagOnly() {
        val id3v1Tag: ID3v1 = ID3v1TagForTesting()
        val wrapper = ID3Wrapper(id3v1Tag, null)
        wrapper.track = "a track"
        Assert.assertEquals("a track", id3v1Tag.track)
    }

    @Test
    fun setsTrackOnV2TagOnly() {
        val id3v2Tag: ID3v2 = ID3v2TagForTesting()
        val wrapper = ID3Wrapper(null, id3v2Tag)
        wrapper.track = "a track"
        Assert.assertEquals("a track", id3v2Tag.track)
    }

    @Test
    fun setTrackDoesNotThrowExceptionWhenBothTagsDoNotExist() {
        val wrapper = ID3Wrapper(null, null)
        wrapper.track = "a track"
    }

    //endregion
    //region getArtist
    @Test
    fun artistReturnsV2TagsArtistBeforeV1TagsArtist() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            id3v1Tag.artist = "V1 Artist"
            val id3v2Tag: ID3v2 = ID3v2TagForTesting()
            id3v2Tag.artist = "V2 Artist"
            val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
            Assert.assertEquals("V2 Artist", wrapper.artist)
        }

    @Test
    fun artistReturnsV1TagsArtistIfV2TagsArtistIsEmpty() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            id3v1Tag.artist = "V1 Artist"
            val id3v2Tag: ID3v2 = ID3v2TagForTesting()
            id3v2Tag.artist = ""
            val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
            Assert.assertEquals("V1 Artist", wrapper.artist)
        }

    @Test
    fun artistReturnsV1TagsArtistIfV2TagsArtistDoesNotExist() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            id3v1Tag.artist = "V1 Artist"
            val id3v2Tag: ID3v2 = ID3v2TagForTesting()
            id3v2Tag.artist = null
            val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
            Assert.assertEquals("V1 Artist", wrapper.artist)
        }

    @Test
    fun artistReturnsV1TagsArtistIfV2TagDoesNotExist() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            id3v1Tag.artist = "V1 Artist"
            val wrapper = ID3Wrapper(id3v1Tag, null)
            Assert.assertEquals("V1 Artist", wrapper.artist)
        }

    @Test
    fun artistReturnsNullIfBothTagsDoNotExist() {
            val wrapper = ID3Wrapper(null, null)
            Assert.assertNull(wrapper.artist)
        }

    //endregion
    //region setArtist
    @Test
    fun setsArtistOnBothV1AndV2Tags() {
        val id3v1Tag: ID3v1 = ID3v1TagForTesting()
        val id3v2Tag: ID3v2 = ID3v2TagForTesting()
        val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
        wrapper.artist = "an artist"
        Assert.assertEquals("an artist", id3v1Tag.artist)
        Assert.assertEquals("an artist", id3v2Tag.artist)
    }

    @Test
    fun setsArtistOnV1TagOnly() {
        val id3v1Tag: ID3v1 = ID3v1TagForTesting()
        val wrapper = ID3Wrapper(id3v1Tag, null)
        wrapper.artist = "an artist"
        Assert.assertEquals("an artist", id3v1Tag.artist)
    }

    @Test
    fun setsArtistOnV2TagOnly() {
        val id3v2Tag: ID3v2 = ID3v2TagForTesting()
        val wrapper = ID3Wrapper(null, id3v2Tag)
        wrapper.artist = "an artist"
        Assert.assertEquals("an artist", id3v2Tag.artist)
    }

    @Test
    fun setArtistDoesNotThrowExceptionWhenBothTagsDoNotExist() {
        val wrapper = ID3Wrapper(null, null)
        wrapper.artist = "an artist"
    }

    //endregion
    //region getTitle
    @Test
    fun titleReturnsV2TagsTitleBeforeV1TagsTitle() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            id3v1Tag.title = "V1 Title"
            val id3v2Tag: ID3v2 = ID3v2TagForTesting()
            id3v2Tag.title = "V2 Title"
            val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
            Assert.assertEquals("V2 Title", wrapper.title)
        }

    @Test
    fun titleReturnsV1TagsTitleIfV2TagsTitleIsEmpty() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            id3v1Tag.title = "V1 Title"
            val id3v2Tag: ID3v2 = ID3v2TagForTesting()
            id3v2Tag.title = ""
            val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
            Assert.assertEquals("V1 Title", wrapper.title)
        }

    @Test
    fun titleReturnsV1TagsTitleIfV2TagsTitleDoesNotExist() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            id3v1Tag.title = "V1 Title"
            val id3v2Tag: ID3v2 = ID3v2TagForTesting()
            id3v2Tag.title = null
            val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
            Assert.assertEquals("V1 Title", wrapper.title)
        }

    @Test
    fun titleReturnsV1TagsTitleIfV2TagDoesNotExist() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            id3v1Tag.title = "V1 Title"
            val wrapper = ID3Wrapper(id3v1Tag, null)
            Assert.assertEquals("V1 Title", wrapper.title)
        }

    @Test
    fun titleReturnsNullIfBothTagsDoNotExist() {
            val wrapper = ID3Wrapper(null, null)
            Assert.assertNull(wrapper.title)
        }

    //endregion
    //region setTitle
    @Test
    fun setsTitleOnBothV1AndV2Tags() {
        val id3v1Tag: ID3v1 = ID3v1TagForTesting()
        val id3v2Tag: ID3v2 = ID3v2TagForTesting()
        val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
        wrapper.title = "a title"
        Assert.assertEquals("a title", id3v1Tag.title)
        Assert.assertEquals("a title", id3v2Tag.title)
    }

    @Test
    fun setsTitleOnV1TagOnly() {
        val id3v1Tag: ID3v1 = ID3v1TagForTesting()
        val wrapper = ID3Wrapper(id3v1Tag, null)
        wrapper.title = "a title"
        Assert.assertEquals("a title", id3v1Tag.title)
    }

    @Test
    fun setsTitleOnV2TagOnly() {
        val id3v2Tag: ID3v2 = ID3v2TagForTesting()
        val wrapper = ID3Wrapper(null, id3v2Tag)
        wrapper.title = "a title"
        Assert.assertEquals("a title", id3v2Tag.title)
    }

    @Test
    fun setTitleDoesNotThrowExceptionWhenBothTagsDoNotExist() {
        val wrapper = ID3Wrapper(null, null)
        wrapper.title = "a title"
    }

    //endregion
    //region getAlbum
    @Test
    fun albumReturnsV2TagsAlbumBeforeV1TagsAlbum() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            id3v1Tag.album = "V1 Album"
            val id3v2Tag: ID3v2 = ID3v2TagForTesting()
            id3v2Tag.album = "V2 Album"
            val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
            Assert.assertEquals("V2 Album", wrapper.album)
        }

    @Test
    fun albumReturnsV1TagsAlbumIfV2TagsAlbumIsEmpty() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            id3v1Tag.album = "V1 Album"
            val id3v2Tag: ID3v2 = ID3v2TagForTesting()
            id3v2Tag.album = ""
            val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
            Assert.assertEquals("V1 Album", wrapper.album)
        }

    @Test
    fun albumReturnsV1TagsAlbumIfV2TagsAlbumDoesNotExist() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            id3v1Tag.album = "V1 Album"
            val id3v2Tag: ID3v2 = ID3v2TagForTesting()
            id3v2Tag.album = null
            val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
            Assert.assertEquals("V1 Album", wrapper.album)
        }

    @Test
    fun albumReturnsV1TagsAlbumIfV2TagDoesNotExist() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            id3v1Tag.album = "V1 Album"
            val wrapper = ID3Wrapper(id3v1Tag, null)
            Assert.assertEquals("V1 Album", wrapper.album)
        }

    @Test
    fun albumReturnsNullIfBothTagsDoNotExist() {
            val wrapper = ID3Wrapper(null, null)
            Assert.assertNull(wrapper.album)
        }

    //endregion
    //region setAlbum
    @Test
    fun setsAlbumOnBothV1AndV2Tags() {
        val id3v1Tag: ID3v1 = ID3v1TagForTesting()
        val id3v2Tag: ID3v2 = ID3v2TagForTesting()
        val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
        wrapper.album = "an album"
        Assert.assertEquals("an album", id3v1Tag.album)
        Assert.assertEquals("an album", id3v2Tag.album)
    }

    @Test
    fun setsAlbumOnV1TagOnly() {
        val id3v1Tag: ID3v1 = ID3v1TagForTesting()
        val wrapper = ID3Wrapper(id3v1Tag, null)
        wrapper.album = "an album"
        Assert.assertEquals("an album", id3v1Tag.album)
    }

    @Test
    fun setsAlbumOnV2TagOnly() {
        val id3v2Tag: ID3v2 = ID3v2TagForTesting()
        val wrapper = ID3Wrapper(null, id3v2Tag)
        wrapper.album = "an album"
        Assert.assertEquals("an album", id3v2Tag.album)
    }

    @Test
    fun setAlbumDoesNotThrowExceptionWhenBothTagsDoNotExist() {
        val wrapper = ID3Wrapper(null, null)
        wrapper.album = "an album"
    }

    //endregion
    //region getYear
    @Test
    fun yearReturnsV2TagsYearBeforeV1TagsYear() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            id3v1Tag.year = "V1 Year"
            val id3v2Tag: ID3v2 = ID3v2TagForTesting()
            id3v2Tag.year = "V2 Year"
            val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
            Assert.assertEquals("V2 Year", wrapper.year)
        }

    @Test
    fun yearReturnsV1TagsYearIfV2TagsYearIsEmpty() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            id3v1Tag.year = "V1 Year"
            val id3v2Tag: ID3v2 = ID3v2TagForTesting()
            id3v2Tag.year = ""
            val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
            Assert.assertEquals("V1 Year", wrapper.year)
        }

    @Test
    fun yearReturnsV1TagsYearIfV2TagsYearDoesNotExist() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            id3v1Tag.year = "V1 Year"
            val id3v2Tag: ID3v2 = ID3v2TagForTesting()
            id3v2Tag.year = null
            val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
            Assert.assertEquals("V1 Year", wrapper.year)
        }

    @Test
    fun yearReturnsV1TagsYearIfV2TagDoesNotExist() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            id3v1Tag.year = "V1 Year"
            val wrapper = ID3Wrapper(id3v1Tag, null)
            Assert.assertEquals("V1 Year", wrapper.year)
        }

    @Test
    fun yearReturnsNullIfBothTagsDoNotExist() {
            val wrapper = ID3Wrapper(null, null)
            Assert.assertNull(wrapper.year)
        }

    //endregion
    //region setYear
    @Test
    fun setsYearOnBothV1AndV2Tags() {
        val id3v1Tag: ID3v1 = ID3v1TagForTesting()
        val id3v2Tag: ID3v2 = ID3v2TagForTesting()
        val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
        wrapper.year = "a year"
        Assert.assertEquals("a year", id3v1Tag.year)
        Assert.assertEquals("a year", id3v2Tag.year)
    }

    @Test
    fun setsYearOnV1TagOnly() {
        val id3v1Tag: ID3v1 = ID3v1TagForTesting()
        val wrapper = ID3Wrapper(id3v1Tag, null)
        wrapper.year = "a year"
        Assert.assertEquals("a year", id3v1Tag.year)
    }

    @Test
    fun setsYearOnV2TagOnly() {
        val id3v2Tag: ID3v2 = ID3v2TagForTesting()
        val wrapper = ID3Wrapper(null, id3v2Tag)
        wrapper.year = "a year"
        Assert.assertEquals("a year", id3v2Tag.year)
    }

    @Test
    fun setYearDoesNotThrowExceptionWhenBothTagsDoNotExist() {
        val wrapper = ID3Wrapper(null, null)
        wrapper.year = "a year"
    }

    //endregion
    //region getGenre
    @Test
    fun genreReturnsV2TagsGenreBeforeV1TagsGenre() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            id3v1Tag.genre = 10
            val id3v2Tag: ID3v2 = ID3v2TagForTesting()
            id3v2Tag.genre = 20
            val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
            Assert.assertEquals(20, wrapper.genre.toLong())
        }

    @Test
    fun genreReturnsV1TagsGenreIfV2TagsGenreIsNegativeOne() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            id3v1Tag.genre = 10
            val id3v2Tag: ID3v2 = ID3v2TagForTesting()
            id3v2Tag.genre = -1
            val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
            Assert.assertEquals(10, wrapper.genre.toLong())
        }

    @Test
    fun genreReturnsV1TagsGenreIfV2TagDoesNotExist() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            id3v1Tag.genre = 10
            val wrapper = ID3Wrapper(id3v1Tag, null)
            Assert.assertEquals(10, wrapper.genre.toLong())
        }

    @Test
    fun genreReturnsNegativeOneIfBothTagsDoNotExist() {
            val wrapper = ID3Wrapper(null, null)
            Assert.assertEquals(-1, wrapper.genre.toLong())
        }

    //endregion
    //region setGenre
    @Test
    fun setsGenreOnBothV1AndV2Tags() {
        val id3v1Tag: ID3v1 = ID3v1TagForTesting()
        val id3v2Tag: ID3v2 = ID3v2TagForTesting()
        val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
        wrapper.genre = 22
        Assert.assertEquals(22, id3v1Tag.genre.toLong())
        Assert.assertEquals(22, id3v2Tag.genre.toLong())
    }

    @Test
    fun setsGenreOnV1TagOnly() {
        val id3v1Tag: ID3v1 = ID3v1TagForTesting()
        val wrapper = ID3Wrapper(id3v1Tag, null)
        wrapper.genre = 22
        Assert.assertEquals(22, id3v1Tag.genre.toLong())
    }

    @Test
    fun setsGenreOnV2TagOnly() {
        val id3v2Tag: ID3v2 = ID3v2TagForTesting()
        val wrapper = ID3Wrapper(null, id3v2Tag)
        wrapper.genre = 22
        Assert.assertEquals(22, id3v2Tag.genre.toLong())
    }

    @Test
    fun setGenreDoesNotThrowExceptionWhenBothTagsDoNotExist() {
        val wrapper = ID3Wrapper(null, null)
        wrapper.genre = 22
    }

    //endregion
    //region getGenreDescription
    @Test
    fun genreDescriptionReturnsV2TagsGenreDescriptionBeforeV1TagsGenreDescription() {
            val id3v1Tag = ID3v1TagForTesting()
            id3v1Tag.genreDescription = "V1 GenreDescription"
            val id3v2Tag: ID3v2 = ID3v2TagForTesting()
            id3v2Tag.genreDescription = "V2 GenreDescription"
            val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
            Assert.assertEquals("V2 GenreDescription", wrapper.genreDescription)
        }

    @Test
    fun genreDescriptionReturnsV1TagsGenreDescriptionIfV2TagDoesNotExist() {
            val id3v1Tag = ID3v1TagForTesting()
            id3v1Tag.genreDescription = "V1 GenreDescription"
            val wrapper = ID3Wrapper(id3v1Tag, null)
            Assert.assertEquals("V1 GenreDescription", wrapper.genreDescription)
        }

    @Test
    fun genreDescriptionReturnsNullIfBothTagsDoNotExist() {
            val wrapper = ID3Wrapper(null, null)
            Assert.assertNull(wrapper.genreDescription)
        }

    //endregion
    //region getComment
    @Test
    fun commentReturnsV2TagsCommentBeforeV1TagsComment() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            id3v1Tag.comment = "V1 Comment"
            val id3v2Tag: ID3v2 = ID3v2TagForTesting()
            id3v2Tag.comment = "V2 Comment"
            val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
            Assert.assertEquals("V2 Comment", wrapper.comment)
        }

    @Test
    fun commentReturnsV1TagsCommentIfV2TagsCommentIsEmpty() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            id3v1Tag.comment = "V1 Comment"
            val id3v2Tag: ID3v2 = ID3v2TagForTesting()
            id3v2Tag.comment = ""
            val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
            Assert.assertEquals("V1 Comment", wrapper.comment)
        }

    @Test
    fun commentReturnsV1TagsCommentIfV2TagsCommentDoesNotExist() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            id3v1Tag.comment = "V1 Comment"
            val id3v2Tag: ID3v2 = ID3v2TagForTesting()
            id3v2Tag.comment = null
            val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
            Assert.assertEquals("V1 Comment", wrapper.comment)
        }

    @Test
    fun commentReturnsV1TagsCommentIfV2TagDoesNotExist() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            id3v1Tag.comment = "V1 Comment"
            val wrapper = ID3Wrapper(id3v1Tag, null)
            Assert.assertEquals("V1 Comment", wrapper.comment)
        }

    @Test
    fun commentReturnsNullIfBothTagsDoNotExist() {
            val wrapper = ID3Wrapper(null, null)
            Assert.assertNull(wrapper.comment)
        }

    //endregion
    //region setComment
    @Test
    fun setsCommentOnBothV1AndV2Tags() {
        val id3v1Tag: ID3v1 = ID3v1TagForTesting()
        val id3v2Tag: ID3v2 = ID3v2TagForTesting()
        val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
        wrapper.comment = "a comment"
        Assert.assertEquals("a comment", id3v1Tag.comment)
        Assert.assertEquals("a comment", id3v2Tag.comment)
    }

    @Test
    fun setsCommentOnV1TagOnly() {
        val id3v1Tag: ID3v1 = ID3v1TagForTesting()
        val wrapper = ID3Wrapper(id3v1Tag, null)
        wrapper.comment = "a comment"
        Assert.assertEquals("a comment", id3v1Tag.comment)
    }

    @Test
    fun setsCommentOnV2TagOnly() {
        val id3v2Tag: ID3v2 = ID3v2TagForTesting()
        val wrapper = ID3Wrapper(null, id3v2Tag)
        wrapper.comment = "a comment"
        Assert.assertEquals("a comment", id3v2Tag.comment)
    }

    @Test
    fun setCommentDoesNotThrowExceptionWhenBothTagsDoNotExist() {
        val wrapper = ID3Wrapper(null, null)
        wrapper.comment = "a comment"
    }

    //endregion
    //region getComposer
    @Test
    fun composerReturnsV2TagsComposer() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            val id3v2Tag: ID3v2 = ID3v2TagForTesting()
            id3v2Tag.composer = "V2 Composer"
            val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
            Assert.assertEquals("V2 Composer", wrapper.composer)
        }

    @Test
    fun composerReturnsNullIfV2TagDoesNotExist() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            val wrapper = ID3Wrapper(id3v1Tag, null)
            Assert.assertNull(wrapper.composer)
        }

    //endregion
    //region setComposer
    @Test
    fun setsComposerOnV2Tag() {
        val id3v1Tag: ID3v1 = ID3v1TagForTesting()
        val id3v2Tag: ID3v2 = ID3v2TagForTesting()
        val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
        wrapper.composer = "a composer"
        Assert.assertEquals("a composer", id3v2Tag.composer)
    }

    @Test
    fun setComposerDoesNotThrowExceptionWhenV2TagDoesNotExist() {
        val id3v1Tag: ID3v1 = ID3v1TagForTesting()
        val wrapper = ID3Wrapper(id3v1Tag, null)
        wrapper.composer = "a composer"
    }

    //endregion
    //region getOriginalArtist
    @Test
    fun originalArtistReturnsV2TagsOriginalArtist() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            val id3v2Tag: ID3v2 = ID3v2TagForTesting()
            id3v2Tag.originalArtist = "V2 OriginalArtist"
            val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
            Assert.assertEquals("V2 OriginalArtist", wrapper.originalArtist)
        }

    @Test
    fun originalArtistReturnsNullIfV2TagDoesNotExist() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            val wrapper = ID3Wrapper(id3v1Tag, null)
            Assert.assertNull(wrapper.originalArtist)
        }

    //endregion
    //region setOriginalArtist
    @Test
    fun setsOriginalArtistOnV2Tag() {
        val id3v1Tag: ID3v1 = ID3v1TagForTesting()
        val id3v2Tag: ID3v2 = ID3v2TagForTesting()
        val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
        wrapper.originalArtist = "an original artist"
        Assert.assertEquals("an original artist", id3v2Tag.originalArtist)
    }

    @Test
    fun setOriginalArtistDoesNotThrowExceptionWhenV2TagDoesNotExist() {
        val id3v1Tag: ID3v1 = ID3v1TagForTesting()
        val wrapper = ID3Wrapper(id3v1Tag, null)
        wrapper.originalArtist = "an original artist"
    }

    //endregion
    //region getAlbumArtist
    @Test
    fun albumArtistReturnsV2TagsAlbumArtist() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            val id3v2Tag: ID3v2 = ID3v2TagForTesting()
            id3v2Tag.albumArtist = "V2 AlbumArtist"
            val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
            Assert.assertEquals("V2 AlbumArtist", wrapper.albumArtist)
        }

    @Test
    fun albumArtistReturnsNullIfV2TagDoesNotExist() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            val wrapper = ID3Wrapper(id3v1Tag, null)
            Assert.assertNull(wrapper.albumArtist)
        }

    //endregion
    //region setAlbumArtist
    @Test
    fun setsAlbumArtistOnV2Tag() {
        val id3v1Tag: ID3v1 = ID3v1TagForTesting()
        val id3v2Tag: ID3v2 = ID3v2TagForTesting()
        val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
        wrapper.albumArtist = "an album artist"
        Assert.assertEquals("an album artist", id3v2Tag.albumArtist)
    }

    @Test
    fun setAlbumArtistDoesNotThrowExceptionWhenV2TagDoesNotExist() {
        val id3v1Tag: ID3v1 = ID3v1TagForTesting()
        val wrapper = ID3Wrapper(id3v1Tag, null)
        wrapper.albumArtist = "an album artist"
    }

    //endregion
    //region getCopyright
    @Test
    fun copyrightReturnsV2TagsCopyright() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            val id3v2Tag: ID3v2 = ID3v2TagForTesting()
            id3v2Tag.copyright = "V2 Copyright"
            val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
            Assert.assertEquals("V2 Copyright", wrapper.copyright)
        }

    @Test
    fun copyrightReturnsNullIfV2TagDoesNotExist() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            val wrapper = ID3Wrapper(id3v1Tag, null)
            Assert.assertNull(wrapper.copyright)
        }

    //endregion
    //region setCopyright
    @Test
    fun setsCopyrightOnV2Tag() {
        val id3v1Tag: ID3v1 = ID3v1TagForTesting()
        val id3v2Tag: ID3v2 = ID3v2TagForTesting()
        val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
        wrapper.copyright = "a copyright"
        Assert.assertEquals("a copyright", id3v2Tag.copyright)
    }

    @Test
    fun setCopyrightDoesNotThrowExceptionWhenV2TagDoesNotExist() {
        val id3v1Tag: ID3v1 = ID3v1TagForTesting()
        val wrapper = ID3Wrapper(id3v1Tag, null)
        wrapper.copyright = "a copyright"
    }

    //endregion
    //region getUrl
    @Test
    fun urlReturnsV2TagsUrl() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            val id3v2Tag: ID3v2 = ID3v2TagForTesting()
            id3v2Tag.url = "V2 Url"
            val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
            Assert.assertEquals("V2 Url", wrapper.url)
        }

    @Test
    fun urlReturnsNullIfV2TagDoesNotExist() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            val wrapper = ID3Wrapper(id3v1Tag, null)
            Assert.assertNull(wrapper.url)
        }

    //endregion
    //region setUrl
    @Test
    fun setsUrlOnV2Tag() {
        val id3v1Tag: ID3v1 = ID3v1TagForTesting()
        val id3v2Tag: ID3v2 = ID3v2TagForTesting()
        val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
        wrapper.url = "a url"
        Assert.assertEquals("a url", id3v2Tag.url)
    }

    @Test
    fun setUrlDoesNotThrowExceptionWhenV2TagDoesNotExist() {
        val id3v1Tag: ID3v1 = ID3v1TagForTesting()
        val wrapper = ID3Wrapper(id3v1Tag, null)
        wrapper.url = "a url"
    }

    //endregion
    //region getEncoder
    @Test
    fun encoderReturnsV2TagsEncoder() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            val id3v2Tag: ID3v2 = ID3v2TagForTesting()
            id3v2Tag.encoder = "V2 Encoder"
            val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
            Assert.assertEquals("V2 Encoder", wrapper.encoder)
        }

    @Test
    fun encoderReturnsNullIfV2TagDoesNotExist() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            val wrapper = ID3Wrapper(id3v1Tag, null)
            Assert.assertNull(wrapper.encoder)
        }

    //endregion
    //region setEncoder
    @Test
    fun setsEncoderOnV2Tag() {
        val id3v1Tag: ID3v1 = ID3v1TagForTesting()
        val id3v2Tag: ID3v2 = ID3v2TagForTesting()
        val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
        wrapper.encoder = "an encoder"
        Assert.assertEquals("an encoder", id3v2Tag.encoder)
    }

    @Test
    fun setEncoderDoesNotThrowExceptionWhenV2TagDoesNotExist() {
        val id3v1Tag: ID3v1 = ID3v1TagForTesting()
        val wrapper = ID3Wrapper(id3v1Tag, null)
        wrapper.encoder = "an encoder"
    }

    //endregion
    //region getAlbumImage and getAlbumImageMimeType
    @Test
    fun albumImageReturnsV2TagsAlbumImage() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            val id3v2Tag: ID3v2 = ID3v2TagForTesting()
            id3v2Tag.setAlbumImage(byteArrayOf(12, 4, 7), "mime type")
            val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
            Assert.assertArrayEquals(byteArrayOf(12, 4, 7), wrapper.albumImage)
            Assert.assertEquals("mime type", wrapper.albumImageMimeType)
        }

    @Test
    fun albumImageReturnsNullIfV2TagDoesNotExist() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            val wrapper = ID3Wrapper(id3v1Tag, null)
            Assert.assertNull(wrapper.albumImage)
            Assert.assertNull(wrapper.albumImageMimeType)
        }

    //endregion
    //region setAlbumImage
    @Test
    fun setsAlbumImageOnV2Tag() {
        val id3v1Tag: ID3v1 = ID3v1TagForTesting()
        val id3v2Tag: ID3v2 = ID3v2TagForTesting()
        val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
        wrapper.setAlbumImage(byteArrayOf(12, 4, 7), "mime type")
        Assert.assertArrayEquals(byteArrayOf(12, 4, 7), id3v2Tag.albumImage)
        Assert.assertEquals("mime type", id3v2Tag.albumImageMimeType)
    }

    @Test
    fun setAlbumImageDoesNotThrowExceptionWhenV2TagDoesNotExist() {
        val id3v1Tag: ID3v1 = ID3v1TagForTesting()
        val wrapper = ID3Wrapper(id3v1Tag, null)
        wrapper.setAlbumImage(byteArrayOf(12, 4, 7), "mime type")
    }

    //endregion
    //region getLyrics
    @Test
    fun lyricsReturnsV2TagsLyrics() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            val id3v2Tag: ID3v2 = ID3v2TagForTesting()
            id3v2Tag.lyrics = "V2 Lyrics"
            val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
            Assert.assertEquals("V2 Lyrics", wrapper.lyrics)
        }

    @Test
    fun lyricsReturnsNullIfV2TagDoesNotExist() {
            val id3v1Tag: ID3v1 = ID3v1TagForTesting()
            val wrapper = ID3Wrapper(id3v1Tag, null)
            Assert.assertNull(wrapper.lyrics)
        }

    //endregion
    //region setLyrics
    @Test
    fun setsLyricsOnV2Tag() {
        val id3v1Tag: ID3v1 = ID3v1TagForTesting()
        val id3v2Tag: ID3v2 = ID3v2TagForTesting()
        val wrapper = ID3Wrapper(id3v1Tag, id3v2Tag)
        wrapper.lyrics = "lyrics"
        Assert.assertEquals("lyrics", id3v2Tag.lyrics)
    }

    @Test
    fun setLyricsDoesNotThrowExceptionWhenV2TagDoesNotExist() {
        val id3v1Tag: ID3v1 = ID3v1TagForTesting()
        val wrapper = ID3Wrapper(id3v1Tag, null)
        wrapper.lyrics = "lyrics"
    }

    //endregion
    //region clearComment
    @Test
    fun clearsCommentOnV1Tag() {
        val id3v1Tag: ID3v1 = ID3v1TagForTesting()
        id3v1Tag.comment = "a comment"
        val wrapper = ID3Wrapper(id3v1Tag, null)
        wrapper.clearComment()
        Assert.assertNull(id3v1Tag.comment)
    }

    @Test
    fun clearsCommentFrameOnV2Tag() {
        val id3v2Tag = ID3v2TagForTesting()
        id3v2Tag.addFrameSet(
            AbstractID3v2Tag.ID_COMMENT,
            ID3v2FrameSet(AbstractID3v2Tag.ID_COMMENT)
        )
        Assert.assertTrue(id3v2Tag.frameSets.containsKey(AbstractID3v2Tag.ID_COMMENT))
        val wrapper = ID3Wrapper(null, id3v2Tag)
        wrapper.clearComment()
        Assert.assertFalse(id3v2Tag.frameSets.containsKey(AbstractID3v2Tag.ID_COMMENT))
    }

    //endregion
    //region clearCopyright
    @Test
    fun clearsCopyrightFrameOnV2Tag() {
        val id3v2Tag = ID3v2TagForTesting()
        id3v2Tag.addFrameSet(
            AbstractID3v2Tag.ID_COPYRIGHT,
            ID3v2FrameSet(AbstractID3v2Tag.ID_COPYRIGHT)
        )
        Assert.assertTrue(id3v2Tag.frameSets.containsKey(AbstractID3v2Tag.ID_COPYRIGHT))
        val wrapper = ID3Wrapper(null, id3v2Tag)
        wrapper.clearCopyright()
        Assert.assertFalse(id3v2Tag.frameSets.containsKey(AbstractID3v2Tag.ID_COPYRIGHT))
    }

    @Test
    fun clearCopyrightDoesNotThrowExceptionWhenV2TagDoesNotExist() {
        val wrapper = ID3Wrapper(null, null)
        wrapper.clearCopyright()
    }

    //endregion
    //region clearCopyright
    @Test
    fun clearsEncoderFrameOnV2Tag() {
        val id3v2Tag = ID3v2TagForTesting()
        id3v2Tag.addFrameSet(
            AbstractID3v2Tag.ID_ENCODER,
            ID3v2FrameSet(AbstractID3v2Tag.ID_ENCODER)
        )
        Assert.assertTrue(id3v2Tag.frameSets.containsKey(AbstractID3v2Tag.ID_ENCODER))
        val wrapper = ID3Wrapper(null, id3v2Tag)
        wrapper.clearEncoder()
        Assert.assertFalse(id3v2Tag.frameSets.containsKey(AbstractID3v2Tag.ID_ENCODER))
    }

    @Test
    fun clearEncoderDoesNotThrowExceptionWhenV2TagDoesNotExist() {
        val wrapper = ID3Wrapper(null, null)
        wrapper.clearEncoder()
    }

    //endregion
    //region ID3v1TagForTesting class
    private open class ID3v1TagForTesting : ID3v1 {
        override var track: String? = null
        override var artist: String? = null
        override var title: String? = null
        override var album: String? = null
        override var year: String? = null
        override var genre = 0
        override var genreDescription: String? = null
        override var comment: String? = null
        override val version: String?
            get() = null

        @Throws(NotSupportedException::class)
        override fun toBytes(): ByteArray? {
            return ByteArray(0)
        }
    }

    //endregion
    //region ID3v2TagForTesting class
    private class ID3v2TagForTesting : ID3v1TagForTesting(), ID3v2 {
        override var composer: String? = null
        override var originalArtist: String? = null
        override var albumArtist: String? = null
        override var copyright: String? = null
        override var url: String? = null
        override var encoder: String? = null
        override var albumImage: ByteArray? = null
            private set
        override var albumImageMimeType: String? = null
            private set
        override var lyrics: String? = null
        override val frameSets: MutableMap<String?, ID3v2FrameSet> = HashMap()
        override fun getPadding() = false
        override fun setPadding(value: Boolean){}

        fun hasFooter(): Boolean {
            return false
        }

        private var _footer = false
        override var footer: Boolean
            get() = _footer
            set(footer) {}

        fun hasUnsynchronisation(): Boolean {
            return false
        }

        private var _unsychronization = false
        override var unsynchronisation: Boolean
            get() = _unsychronization
            set(unsynchronisation) {}
        override var bPM: Int
            get() = 0
            set(bpm) {}
        override var grouping: String?
            get() = null
            set(grouping) {}
        override var key: String?
            get() = null
            set(key) {}
        override var date: String?
            get() = null
            set(date) {}
        override var publisher: String?
            get() = null
            set(publisher) {}
        override var artistUrl: String?
            get() = null
            set(url) {}
        override var commercialUrl: String?
            get() = null
            set(url) {}
        override var copyrightUrl: String?
            get() = null
            set(url) {}
        override var audiofileUrl: String?
            get() = null
            set(url) {}
        override var audioSourceUrl: String?
            get() = null
            set(url) {}
        override var radiostationUrl: String?
            get() = null
            set(url) {}
        override var paymentUrl: String?
            get() = null
            set(url) {}
        override var publisherUrl: String?
            get() = null
            set(url) {}
        override var partOfSet: String?
            get() = null
            set(partOfSet) {}
        override var isCompilation: Boolean
            get() = false
            set(compilation) {}

        var _chapters: ArrayList<ID3v2ChapterFrameData>? = null
        override var chapters: ArrayList<ID3v2ChapterFrameData>?
            get() = null
            set(chapters) {
                _chapters = chapters
            }


        private var _chapterTOC: ArrayList<ID3v2ChapterTOCFrameData>? = null
        override var chapterTOC: ArrayList<ID3v2ChapterTOCFrameData>?
            get() = null
            set(chapterTOC) {
                _chapterTOC = chapterTOC
            }


        override fun setAlbumImage(albumImage: ByteArray?, mimeType: String?) {
            this.albumImage = albumImage
            albumImageMimeType = mimeType
        }

        override fun setAlbumImage(
            albumImage: ByteArray?,
            mimeType: String?,
            imageType: Byte,
            imageDescription: String?
        ) {
        }

        override fun clearAlbumImage() {}
        override var wmpRating: Int
            get() = 0
            set(rating) {}
        override var itunesComment: String?
            get() = null
            set(itunesComment) {}
        override var genreDescription: String?
            get() = super.genreDescription
            set(value) {super.genreDescription = value}

        override val dataLength: Int
            get() = 0
        override val length: Int
            get() = 0
        override val obsoleteFormat: Boolean
            get() = false

        fun addFrameSet(id: String?, frameSet: ID3v2FrameSet) {
            frameSets[id] = frameSet
        }

        override fun clearFrameSet(id: String?) {
            frameSets.remove(id)
        }
    } //endregion
}