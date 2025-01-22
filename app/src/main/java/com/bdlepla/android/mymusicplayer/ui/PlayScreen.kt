package com.bdlepla.android.mymusicplayer.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.session.MediaController
import com.bdlepla.android.mymusicplayer.R
import com.bdlepla.android.mymusicplayer.SampleData
import com.bdlepla.android.mymusicplayer.business.Callbacks
import com.bdlepla.android.mymusicplayer.business.CurrentPlayingStats
import com.bdlepla.android.mymusicplayer.business.SongInfo
import com.bdlepla.android.mymusicplayer.extensions.toImagePainter
import com.bdlepla.android.mymusicplayer.ui.theme.MyMusicPlayerTheme
import com.danrusu.pods4k.immutableArrays.ImmutableArray
import com.danrusu.pods4k.immutableArrays.indexOf

@Composable
fun PlayScreen(
    currentPlayingStats: CurrentPlayingStats?,
    currentSongs: ImmutableArray<SongInfo>,
    isPaused: Boolean,
    controller: MediaController?=null) {
    val currentSong = currentPlayingStats?.currentPlaying ?: return
    val currentSongIndex = currentSongs.indexOf(currentSong)
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
        Spacer(modifier= Modifier.padding(all=4.dp))
        SongList(currentSongs, currentSongIndex = currentSongIndex)
    }
}

@Composable
fun PlayerControllerButtons(isPaused: Boolean, callbacks: Callbacks) {
    val isDark = isSystemInDarkTheme()
    val prevIcon = painterResource(id =
        if (isDark) R.drawable.baseline_skip_prev_white_24
        else R.drawable.baseline_skip_prev_black_24)
    val reverseIcon = painterResource(id =
        if (isDark) R.drawable.baseline_fast_rewind_white_24
        else R.drawable.baseline_fast_rewind_black_24)
    val playIcon = painterResource(id =
        if (isDark) R.drawable.baseline_play_white_24
        else R.drawable.baseline_play_black_24)
    val pauseIcon = painterResource(id =
        if (isDark) R.drawable.baseline_pause_white_24
        else R.drawable.baseline_pause_black_24)
    val forwardIcon = painterResource(id =
        if (isDark) R.drawable.baseline_fast_forward_white_24
        else R.drawable.baseline_fast_forward_black_24)
    val nextIcon = painterResource(id =
        if (isDark) R.drawable.baseline_skip_next_white_24
        else R.drawable.baseline_skip_next_black_24)

    Row {
        Image(painter = prevIcon,
            contentDescription = "Previous Song",
            modifier = Modifier
                .size(50.dp)
                .requiredWidth(50.dp)
                .weight(1f)
                .background(Color.Transparent)
                .clickable { callbacks.onPrevious() })
        Spacer(modifier = Modifier.requiredWidth(4.dp))
        Image(painter = reverseIcon,
            contentDescription = "Back up 10 seconds",
            modifier = Modifier
                .size(50.dp)
                .requiredWidth(50.dp)
                .weight(1f)
                .background(Color.Transparent)
                .clickable { callbacks.onReverse() })
        Spacer(modifier = Modifier.requiredWidth(4.dp))
        Image(painter = if (isPaused) playIcon else pauseIcon,
            contentDescription = if (isPaused) "Play" else "Pause",
            modifier = Modifier
                .size(50.dp)
                .requiredWidth(50.dp)
                .weight(1f)
                .background(Color.Transparent)
                .clickable { if (isPaused) callbacks.onPlay()
                    else callbacks.onPause() })
        Spacer(modifier = Modifier.requiredWidth(4.dp))
        Image(painter = forwardIcon,
            contentDescription = "Fast Forward 10 seconds",
            modifier = Modifier
                .size(50.dp)
                .requiredWidth(50.dp)
                .weight(1f)
                .background(Color.Transparent)
                .clickable { callbacks.onForward() })
        Spacer(modifier = Modifier.requiredWidth(4.dp))
        Image(painter = nextIcon,
            contentDescription = "Next Song",
            modifier = Modifier
                .size(50.dp)
                .requiredWidth(50.dp)
                .weight(1f)
                .background(Color.Transparent)
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
        val songs = SampleData().songs
        PlayScreen(CurrentPlayingStats(songs[0], 0, 0), songs, false)
    }
}
