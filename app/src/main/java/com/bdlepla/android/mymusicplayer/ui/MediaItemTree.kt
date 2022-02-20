package com.bdlepla.android.mymusicplayer.ui

import androidx.media3.common.MediaItem

object MediaItemTree {
    private val treeNodes: MutableMap<String, MediaItemNode> = mutableMapOf()
    private const val ROOT_ID = "[rootID]"
    fun getRootItem(): MediaItem {
        return treeNodes[ROOT_ID]!!.item
    }

    fun initialize() {
        TODO("Not yet implemented")
    }

    fun getItem(mediaId: String): MediaItem? =
        treeNodes.getOrDefault(mediaId, null)?.item

    private class MediaItemNode(val item: MediaItem) {

    }
}