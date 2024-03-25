package com.bdlepla.android.mymusicplayer.cast

import android.content.Context
import androidx.media3.cast.DefaultCastOptionsProvider
import com.google.android.gms.cast.framework.CastOptions
import com.google.android.gms.cast.framework.OptionsProvider
import com.google.android.gms.cast.framework.SessionProvider

// specified in Manifest
@Suppress("unused")
class CastOptionsProvider : OptionsProvider {

    override fun getCastOptions(context: Context): CastOptions = DefaultCastOptionsProvider().getCastOptions(context)
//    {
//        return CastOptions.Builder()
//            // Use the Default Media Receiver with DRM support.
//            .setResumeSavedSession(false)
//            .setEnableReconnectionService(false)
//            .setReceiverApplicationId(CastMediaControlIntent.DEFAULT_MEDIA_RECEIVER_APPLICATION_ID)
//            //.setSupportedNamespaces()
//            .setCastMediaOptions(
//                CastMediaOptions.Builder()
//                    // We manage the media session and the notifications ourselves.
//                    .setMediaSessionEnabled(false)
//                    .setNotificationOptions(null)
//                    .build()
//            )
//            .setStopReceiverApplicationWhenEndingSession(true).build()
//    }

    override fun getAdditionalSessionProviders(context: Context): MutableList<SessionProvider> {
        return mutableListOf()
    }
}