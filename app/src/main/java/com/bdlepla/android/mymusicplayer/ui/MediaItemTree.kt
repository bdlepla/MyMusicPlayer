package com.bdlepla.android.mymusicplayer.ui

import android.content.res.AssetManager
import androidx.media3.common.MediaItem

object MediaItemTree {
    private val treeNodes: MutableMap<String, MediaItemNode> = mutableMapOf()
    private const val ROOT_ID = "[rootID]"
    fun getRootItem(): MediaItem {
        return treeNodes[ROOT_ID]!!.item
    }

    fun initialize(assets: AssetManager?) {
        TODO("Not yet implemented")
    }

    fun getItem(mediaId: String): MediaItem? {
        TODO("Not yet implemented")
    }

    private class MediaItemNode(val item: MediaItem) {

    }
}