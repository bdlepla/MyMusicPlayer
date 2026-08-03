package com.bdlepla.android.mymusicplayer.service

import androidx.media3.common.MediaItem
import com.bdlepla.android.mymusicplayer.business.PlaylistInfo
import com.bdlepla.android.mymusicplayer.business.SongInfo
import com.danrusu.pods4k.immutableArrays.emptyImmutableArray
import com.danrusu.pods4k.immutableArrays.toImmutableArray
import org.junit.Assert.assertEquals
import org.junit.Test

class QueueManagerTest {

    private val defaultPlaylistProvider = {
        PlaylistInfo("Default", emptyImmutableArray())
    }

    @Test
    fun setSongs_withValidIndex_setsIndex() {
        val queueManager = QueueManager(defaultPlaylistProvider)
        val songs = listOf(createSongInfo("1"), createSongInfo("2"))
        
        queueManager.setSongs(songs, 1, 100L)
        
        assertEquals(1, queueManager.currentIndex)
        assertEquals(100L, queueManager.currentPosition)
    }

    @Test
    fun setSongs_withOutOfBoundsIndex_coercesIndex() {
        val queueManager = QueueManager(defaultPlaylistProvider)
        val songs = listOf(createSongInfo("1"), createSongInfo("2"))
        
        queueManager.setSongs(songs, 5, 100L)
        
        assertEquals(1, queueManager.currentIndex)
        
        queueManager.setSongs(songs, -1, 100L)
        assertEquals(0, queueManager.currentIndex)
    }

    @Test
    fun updateCurrentState_withInvalidIndex_ignoresUpdate() {
        val queueManager = QueueManager(defaultPlaylistProvider)
        val songs = listOf(createSongInfo("1"), createSongInfo("2"))
        queueManager.setSongs(songs, 0, 0L)
        
        queueManager.updateCurrentState(5, 100L) // Out of bounds
        
        assertEquals(0, queueManager.currentIndex)
        assertEquals(0L, queueManager.currentPosition)
        
        queueManager.updateCurrentState(-1, 100L) // Invalid
        assertEquals(0, queueManager.currentIndex)
    }

    private fun createSongInfo(id: String): SongInfo {
        val mediaItem = MediaItem.Builder().setMediaId("[item]$id").build()
        return SongInfo(mediaItem)
    }
}
