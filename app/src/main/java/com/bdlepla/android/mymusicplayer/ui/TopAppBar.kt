package com.bdlepla.android.mymusicplayer.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.bdlepla.android.mymusicplayer.R
import com.bdlepla.android.mymusicplayer.ui.theme.MyMusicPlayerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(
    onShuffleClick: ()->Unit = emptyFunction(),
    onRepeatClick: ()->Unit = emptyFunction(),
) {
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
                IconButton(onClick = { onShuffleClick() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_shuffle),
                        contentDescription = "Shuffle"
                    )
                }
                IconButton(onClick = { onRepeatClick() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_repeat),
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
        TopAppBar()
    }
}