package com.bdlepla.android.mymusicplayer.service

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MediaMetadata.*
import com.bdlepla.android.mymusicplayer.Extensions.forSorting
import com.bdlepla.android.mymusicplayer.business.albumId
import com.bdlepla.android.mymusicplayer.business.artistId
import com.bdlepla.android.mymusicplayer.repository.*
import com.google.common.collect.ImmutableList

object MediaItemTree {
    private var treeNodes: MutableMap<String, MediaItemNode> = mutableMapOf()
    private var titleMap: MutableMap<String, String> = mutableMapOf()
    private var isInitialized = false
    private const val ALBUM_PREFIX = "[album]"
    private const val GENRE_PREFIX = "[genre]"
    private const val ARTIST_PREFIX = "[artist]"
    const val ITEM_PREFIX = "[item]"

    private class MediaItemNode(val item: MediaItem) {
        private val children: MutableList<String> = ArrayList()

        fun addChild(childID: String) {
            this.children.add(childID)
        }

        fun getChildren(): List<MediaItem> {
            return ImmutableList.copyOf(children.mapNotNull{treeNodes[it]?.item})
        }
    }

    private fun buildMediaItem(
        title: String,
        mediaId: String,
        isPlayable: Boolean,
        @MediaMetadata.FolderType folderType: Int,
        album: String? = null,
        albumId: Long = 0L,
        artist: String? = null,
        artistId: Long = 0L,
        genre: String? = null,
        sourceUri: Uri? = null,
        imageUri: Uri? = null,
    ): MediaItem {
        val bundle =  Bundle().apply {
            putString(ALBUM, album)
            putLong(ALBUM_ID, albumId)
            putString(ARTIST, artist)
            putLong(ARTIST_ID, artistId)
            putString(GENRE, genre)
            //putLong(GENRE_ID, genreId)
        }
        @Suppress("RemoveRedundantQualifierName")
        val metadata =
            MediaMetadata.Builder()
                .setAlbumTitle(album)
                .setTitle(title)
                .setArtist(artist)
                .setGenre(genre)
                .setFolderType(folderType)
                .setIsPlayable(isPlayable)
                .setArtworkUri(imageUri)
                .setExtras(bundle)
                .build()
        return MediaItem.Builder()
            .setMediaId(mediaId)
            .setMediaMetadata(metadata)
            .setUri(sourceUri)
            .build()
    }

    fun initialize(context: Context) {
        if (isInitialized) return
        isInitialized = true
        // create root and folders for album/artist/genre.
        treeNodes[ROOT_ID] =
            MediaItemNode(
                buildMediaItem(
                    title = "Root Folder",
                    mediaId = ROOT_ID,
                    isPlayable = false,
                    folderType = FOLDER_TYPE_MIXED
                )
            )

        treeNodes[ITEM_ID] =
            MediaItemNode(
                buildMediaItem(
                    title = "Item Folder",
                    mediaId = ITEM_ID,
                    isPlayable = false,
                    folderType = FOLDER_TYPE_MIXED
                )
            )

        treeNodes[ALBUM_ID] =
            MediaItemNode(
                buildMediaItem(
                    title = "Album Folder",
                    mediaId = ALBUM_ID,
                    isPlayable = false,
                    folderType = FOLDER_TYPE_MIXED
                )
            )
        treeNodes[ARTIST_ID] =
            MediaItemNode(
                buildMediaItem(
                    title = "Artist Folder",
                    mediaId = ARTIST_ID,
                    isPlayable = false,
                    folderType = FOLDER_TYPE_MIXED
                )
            )
        treeNodes[GENRE_ID] =
            MediaItemNode(
                buildMediaItem(
                    title = "Genre Folder",
                    mediaId = GENRE_ID,
                    isPlayable = false,
                    folderType = FOLDER_TYPE_MIXED
                )
            )
        treeNodes[ROOT_ID]!!.addChild(ALBUM_ID)
        treeNodes[ROOT_ID]!!.addChild(ARTIST_ID)
        treeNodes[ROOT_ID]!!.addChild(GENRE_ID)
        treeNodes[ROOT_ID]!!.addChild(ITEM_ID)
        val songs = Repository.getAllSongs(context)
        buildTree(songs)
    }

