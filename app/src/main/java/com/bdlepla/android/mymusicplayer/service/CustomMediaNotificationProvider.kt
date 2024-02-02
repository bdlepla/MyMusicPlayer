package com.bdlepla.android.mymusicplayer.service

import android.content.Context
import android.os.Bundle
import androidx.media3.session.CommandButton
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaNotification
import androidx.media3.session.MediaSession
import com.bdlepla.android.mymusicplayer.R
import com.google.common.collect.ImmutableList

class CustomMediaNotificationProvider(private val context: Context): MediaNotification.Provider {
    override fun createNotification(
        mediaSession: MediaSession,
        customLayout: ImmutableList<CommandButton>,
        actionFactory: MediaNotification.ActionFactory,
        onNotificationChangedCallback: MediaNotification.Provider.Callback
    ): MediaNotification {
        val ret = DefaultMediaNotificationProvider(context)
        ret.setSmallIcon(R.drawable.icons8_musical_notes_18___)
        return ret.createNotification(
            mediaSession,
            customLayout,
            actionFactory,
            onNotificationChangedCallback
        )
    }

    override fun handleCustomCommand(
        session: MediaSession,
        action: String,
        extras: Bundle
    ): Boolean {
        return false
    }
}