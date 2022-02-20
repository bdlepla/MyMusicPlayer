package com.bdlepla.android.mymusicplayer.ui

import android.app.PendingIntent.FLAG_IMMUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Intent
import android.os.Build
import androidx.core.app.TaskStackBuilder
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import com.bdlepla.android.mymusicplayer.MainActivity
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

class PlayService: MediaLibraryService() {
    private lateinit var player: ExoPlayer
    private lateinit var mediaLibrarySession: MediaLibrarySession
    private val librarySessionCallback = CustomMediaLibrarySessionCallback()

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession {
        return mediaLibrarySession
    }

    @androidx.annotation.OptIn(UnstableApi::class)
    private inner class CustomMediaLibrarySessionCallback :
        MediaLibrarySession.MediaLibrarySessionCallback {
        override fun onGetLibraryRoot(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<MediaItem>> {
            return Futures.immediateFuture(
                LibraryResult.ofItem(
                    MediaItemTree.getRootItem(),
                    params
                )
            )
        }
    }

    private fun initializeSessionAndPlayer() {

        val parentScreenIntent = Intent(this, MainActivity::class.java)
        //val intent = Intent(this, PlayerActivity::class.java)

        val pendingIntent = TaskStackBuilder.create(this).run {
                addNextIntent(parentScreenIntent)
                //addNextIntent(intent)

                val immutableFlag = if (Build.VERSION.SDK_INT >= 23) FLAG_IMMUTABLE else 0
                getPendingIntent(0, immutableFlag or FLAG_UPDATE_CURRENT)
            }

        player = ExoPlayer.Builder(this)
            .setAudioAttributes(AudioAttributes.DEFAULT, /* handleAudioFocus= */ true)
            .build()

        MediaItemTree.initialize()

        mediaLibrarySession = MediaLibrarySession.Builder(this, player, librarySessionCallback)
            .setMediaItemFiller(CustomMediaItemFiller())
            .setSessionActivity(pendingIntent!!)
            .build()
    }

    private class CustomMediaItemFiller : MediaSession.MediaItemFiller {
        override fun fillInLocalConfiguration(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItem: MediaItem
        ): MediaItem {
            return MediaItemTree.getItem(mediaItem.mediaId) ?: mediaItem
        }
    }

    override fun onCreate() {
        super.onCreate()
        initializeSessionAndPlayer()
    }

    override fun onDestroy() {
        player.release()
        mediaLibrarySession.release()
        super.onDestroy()
    }

}
