package com.bdlepla.android.mymusicplayer.service

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MediaMetadata.*
import com.bdlepla.android.mymusicplayer.business.PlaylistReader
import com.bdlepla.android.mymusicplayer.extensions.forSorting
import com.bdlepla.android.mymusicplayer.business.albumId
import com.bdlepla.android.mymusicplayer.business.artistId
import com.bdlepla.android.mymusicplayer.repository.*
import com.google.common.collect.ImmutableList

object MediaItemTree {
    private var treeNodes: MutableMap<String, MediaItemNode> = mutableMapOf()
    private var titleMap: MutableMap<String, MediaItem> = mutableMapOf()
    private var isInitialized = false
    private const val ALBUM_PREFIX = "[album]"
    private const val GENRE_PREFIX = "[genre]"
    private const val ARTIST_PREFIX = "[artist]"
    private const val PLAYLIST_PREFIX = "[playlist]"
    const val ITEM_PREFIX = "[item]"

    private class MediaItemNode(val item: MediaItem) {
        private val children: MutableList<String> = ArrayList()

        fun addChild(childID: String) {
            this.children.add(childID)
        }

        fun getChildren(): List<MediaItem> =
            ImmutableList.copyOf(children.mapNotNull{treeNodes[it]?.item})
    }

    private fun buildMediaItem(
        title: String,
        mediaId: String,
        isPlayable: Boolean,
        mediaType: Int,
        isBrowsable: Boolean,
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
                .setMediaType(mediaType)
                .setIsBrowsable(isBrowsable)
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
                    isBrowsable = true,
                    mediaType = MEDIA_TYPE_FOLDER_MIXED
                )
            )

        treeNodes[ITEM_ID] =
            MediaItemNode(
                buildMediaItem(
                    title = "Item Folder",
                    mediaId = ITEM_ID,
                    isPlayable = false,
                    isBrowsable = true,
                    mediaType = MEDIA_TYPE_MUSIC
                )
            )

        treeNodes[ALBUM_ID] =
            MediaItemNode(
                buildMediaItem(
                    title = "Album Folder",
                    mediaId = ALBUM_ID,
                    isPlayable = false,
                    isBrowsable = true,
                    mediaType = MEDIA_TYPE_FOLDER_ALBUMS
                )
            )
        treeNodes[ARTIST_ID] =
            MediaItemNode(
                buildMediaItem(
                    title = "Artist Folder",
                    mediaId = ARTIST_ID,
                    isPlayable = false,
                    isBrowsable = true,
                    mediaType = MEDIA_TYPE_FOLDER_ARTISTS
                )
            )
        treeNodes[GENRE_ID] =
            MediaItemNode(
                buildMediaItem(
                    title = "Genre Folder",
                    mediaId = GENRE_ID,
                    isPlayable = false,
                    isBrowsable = true,
                    mediaType = MEDIA_TYPE_FOLDER_GENRES
                )
            )
        treeNodes[PLAYLIST_ID] =
            MediaItemNode(
                buildMediaItem(
                    title = "Playlist Folder",
                    mediaId = PLAYLIST_ID,
                    isPlayable = false,
                    isBrowsable = true,
                    mediaType = MEDIA_TYPE_FOLDER_PLAYLISTS
                )
            )

        treeNodes[ROOT_ID]!!.addChild(ALBUM_ID)
        treeNodes[ROOT_ID]!!.addChild(ARTIST_ID)
        treeNodes[ROOT_ID]!!.addChild(GENRE_ID)
        treeNodes[ROOT_ID]!!.addChild(PLAYLIST_ID)
        treeNodes[ROOT_ID]!!.addChild(ITEM_ID)
        val songs = Repository.getAllSongs(context)
        buildTree(songs, context)
    }

    private fun buildTree(songList: List<MediaItem>, context:Context) {
        val sortedSongs = songList.sortedBy { it.mediaMetadata.title.toString().forSorting() }
        val itemListInTree = treeNodes[ITEM_ID]!!
        sortedSongs.forEach {
            val idInTree = it.mediaId
            treeNodes[idInTree] = MediaItemNode(it)
            itemListInTree.addChild(idInTree)
            val title = it.mediaMetadata.title.toString().forSorting().lowercase()
            titleMap[title] = it
        }

        val songsByAlbum = sortedSongs
            .groupBy { it.mediaMetadata.albumTitle.toString() }
            .toSortedMap(compareBy { it.forSorting() })
        val albumsInTree = treeNodes[ALBUM_ID]!!
         songsByAlbum.forEach { (album,items) ->
            val albumFolderIdInTree = ALBUM_PREFIX + album
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
                        isBrowsable = true,
                        mediaType = MEDIA_TYPE_ALBUM
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
        val artistIds = mutableSetOf<Long>()
        albumsByArtist.forEach { (artist, albums) ->
            val artistFolderIdInTree = ARTIST_PREFIX + artist
            val artistId = albums[0].mediaMetadata.artistId
            if (!artistIds.add(artistId)) {
                return@forEach
            }
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
                        isBrowsable = true,
                        mediaType = MEDIA_TYPE_ARTIST
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
        songsByGenre.forEach { (genre, songs) ->
            val genreFolderIdInTree = GENRE_PREFIX + genre
            treeNodes[genreFolderIdInTree] =
                MediaItemNode(
                    buildMediaItem(
                        title = genre,
                        mediaId = genreFolderIdInTree,
                        isPlayable = true,
                        isBrowsable = true,
                        mediaType = MEDIA_TYPE_GENRE
                    )
                )
            genresInTree.addChild(genreFolderIdInTree)
            val thisGenre = treeNodes[genreFolderIdInTree]!!
            songs.map { it.mediaId }.forEach { thisGenre.addChild(it) }
        }

        val playlistsInTree = treeNodes[PLAYLIST_ID]!!
        PlaylistReader(context).loadPlaylists().forEach {(playlistName,songNames) ->
            val playlistFolderIdInTree = PLAYLIST_PREFIX + playlistName
            val thisPlaylist =
                MediaItemNode(
                    buildMediaItem(
                        title = playlistName,
                        mediaId = playlistFolderIdInTree,
                        isPlayable = true,
                        isBrowsable = true,
                        mediaType = MEDIA_TYPE_PLAYLIST
                    )
                )
            treeNodes[playlistFolderIdInTree] = thisPlaylist
            playlistsInTree.addChild(playlistFolderIdInTree)
            val playlistSongs = songNames
                .mapNotNull{songName -> songList.firstOrNull{it.localConfiguration?.uri.toString().endsWith(songName)}}
            playlistSongs.map{it.mediaId}.forEach{thisPlaylist.addChild(it)}
        }
    }

    fun getItem(mediaId: String): MediaItem? = treeNodes[mediaId]?.item

    fun getRootItem(): MediaItem = treeNodes[ROOT_ID]!!.item

    fun getChildren(id: String): List<MediaItem>? {
        return treeNodes[id]?.getChildren()
    }

    fun getRandomItem(): MediaItem {
        var curRoot = getRootItem()
        while (curRoot.mediaMetadata.isPlayable == null || !curRoot.mediaMetadata.isPlayable!!) {
            val children = getChildren(curRoot.mediaId)!!
            curRoot = children.random()
        }
        return curRoot
    }

    fun getItemFromTitle(title: String): MediaItem? {
        val titleForSorting = title.trim().forSorting().lowercase()
        val result = titleMap.getOrDefault(titleForSorting, null)
        if (result != null) return result
        val titleSplits = titleForSorting.split(' ')
        val key = titleMap.keys.map{key -> key to key.split(' ')
            .count{it in titleSplits}}.maxByOrNull { it.second } ?: return null
        return titleMap[key.first]
    }
}

