package com.bdlepla.android.mymusicplayer.business

import android.content.Context
import android.content.ContextWrapper
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.io.File

class PlaylistManagerTest {

    private class TestContextWrapper(private val targetDir: File?) : ContextWrapper(null) {
        override fun getApplicationContext(): Context = this
        override fun getExternalFilesDir(type: String?): File? = targetDir
    }

    @Test
    fun playlistManager_handlesNullExternalFilesDir_withoutThrowing() {
        val testContext = TestContextWrapper(null)
        val manager = PlaylistManager(testContext)
        assertNotNull(manager)
    }

    @Test
    fun playlistManager_handlesNonExistentExternalFilesDir_withoutThrowing() {
        val nonExistentDir = File("/non/existent/path/for/test/playlists")
        val testContext = TestContextWrapper(nonExistentDir)
        val manager = PlaylistManager(testContext)
        assertNotNull(manager)
    }
}
