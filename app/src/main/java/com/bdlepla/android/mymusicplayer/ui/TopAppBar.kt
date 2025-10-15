package com.bdlepla.android.mymusicplayer.ui

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.media3.common.Player
import com.bdlepla.android.mymusicplayer.R
import com.bdlepla.android.mymusicplayer.ui.theme.MyMusicPlayerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    castState: Int,
    repeatState: Int,
    activity: Context? = null,
    onShuffleClick: ()->Unit = emptyFunction(),
    onRepeatClick: ()->Unit = emptyFunction(),
) {
    fun onCastButtonClick(state:Int) {}
    Column {
        TopAppBar(
            modifier = Modifier.background(MaterialTheme.colorScheme.primaryContainer),
            title = {
                Text(
                    text = "My Music Player",
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            actions = {
                CastIconButton(castState, activity)
                IconButton(onClick = { onShuffleClick() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_shuffle),
                        contentDescription = "Shuffle"
                    )
                }
                IconButton(onClick = { onRepeatClick() }) {
                    val repeatPainter = if (repeatState== Player.REPEAT_MODE_OFF)
                        R.drawable.ic_repeat else R.drawable.ic_repeat_on

                    Icon(
                        painter = painterResource(id = repeatPainter),
                        contentDescription = "Repeat"
                    )
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                }
            }
        )
    }
}

@Preview(
    showBackground = true,
    name="Top App Bar Light Mode"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Top App Bar Dark Mode"
)

@Composable
fun TopAppBarPreview() {
    MyMusicPlayerTheme {
        TopAppBar(0, 0)
    }
}