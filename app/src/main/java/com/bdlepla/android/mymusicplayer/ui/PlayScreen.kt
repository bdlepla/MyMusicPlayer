package com.bdlepla.android.mymusicplayer.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
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
private fun PlayScreen(filename:String) {

}

@Composable
fun CurrentlyPlayingSmallScreen(currentSong: SongInfo, isPaused:Boolean, onPlayPauseClick:()->Unit={}) {
    //val isPaused by viewModel.isPaused.collectAsState()
    val playPauseIcon = painterResource(
        if (isPaused) R.drawable.exo_icon_play
        else R.drawable.exo_icon_pause)
    val nextIcon = painterResource(id = R.drawable.exo_icon_next)

    Column {
        Divider(modifier = Modifier.padding(vertical = 4.dp),
                color= MaterialTheme.colors.primary)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.weight(3f)) {
                Song(currentSong)
            }
            Image(painter = playPauseIcon, contentDescription = "Play",
                modifier = Modifier
                    .size(50.dp)
                    .requiredWidth(50.dp)
                    .weight(1f)
                    .background(Color.Black)
                    .clickable { onPlayPauseClick.invoke()})
            Spacer(modifier = Modifier.width(4.dp))
            Image(painter = nextIcon, contentDescription = "Next",
                modifier = Modifier
                    .size(50.dp)
                    .weight(1f)
                    .requiredWidth(50.dp)
                    .background(Color.Black))
        }
    }
}

@Preview(
    showBackground = true,
    name="Song Light Mode"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Song Dark Mode"
)
@Composable
fun CurrentlyPlayingSmallScreenPreview() {
    MyMusicPlayerTheme {
        CurrentlyPlayingSmallScreen(SampleData.Songs[0], false)
    }
}