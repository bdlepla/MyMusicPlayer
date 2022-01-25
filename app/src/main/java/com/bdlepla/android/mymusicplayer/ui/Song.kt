package com.bdlepla.android.mymusicplayer.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bdlepla.android.mymusicplayer.SampleData
import com.bdlepla.android.mymusicplayer.business.SongInfo
import com.bdlepla.android.mymusicplayer.ui.theme.MyMusicPlayerTheme

@Composable
fun SongList(songInfos: List<SongInfo>) {
    LazyColumn{
        items(items = songInfos) { songInfo ->
            Song(songInfo)
            Divider(color = MaterialTheme.colors.primary)
        }
    }
}

@Composable
fun Song(songInfo:SongInfo) {
    Column(modifier=Modifier.padding(all=4.dp)){
        Text(
            text=songInfo.title,
            style = MaterialTheme.typography.subtitle2,
            color = MaterialTheme.colors.secondaryVariant
        )
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Row {
                Text(
                    text = songInfo.artist,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.primaryVariant
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = songInfo.album,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.primaryVariant
                )
            }
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
fun SongPreview() {
    MyMusicPlayerTheme {
        Song(SampleData.Songs[0])
    }
}

@Preview(
    showBackground = true,
    name = "Song List Light Mode"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Song List Dark Mode"
)
@Composable
fun SongListPreview() {
    MyMusicPlayerTheme {
         SongList(SampleData.Songs)
    }
}