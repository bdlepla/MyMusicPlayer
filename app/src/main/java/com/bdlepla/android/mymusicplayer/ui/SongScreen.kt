package com.bdlepla.android.mymusicplayer.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bdlepla.android.mymusicplayer.Extensions.toImagePainter
import com.bdlepla.android.mymusicplayer.SampleData
import com.bdlepla.android.mymusicplayer.business.ISongInfo
import com.bdlepla.android.mymusicplayer.ui.theme.MyMusicPlayerTheme

@Composable
fun SongList(songInfos: List<ISongInfo>,
             onClick: (ISongInfo, List<ISongInfo>, Boolean) -> Unit = emptyFunction3()) {
    val listState = rememberLazyListState()
    val myOnClick: (ISongInfo)->Unit = {
        onClick(it, songInfos, false)
    }

    LazyColumn(state = listState) {
        items(items = songInfos, key = { it.songId }) { songInfo ->
            Song(songInfo, myOnClick)
            Divider(color = MaterialTheme.colors.primary)
        }
    }
}

@Composable
fun Song(songInfo: ISongInfo, onClick: (ISongInfo) -> Unit = emptyFunction1()) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(songInfo) }
            .padding(all = 4.dp)
            .semantics(mergeDescendants = true){}) {
        Spacer(modifier = Modifier.padding(vertical = 4.dp))
        Image(
            painter = songInfo.albumArt.toImagePainter(),
            contentDescription = songInfo.title,
            modifier = Modifier.size(50.dp)
        )
        Spacer(modifier = Modifier.padding(all = 4.dp))
        Column(modifier = Modifier.padding(all = 4.dp)) {
            Text(
                text = songInfo.title,
                style = MaterialTheme.typography.subtitle2,
                color = MaterialTheme.colors.primary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Column {
                    Text(
                        text = songInfo.artist,
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.secondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = songInfo.album,
                        style = MaterialTheme.typography.body2,
                        color = MaterialTheme.colors.secondaryVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
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
         SongList(SampleData.Songs )
    }
}