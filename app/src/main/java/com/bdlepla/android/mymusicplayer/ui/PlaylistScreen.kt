package com.bdlepla.android.mymusicplayer.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bdlepla.android.mymusicplayer.SampleData
import com.bdlepla.android.mymusicplayer.business.PlaylistInfo
import com.bdlepla.android.mymusicplayer.extensions.toImagePainter
import com.bdlepla.android.mymusicplayer.ui.theme.MyMusicPlayerTheme

@Composable
fun PlaylistScreen(
    playlistList: List<PlaylistInfo>,
    onClick: (PlaylistInfo) -> Unit = emptyFunction1(),
    onAddNewPlaylist: () -> Unit = emptyFunction()){
    Box {
        FloatingActionButton(
            onClick = onAddNewPlaylist,
            modifier = Modifier
                .align(alignment = Alignment.BottomEnd)
                .padding(40.dp)
        ) {
            Icon(Icons.Filled.Add, "")
        }
        Column {
            Text(
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                text = "Playlists",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(modifier = Modifier.padding(all = 4.dp))
            Divider(color = MaterialTheme.colorScheme.primary)
            PlaylistList(playlistList, onClick)
        }
    }
}

@Composable
fun PlaylistList(
    playlistList: List<PlaylistInfo>,
    onClick: (PlaylistInfo) -> Unit = emptyFunction1()) {
    val listState = rememberLazyListState()
    Column {
        Box(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()) {
            LazyColumn(state = listState) {
                items(items = playlistList, key = { it.name }) { playlistInfo ->
                    Playlist(playlistInfo, onClick)
                    Divider(color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
fun Playlist(playlistInfo: PlaylistInfo, onClick: (PlaylistInfo)->Unit=emptyFunction1()) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(playlistInfo) }
            .padding(all = 4.dp)
            .semantics(mergeDescendants = true) {}) {
        Spacer(modifier = Modifier.padding(vertical = 4.dp))
        Image(
            painter = playlistInfo.artwork.toImagePainter(),
            contentDescription = playlistInfo.name,
            modifier = Modifier.size(50.dp)
        )
        Spacer(modifier = Modifier.padding(all = 4.dp))
        Column(modifier = Modifier.padding(all = 4.dp)) {
            Text(
                text = playlistInfo.name,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "${playlistInfo.songs.count()} songs",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
    }
}


@Preview(
    showBackground = true,
    name="Playlist Light Mode"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Playlist Dark Mode"
)
@Composable
fun PlaylistPreview() {
    MyMusicPlayerTheme {
        Playlist(SampleData().playlists[0])

    }
}

@Preview(
    showBackground = true,
    name = "Playlist List Light Mode"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Playlist List Dark Mode"
)
@Composable
fun PlaylistListPreview() {
    MyMusicPlayerTheme {
        PlaylistList(SampleData().playlists)
    }
}