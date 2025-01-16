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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bdlepla.android.mymusicplayer.R
import com.bdlepla.android.mymusicplayer.SampleData
import com.bdlepla.android.mymusicplayer.business.SongInfo
import com.bdlepla.android.mymusicplayer.ui.theme.MyMusicPlayerTheme

@Composable
fun CurrentlyPlayingSmallScreen(
    currentSong: SongInfo,
    isPaused:Boolean,
    onPlayPauseClick:() -> Unit = emptyFunction(),
    onNextClick:() -> Unit = emptyFunction(),
    onClick:(SongInfo) -> Unit = emptyFunction1()
) {
    val isDark = isSystemInDarkTheme()
    val playPauseIcon = painterResource( id =
        if (isPaused) {
            if (isDark) R.drawable.baseline_play_white_24
            else R.drawable.baseline_play_black_24
        }
        else {
            if (isDark) R.drawable.baseline_pause_white_24
            else R.drawable.baseline_pause_black_24
        }
    )
    val nextIcon = painterResource(id =
        if (isDark) R.drawable.baseline_skip_next_white_24
        else R.drawable.baseline_skip_next_black_24
    )

    Column {
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 4.dp),
            color = MaterialTheme.colorScheme.primary
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.weight(3f)
            ) {
                SongWithImage(currentSong, onClick)
            }
            Image(painter = playPauseIcon,
                contentDescription = if (isPaused) "Play" else "Pause",
                modifier = Modifier
                    .size(50.dp)
                    .requiredWidth(50.dp)
                    .weight(1f)
                    .background(Color.Transparent)
                    .clickable { onPlayPauseClick.invoke() })
            Spacer(modifier = Modifier.requiredWidth(4.dp))
            Image(painter = nextIcon, contentDescription = "Next",
                modifier = Modifier
                    .size(50.dp)
                    .weight(1f)
                    .requiredWidth(50.dp)
                    .background(Color.Transparent)
                    .clickable { onNextClick.invoke() })
        }
    }
}


@Preview(
    showBackground = true,
    name="Currently Playing Small Light Mode"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Currently Playing Small Dark Mode"
)
@Composable
fun CurrentlyPlayingSmallScreenPreview() {
    MyMusicPlayerTheme {
        CurrentlyPlayingSmallScreen(SampleData().songs[0], false)
    }
}