package com.bdlepla.android.mymusicplayer.ui

import android.content.res.Configuration
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bdlepla.android.mymusicplayer.SampleData
import com.bdlepla.android.mymusicplayer.business.AlbumInfo
import com.bdlepla.android.mymusicplayer.business.SongInfo
import com.bdlepla.android.mymusicplayer.extensions.toImagePainter
import com.bdlepla.android.mymusicplayer.ui.theme.MyMusicPlayerTheme
import com.danrusu.pods4k.immutableArrays.ImmutableArray
import com.danrusu.pods4k.immutableArrays.asList

@Composable
fun AlbumList(
    albumList: ImmutableArray<AlbumInfo>,
    navController: NavController? = null,
    onLongPress: (ImmutableArray<SongInfo>) -> Unit = emptyFunction1()) {
    val listState = rememberLazyListState()
    val onClick: (AlbumInfo)->Unit = {
        val albumId = it.albumId
        val route = "albumsongs/${albumId}"
        navController?.navigate(route)
    }

    LazyColumn(state = listState) {
        items(items = albumList.asList(), key = { it.albumId }) { albumInfo ->
            Album(albumInfo, onClick, onLongPress)
            HorizontalDivider(thickness = 10.dp, color = MaterialTheme.colorScheme.background)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Album(
    albumInfo:AlbumInfo,
    onClick: (AlbumInfo) -> Unit = emptyFunction1(),
    onLongPress: (ImmutableArray<SongInfo>) -> Unit = emptyFunction1()) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(color=MaterialTheme.colorScheme.background)
            .fillMaxWidth()
            .combinedClickable (
                onClick = {onClick(albumInfo)},
                onLongClick = {onLongPress(albumInfo.songs)}
            )
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
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.secondary
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
        Album(SampleData().albums[0])
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
        AlbumList(SampleData().albums)
    }
}

//const val TAG = "AlbumScreen"