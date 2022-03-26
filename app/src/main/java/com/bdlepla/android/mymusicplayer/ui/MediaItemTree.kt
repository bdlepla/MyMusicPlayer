package com.bdlepla.android.mymusicplayer.ui

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MediaMetadata.*
import com.bdlepla.android.mymusicplayer.business.SongInfo
import com.bdlepla.android.mymusicplayer.repository.Repository
import com.google.common.collect.ImmutableList

object MediaItemTree {
    private var treeNodes: MutableMap<String, MediaItemNode> = mutableMapOf()
    private var titleMap: MutableMap<String, MediaItemNode> = mutableMapOf()
    private var isInitialized = false
    private const val ROOT_ID = "[rootID]"
    private const val ALBUM_ID = "[albumID]"
    private const val GENRE_ID = "[genreID]"
    private const val ARTIST_ID = "[artistID]"
    private const val ALBUM_PREFIX = "[album]"
    private const val GENRE_PREFIX = "[genre]"
    private const val ARTIST_PREFIX = "[artist]"
    const val ITEM_PREFIX = "[item]"

    private class MediaItemNode(val item: MediaItem) {
        private val children: MutableList<MediaItem> = ArrayList()

        fun addChild(childID: String) {
            this.children.add(treeNodes[childID]!!.item)
        }

        fun getChildren(): List<MediaItem> {
            return ImmutableList.copyOf(children)
        }
    }

    @androidx.media3.common.util.UnstableApi
    private fun buildMediaItem(
        title: String,
        mediaId: String,
        isPlayable: Boolean,
        @MediaMetadata.FolderType folderType: Int,
        album: String? = null,
        artist: String? = null,
        genre: String? = null,
        sourceUri: Uri? = null,
        imageUri: Uri? = null,
    ): MediaItem {
        val metadata =
            Builder()
                .setAlbumTitle(album)
                .setTitle(title)
                .setArtist(artist)
                .setGenre(genre)
                .setFolderType(folderType)
                .setIsPlayable(isPlayable)
                .setArtworkUri(imageUri)
                .build()
        return MediaItem.Builder()
            .setMediaId(mediaId)
            .setMediaMetadata(metadata)
            .setUri(sourceUri)
            .build()
    }

    @androidx.media3.common.util.UnstableApi
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
        Repository.getAllSongs(context).forEach {songInfo ->
            addNodeToTree(songInfo)
        }
    }

    @androidx.media3.common.util.UnstableApi
    private fun addNodeToTree(songInfo: SongInfo) {
        // key of such items in tree
        val title = songInfo.title
        val album = songInfo.album
        val artist = songInfo.artist
        val genre = songInfo.genre

        val idInTree = ITEM_PREFIX + songInfo.songId
        val albumFolderIdInTree = ALBUM_PREFIX + album
        val artistFolderIdInTree = ARTIST_PREFIX + artist
        val genreFolderIdInTree = GENRE_PREFIX + genre

        treeNodes[idInTree] = MediaItemNode(songInfo.toMediaItem())

        titleMap[title.lowercase()] = treeNodes[idInTree]!!

        if (!treeNodes.containsKey(albumFolderIdInTree)) {
            treeNodes[albumFolderIdInTree] =
                MediaItemNode(
                    buildMediaItem(
                        title = album,
                        mediaId = albumFolderIdInTree,
                        isPlayable = true,
                        folderType = FOLDER_TYPE_PLAYLISTS
                    )
                )
            treeNodes[ALBUM_ID]!!.addChild(albumFolderIdInTree)
        }
        treeNodes[albumFolderIdInTree]!!.addChild(idInTree)

        // add into artist folder
        if (!treeNodes.containsKey(artistFolderIdInTree)) {
            treeNodes[artistFolderIdInTree] =
                MediaItemNode(
                    buildMediaItem(
                        title = artist,
                        mediaId = artistFolderIdInTree,
                        isPlayable = true,
                        folderType = FOLDER_TYPE_PLAYLISTS
                    )
                )
            treeNodes[ARTIST_ID]!!.addChild(artistFolderIdInTree)
        }
        treeNodes[artistFolderIdInTree]!!.addChild(idInTree)

        // add into genre folder
        if (!treeNodes.containsKey(genreFolderIdInTree)) {
            treeNodes[genreFolderIdInTree] =
                MediaItemNode(
                    buildMediaItem(
                        title = genre,
                        mediaId = genreFolderIdInTree,
                        isPlayable = true,
                        folderType = FOLDER_TYPE_PLAYLISTS
                    )
                )
            treeNodes[GENRE_ID]!!.addChild(genreFolderIdInTree)
        }
        treeNodes[genreFolderIdInTree]!!.addChild(idInTree)
    }

    fun getItem(mediaId: String): MediaItem? = treeNodes[mediaId]?.item

    fun getRootItem(): MediaItem = treeNodes[ROOT_ID]!!.item

    fun getChildren(id: String): List<MediaItem>? = treeNodes[id]?.getChildren()

    @androidx.media3.common.util.UnstableApi
    fun getRandomItem(): MediaItem {
        var curRoot = getRootItem()
        while (curRoot.mediaMetadata.folderType != FOLDER_TYPE_NONE) {
            val children = getChildren(curRoot.mediaId)!!
            curRoot = children.random()
        }
        return curRoot
    }

    fun getItemFromTitle(title: String): MediaItem? {
        return titleMap[title]?.item
    }
}