    private fun buildTree(songList: List<MediaItem>) {
        val sortedSongs = songList.sortedBy { it.mediaMetadata.title.toString().forSorting() }
        val itemListInTree = treeNodes[ITEM_ID]!!
        sortedSongs.forEach {
            val idInTree = it.mediaId
            treeNodes[idInTree] = MediaItemNode(it)
            itemListInTree.addChild(idInTree)
        }

        val songsByAlbum = sortedSongs
            .groupBy { it.mediaMetadata.albumTitle.toString() }
            .toSortedMap(compareBy { it.forSorting() })
        val albumsInTree = treeNodes[ALBUM_ID]!!
        songsByAlbum.forEach { kv ->
            val album = kv.key
            val albumFolderIdInTree = ALBUM_PREFIX + album
            val items = kv.value
            val artist = items[0].mediaMetadata.artist.toString()
            val artistId = items[0].mediaMetadata.artistId
            val albumId = items[0].mediaMetadata.albumId
            val imageUri = items[0].mediaMetadata.artworkUri
            treeNodes[albumFolderIdInTree] =
                MediaItemNode(
                    buildMediaItem(
                        title = album,
                        album = album,
                        albumId = albumId,
                        imageUri = imageUri,
                        artist= artist,
                        artistId = artistId,
                        mediaId = albumFolderIdInTree,
                        isPlayable = true,
                        folderType = FOLDER_TYPE_PLAYLISTS
                    )
                )
            albumsInTree.addChild(albumFolderIdInTree)
            val thisAlbum = treeNodes[albumFolderIdInTree]!!
            items.map { it.mediaId }.forEach { thisAlbum.addChild(it) }
        }

        val albumsByArtist = sortedSongs
            .groupBy { k -> k.mediaMetadata.artist.toString() }
            .mapValues { kv -> kv.value.distinct().sortedBy { it.mediaMetadata.releaseYear } }
            .toSortedMap(compareBy { it.forSorting() })
        val artistsInTree = treeNodes[ARTIST_ID]!!
        albumsByArtist.forEach { kv ->
            val artist = kv.key
            val artistFolderIdInTree = ARTIST_PREFIX + artist
            val albums = kv.value
            val artistId = albums[0].mediaMetadata.artistId
            val imageUri = albums[0].mediaMetadata.artworkUri
            treeNodes[artistFolderIdInTree] =
                MediaItemNode(
                    buildMediaItem(
                        title = artist,
                        artist = artist,
                        artistId = artistId,
                        imageUri = imageUri,
                        mediaId = artistFolderIdInTree,
                        isPlayable = true,
                        folderType = FOLDER_TYPE_PLAYLISTS
                    )
                )
            artistsInTree.addChild(artistFolderIdInTree)
            val thisArtist = treeNodes[artistFolderIdInTree]!!
            albums.map { it.mediaId }.forEach { thisArtist.addChild(it) }
        }

        val songsByGenre = sortedSongs
            .groupBy { it.mediaMetadata.genre.toString() }
            .toSortedMap(compareBy { it.forSorting() })
        val genresInTree = treeNodes[GENRE_ID]!!
        songsByGenre.forEach { kv ->
            val genre = kv.key
            val genreFolderIdInTree = GENRE_PREFIX + genre
            val songs = kv.value
            treeNodes[genreFolderIdInTree] =
                MediaItemNode(
                    buildMediaItem(
                        title = genre,
                        mediaId = genreFolderIdInTree,
                        isPlayable = true,
                        folderType = FOLDER_TYPE_PLAYLISTS
                    )
                )
            genresInTree.addChild(genreFolderIdInTree)
            val thisGenre = treeNodes[genreFolderIdInTree]!!
            songs.map { it.mediaId }.forEach { thisGenre.addChild(it) }
        }
    }

    fun getItem(mediaId: String): MediaItem? = treeNodes[mediaId]?.item

    fun getRootItem(): MediaItem = treeNodes[ROOT_ID]!!.item

    fun getChildren(id: String): List<MediaItem>? {
        return treeNodes[id]?.getChildren()
    }

    fun getRandomItem(): MediaItem {
        var curRoot = getRootItem()
        while (curRoot.mediaMetadata.folderType != FOLDER_TYPE_NONE) {
            val children = getChildren(curRoot.mediaId)!!
            curRoot = children.random()
        }
        return curRoot
    }

    fun getItemFromTitle(title: String): MediaItem? {
        val itemId = titleMap[title] ?: return null
        return treeNodes[itemId]?.item
    }
}
