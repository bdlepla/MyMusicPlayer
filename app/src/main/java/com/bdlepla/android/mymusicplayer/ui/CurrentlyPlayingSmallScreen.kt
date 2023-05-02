package com.bdlepla.android.mymusicplayer.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Divider
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
    val playPauseIcon = painterResource(
        id =
        if (isPaused) androidx.media3.session.R.drawable.media3_notification_play
        else androidx.media3.session.R.drawable.media3_notification_pause
    )
    val nextIcon = painterResource(id = androidx.media3.session.R.drawable.media3_notification_seek_to_next)

    Column {
        Divider(
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
                    .background(Color.Black)
                    .clickable { onPlayPauseClick.invoke() })
            Spacer(modifier = Modifier.requiredWidth(4.dp))
            Image(painter = nextIcon, contentDescription = "Next",
                modifier = Modifier
                    .size(50.dp)
                    .weight(1f)
                    .requiredWidth(50.dp)
                    .background(Color.Black)
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