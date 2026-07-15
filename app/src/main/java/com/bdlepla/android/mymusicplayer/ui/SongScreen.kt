package com.bdlepla.android.mymusicplayer.ui

import android.content.res.Configuration
import androidx.activity.compose.ReportDrawn
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bdlepla.android.mymusicplayer.SampleData
import com.bdlepla.android.mymusicplayer.business.SongInfo
import com.bdlepla.android.mymusicplayer.extensions.toImagePainter
import com.bdlepla.android.mymusicplayer.ui.theme.MyMusicPlayerTheme
import com.danrusu.pods4k.immutableArrays.ImmutableArray
import com.danrusu.pods4k.immutableArrays.asList
import com.danrusu.pods4k.immutableArrays.immutableArrayOf
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SongList(
    songInfos: ImmutableArray<SongInfo>,
    onLongClick: (ImmutableArray<SongInfo>) -> Unit = emptyFunction1(),
    currentSongIndex: Int = -1
) {

    val listState = rememberLazyListState()
    LazyColumn(state = listState) {
        items(items = songInfos.asList(), key = { it.songId }) { songInfo ->
            SongWithImage(
                songInfo = songInfo,
                onLongClick = { onLongClick(immutableArrayOf(it)) }
            )
            HorizontalDivider(thickness = 10.dp, color = MaterialTheme.colorScheme.background)
        }
    }

    if (currentSongIndex != -1) {
        LaunchedEffect(currentSongIndex) {
            delay(250.milliseconds)
            listState.animateScrollToItem(currentSongIndex)
        }
    }
    ReportDrawn()
}


@Composable
fun Song(songInfo: SongInfo) {
    Column(modifier = Modifier.padding(all = 4.dp)) {
        Text(
            text = songInfo.title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "${songInfo.album} by ${songInfo.artist}",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SongWithImage(
    songInfo: SongInfo,
    onLongClick: (SongInfo) -> Unit = emptyFunction1()
){
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .combinedClickable (
                onClick = { },
                onLongClick = { onLongClick(songInfo) }
            )
            .semantics(mergeDescendants = true) {}) {
        Spacer(modifier = Modifier.padding(vertical = 4.dp))
        Image(
            painter = songInfo.albumArt.toImagePainter(),
            contentDescription = songInfo.title,
            modifier = Modifier.size(50.dp),
        )
        Spacer(modifier = Modifier.padding(all = 4.dp))
        Song(songInfo)
    }
}

@Preview(
    showBackground = true,
    name="Song with Image Light Mode"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Song with Image Dark Mode"
)
@Composable
fun SongWithImagePreview() {
    MyMusicPlayerTheme {
        SongWithImage(SampleData().songs[0])
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
         SongList(SampleData().songs)
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
        Song(SampleData().songs[0])
    }
}