package com.bdlepla.android.mymusicplayer.ui

import android.content.res.Configuration
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.bdlepla.android.mymusicplayer.SampleData
import com.bdlepla.android.mymusicplayer.business.SongInfo
import com.bdlepla.android.mymusicplayer.extensions.toImagePainter
import com.bdlepla.android.mymusicplayer.ui.theme.MyMusicPlayerTheme
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

enum class DragAnchors {
    //Start,
    Center,
    End,
}

@Composable
fun SongList(songInfos: List<SongInfo>,
             onClick: (SongInfo) -> Unit = emptyFunction1(),
             onAddSongToPlaylist:(List<SongInfo>)->Unit = emptyFunction1(),
             currentSongIndex : Int = -1
) {
    val listState = rememberLazyListState()
    LazyColumn(state = listState) {
        items(items = songInfos, key = { it.songId }) { songInfo ->
            SongWithImage(songInfo, onClick, onAddSongToPlaylist, true)
            HorizontalDivider(thickness = 10.dp, color = MaterialTheme.colorScheme.background)
        }
    }

    if (currentSongIndex != -1) {
        LaunchedEffect(currentSongIndex) {
            delay(250)
            listState.animateScrollToItem(currentSongIndex)
        }
    }
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
fun SongWithImage(songInfo: SongInfo,
                  onClick: (SongInfo) -> Unit = emptyFunction1(),
                  onAddSongToPlaylist: (List<SongInfo>) -> Unit = emptyFunction1(),
                  swipeEnabled: Boolean = false) {

    val density = LocalDensity.current
    val defaultActionSize = 80.dp
    val endActionSizePx = with(density) { (defaultActionSize).toPx() }
    //val startActionSizePx = with(density) { 0.dp.toPx() }

    val state = remember {
        AnchoredDraggableState(
            initialValue = DragAnchors.Center,
            anchors = DraggableAnchors {
                DragAnchors.Center at 0f
                DragAnchors.End at endActionSizePx
            },
            positionalThreshold = { distance: Float -> distance * 0.5f },
            velocityThreshold = { with(density) { 100.dp.toPx() } },
            animationSpec = tween(),
        )
    }

    SwipeableItem(
        state = state,
        endAction = {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .offset {
                        IntOffset(
                            ((-state
                                .requireOffset()) + endActionSizePx)
                                .roundToInt(), 0
                        )
                    }
            )
            {
                SwipeAction(
                    modifier = Modifier
                        .width(defaultActionSize)
                        .combinedClickable(onClick = {
                            onAddSongToPlaylist(listOf(songInfo))
                        }),
                    "",
                    Color.Blue,
                    Icons.Filled.Add
                )
            } },
        content = {
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.background)
                    .fillMaxWidth()
                    .combinedClickable(
                        onClick = { onClick(songInfo) }
                    )
                    .padding(all = 4.dp)
                    .offset{
                        IntOffset(
                        x = -state
                            .requireOffset()
                            .roundToInt(),
                        y = 0)
                    }
                    .anchoredDraggable(state, Orientation.Horizontal, reverseDirection = true, enabled = swipeEnabled)
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
    )
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
         SongList(SampleData().songs )
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