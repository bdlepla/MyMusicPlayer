package com.bdlepla.android.mymusicplayer.ui

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bdlepla.android.mymusicplayer.Extensions.toImagePainter
import com.bdlepla.android.mymusicplayer.SampleData
import com.bdlepla.android.mymusicplayer.business.AlbumInfo
import com.bdlepla.android.mymusicplayer.business.ISongInfo
import com.bdlepla.android.mymusicplayer.ui.theme.MyMusicPlayerTheme

@Composable
fun AlbumList(
    AlbumList:List<AlbumInfo>,
    songList:List<ISongInfo>,
    onSongClick:(ISongInfo, List<ISongInfo>, Boolean)->Unit = emptyFunction3()) {
    val listState = rememberLazyListState()
    val selectedAlbum: MutableState<AlbumInfo?> = remember {mutableStateOf(null) }
    val onClick: (AlbumInfo)->Unit = { selectedAlbum.value = it }

    BackHandler(enabled = selectedAlbum.value != null) {
        selectedAlbum.value = null
    }

    if (selectedAlbum.value != null) {
        val theAlbum = selectedAlbum.value!!
        val albumId = theAlbum.albumId
        val songsInAlbum = songList
            .filter { it.albumId == albumId }
            .sortedBy { it.trackNumber }
        val myOnClick:(ISongInfo)->Unit = {
            onSongClick(it, songsInAlbum, false)
        }
        AlbumSongsScreen(theAlbum, songsInAlbum, myOnClick)
    } else {
        LazyColumn(state = listState) {
            items(items = AlbumList, key = { it.albumId }) { albumInfo ->
                Album(albumInfo, onClick)
                Divider(color = MaterialTheme.colors.primary)
            }
        }
    }
}

@Composable
fun Album(albumInfo:AlbumInfo, onClick: (AlbumInfo) -> Unit = emptyFunction1()) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(albumInfo) }
            .padding(all = 4.dp)
            .semantics(mergeDescendants = true) {}) {
        Image(
            painter = albumInfo.albumArt.toImagePainter(),
            contentDescription = albumInfo.name,
            modifier = Modifier.size(50.dp)
        )
        Spacer(modifier=Modifier.padding(all=4.dp))
        Text(
            text = albumInfo.name,
            style = MaterialTheme.typography.subtitle2,
            color = MaterialTheme.colors.secondaryVariant
        )
    }
}

@Preview(
    showBackground = true,
    name="Album Light Mode"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Album Dark Mode"
)
@Composable
fun AlbumPreview() {
    MyMusicPlayerTheme {
        Album(SampleData.Albums[0])
    }
}

@Preview(
    showBackground = true,
    name = "Album List Light Mode"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Album List Dark Mode"
)
@Composable
fun AlbumListPreview() {
    MyMusicPlayerTheme {
        AlbumList(SampleData.Albums, emptyList())
    }
}

const val TAG = "AlbumScreen"