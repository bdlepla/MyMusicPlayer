package com.bdlepla.android.mymusicplayer.ui

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.bdlepla.android.mymusicplayer.SampleData
import com.bdlepla.android.mymusicplayer.business.PlaylistInfo
import com.bdlepla.android.mymusicplayer.extensions.count
import com.bdlepla.android.mymusicplayer.extensions.toHourMinutesSeconds
import com.bdlepla.android.mymusicplayer.extensions.toImagePainter
import com.bdlepla.android.mymusicplayer.ui.theme.MyMusicPlayerTheme
import com.danrusu.pods4k.immutableArrays.ImmutableArray
import com.danrusu.pods4k.immutableArrays.asList

@Composable
fun PlaylistScreen(
    playlistList: ImmutableArray<PlaylistInfo>,
    onClick: (PlaylistInfo) -> Unit = emptyFunction1(),
    onCreateNewPlaylist: (String) -> Unit = emptyFunction1(),
    onRemovePlaylist: (PlaylistInfo) -> Unit = emptyFunction1()){

    val nameNewPlaylist = remember { mutableStateOf(false) }
    val savedPlaylistNameToAdd = remember { mutableStateOf("") }
    val onAddNewPlaylist: ()->Unit = {
        savedPlaylistNameToAdd.value = ""
        nameNewPlaylist.value = true
    }

    val onLongPress: (PlaylistInfo)->Unit = onRemovePlaylist

    if (nameNewPlaylist.value) {
        MyMusicPlayerTheme {
            Dialog(onDismissRequest = { nameNewPlaylist.value = false }) {

                val onTextBoxUpdate: (String) -> Unit = {
                    savedPlaylistNameToAdd.value = it
                }

                val onAddButtonClick:() -> Unit = {
                    onCreateNewPlaylist( savedPlaylistNameToAdd.value)
                    nameNewPlaylist.value = false
                }

                Box(
                    Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .size(500.dp, 300.dp)) {
                    Column(modifier=Modifier.align(Alignment.Center)) {
                        Row {
                            Spacer(modifier = Modifier.padding(all = 4.dp))
                            androidx.compose.material.Text(
                                text = "Name New Playlist",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                        Spacer(modifier = Modifier.padding(all = 4.dp))
                        androidx.compose.material.TextField(
                            value=savedPlaylistNameToAdd.value,
                            onValueChange = onTextBoxUpdate)
                        Spacer(modifier = Modifier.padding(all = 4.dp))
                        androidx.compose.material.Button(onClick = onAddButtonClick) {
                            androidx.compose.material.Text("Add")
                        }
                    }
                }
            }
        }
    }

    Scaffold (
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddNewPlaylist,
                containerColor = MaterialTheme.colorScheme.secondaryContainer) {

                Icon(Icons.Filled.Add, "")
            }
        },
        content = {
            Column(Modifier.padding(it)) {
                Text(
                    modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                    text = "Playlists",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.padding(all = 4.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.primary)
                PlaylistList(playlistList, onClick, onLongPress)
            }
        }
    )
}

@Composable
fun PlaylistList(
    playlistList: ImmutableArray<PlaylistInfo>,
    onClick: (PlaylistInfo) -> Unit = emptyFunction1(),
    onLongPress: (PlaylistInfo)->Unit=emptyFunction1()) {
    val listState = rememberLazyListState()
    Column {
        Box(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()) {
            LazyColumn(state = listState) {
                items(items = playlistList.asList(), key = { it.name }) { playlistInfo ->
                    Playlist(playlistInfo, onClick, onLongPress)
                    HorizontalDivider(
                        thickness = 10.dp,
                        color = MaterialTheme.colorScheme.background
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Playlist(playlistInfo: PlaylistInfo,
             onClick: (PlaylistInfo)->Unit=emptyFunction1(),
             onLongPress: (PlaylistInfo)->Unit=emptyFunction1()) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(color = MaterialTheme.colorScheme.background)
            .fillMaxWidth()
            .combinedClickable(
                onClick = { onClick(playlistInfo) },
                onLongClick = {
                    onLongPress(playlistInfo)
                }
            )
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
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "${playlistInfo.songs.count()} songs",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
            )
            Text(
                text = playlistInfo.songs.sumOf{it.duration}.toHourMinutesSeconds() + " Total time",
                style = MaterialTheme.typography.bodyMedium,
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