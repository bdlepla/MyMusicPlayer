package com.bdlepla.android.mymusicplayer.ui

import android.content.Context
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.mediarouter.app.MediaRouteChooserDialog
import androidx.mediarouter.app.MediaRouteControllerDialog
import androidx.mediarouter.media.MediaControlIntent
import androidx.mediarouter.media.MediaRouteSelector
import com.bdlepla.android.mymusicplayer.R
import com.google.android.gms.cast.framework.CastState

@Composable
fun CastIconButton(castState: Int, activity: Context? = null) {
    val castIcon = when (castState) {
        CastState.NOT_CONNECTED -> R.drawable.cast_not_connected
        CastState.CONNECTING -> R.drawable.cast_connected
        CastState.CONNECTED -> R.drawable.cast_connected
        else -> 0
    }
    if (castState > CastState.NO_DEVICES_AVAILABLE) {
        IconButton(onClick = {
            if (activity != null) {
                if (castState == CastState.NOT_CONNECTED) {
                    MediaRouteChooserDialog(activity).apply {
                        routeSelector = MediaRouteSelector.Builder()
                            .addControlCategory(MediaControlIntent.CATEGORY_LIVE_AUDIO)
                            .addControlCategory(MediaControlIntent.CATEGORY_REMOTE_PLAYBACK)
                            .build()
                    }.show()
                } else {
                    MediaRouteControllerDialog(activity).show()
                }
            }
        }) {
            Icon(
                painter = painterResource(id = castIcon),
                contentDescription = "Cast"
            )
        }
    }
}