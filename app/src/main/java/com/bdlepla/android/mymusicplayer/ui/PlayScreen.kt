package com.bdlepla.android.mymusicplayer.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.session.MediaController
import com.bdlepla.android.mymusicplayer.Extensions.toImagePainter
import com.bdlepla.android.mymusicplayer.R
import com.bdlepla.android.mymusicplayer.SampleData
import com.bdlepla.android.mymusicplayer.business.Callbacks
import com.bdlepla.android.mymusicplayer.business.CurrentPlayingStats
import com.bdlepla.android.mymusicplayer.business.SongInfo
import com.bdlepla.android.mymusicplayer.ui.theme.MyMusicPlayerTheme

@Composable
fun PlayScreen(currentSong: SongInfo,
               currentPlayingStats: CurrentPlayingStats?,
               isPaused: Boolean,
               controller: MediaController?=null) {
    val callbacks = Callbacks(
        onForward = {controller?.seekForward()},
        onNext = {controller?.seekToNext()},
        onPause = {controller?.pause()},
        onPlay = {controller?.play()},
        onPrevious = {controller?.seekToPrevious()},
        onReverse = {controller?.seekBack()}
    )
    Column {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = currentSong.albumArt.toImagePainter(),
                contentDescription = currentSong.albumArt,
                modifier = Modifier
                    .size(300.dp)
                    .align(Alignment.Center)
            )
        }
        Spacer(modifier = Modifier.padding(4.dp))
        TimingLine(currentPlayingStats)
        Spacer(modifier = Modifier.padding(all = 4.dp))
        Column(modifier = Modifier.padding(all = 4.dp)) {
            PlayerControllerButtons(isPaused, callbacks)
            Song(currentSong)
        }
    }
}

@Composable
fun PlayerControllerButtons(isPaused: Boolean, callbacks: Callbacks) {
    val prevIcon = painterResource(id = R.drawable.media3_notification_seek_to_previous)
    val reverseIcon = painterResource(id = R.drawable.media3_notification_seek_back)
    val playIcon = painterResource(id = R.drawable.media3_notification_play)
    val pauseIcon = painterResource(id = R.drawable.media3_notification_pause)
    val forwardIcon = painterResource(id = R.drawable.media3_notification_seek_forward)
    val nextIcon = painterResource(id = R.drawable.media3_notification_seek_to_next)

    Row {
        Image(painter = prevIcon,
            contentDescription = "Previous Song",
            modifier = Modifier
                .size(50.dp)
                .requiredWidth(50.dp)
                .weight(1f)
                .background(Color.Black)
                .clickable { callbacks.onPrevious() })
        Spacer(modifier = Modifier.requiredWidth(4.dp))
        Image(painter = reverseIcon,
            contentDescription = "Back up 10 seconds",
            modifier = Modifier
                .size(50.dp)
                .requiredWidth(50.dp)
                .weight(1f)
                .background(Color.Black)
                .clickable { callbacks.onReverse() })
        Spacer(modifier = Modifier.requiredWidth(4.dp))
        Image(painter = if (isPaused) playIcon else pauseIcon,
            contentDescription = if (isPaused) "Play" else "Pause",
            modifier = Modifier
                .size(50.dp)
                .requiredWidth(50.dp)
                .weight(1f)
                .background(Color.Black)
                .clickable { if (isPaused) callbacks.onPlay()
                    else callbacks.onPause() })
        Spacer(modifier = Modifier.requiredWidth(4.dp))
        Image(painter = forwardIcon,
            contentDescription = "Fast Forward 10 seconds",
            modifier = Modifier
                .size(50.dp)
                .requiredWidth(50.dp)
                .weight(1f)
                .background(Color.Black)
                .clickable { callbacks.onForward() })
        Spacer(modifier = Modifier.requiredWidth(4.dp))
        Image(painter = nextIcon,
            contentDescription = "Next Song",
            modifier = Modifier
                .size(50.dp)
                .requiredWidth(50.dp)
                .weight(1f)
                .background(Color.Black)
                .clickable { callbacks.onNext() })
    }
}


@Preview(
    showBackground = true,
    name="Currently Playing Light Mode"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Currently Playing Dark Mode"
)
@Composable
fun PlayScreenPreview() {
    MyMusicPlayerTheme {
        PlayScreen(SampleData().songs[0], CurrentPlayingStats(0, 0), false)
    }
}
