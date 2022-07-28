package com.bdlepla.android.mymusicplayer.ui

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bdlepla.android.mymusicplayer.Extensions.toImagePainter
import com.bdlepla.android.mymusicplayer.SampleData
import com.bdlepla.android.mymusicplayer.business.AlbumInfo
import com.bdlepla.android.mymusicplayer.business.ArtistInfo
import com.bdlepla.android.mymusicplayer.business.SongInfo
import com.bdlepla.android.mymusicplayer.ui.theme.MyMusicPlayerTheme

@Composable
fun AlbumList(
    AlbumList:List<AlbumInfo>,
    navController: NavController? = null) {
    val listState = rememberLazyListState()
    val onClick: (AlbumInfo)->Unit = {
        val albumId = it.albumId
        val route = "albumsongs/${albumId}"
        navController?.navigate(route)
    }

    LazyColumn(state = listState) {
        items(items = AlbumList, key = { it.albumId }) { albumInfo ->
            Album(albumInfo, onClick)
            Divider(color = MaterialTheme.colorScheme.primary)
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
            style = MaterialTheme.typography.bodyMedium,
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