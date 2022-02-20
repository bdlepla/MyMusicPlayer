package com.bdlepla.android.mymusicplayer.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bdlepla.android.mymusicplayer.MyMusicViewModel
import com.bdlepla.android.mymusicplayer.business.SongInfo
import com.bdlepla.android.mymusicplayer.ui.theme.MyMusicPlayerTheme

@Composable
internal fun MainScreen(viewModel: MyMusicViewModel) {
    var selectedCategory by remember { mutableStateOf(0) }

    val items = listOf("Songs", "Artists", "Albums")//, "Genres", "Playlists")
    MyMusicPlayerTheme {
        Scaffold (
            topBar = { TopAppBar() },
            bottomBar = { BottomAppBar(viewModel, items, selectedCategory) { selectedCategory = it }}
        ){ paddingValues ->
            MainContent(paddingValues, viewModel, selectedCategory)
        }
    }
}

@Composable
private fun MainContent(padding: PaddingValues, viewModel: MyMusicViewModel, categorySelected:Int) {
    val songs = viewModel.allSongs.collectAsState()
    val artists = viewModel.allArtists.collectAsState()
    val albums = viewModel.allAlbums.collectAsState()

    val onSongClick: (SongInfo) -> Unit = {
        viewModel.setCurrentlyPlaying(it)
    }

    // A surface container using the 'background' color from the theme
    Column(modifier=Modifier.padding(padding)) {
        Surface(color = MaterialTheme.colors.background) {
            when (categorySelected) {
                1 -> ArtistList(artists.value)
                2 -> AlbumList(albums.value)
                else -> SongList(songs.value, onSongClick)
            }
        }
    }
}


@Composable
private fun TopAppBar() {
    TopAppBar(
        title = {
            Text(text = "My Music Player",
                style = MaterialTheme.typography.h3)

        },
        actions = {

            IconButton(onClick = { /* doSomething() */ }) {
                Icon(Icons.Filled.Favorite, contentDescription = null)
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Filled.Refresh, contentDescription = null)
            }
        }
    )
}

@Composable
private fun BottomAppBar(viewModel: MyMusicViewModel, items:List<String>, selected:Int, onClicked:(Int)->Unit) {
    val currentlyPlaying by viewModel.currentlyPlaying.collectAsState()
    val isPaused by viewModel.isPaused.collectAsState()

    Column {
        if (currentlyPlaying != null) {
            CurrentlyPlayingSmallScreen(currentlyPlaying!!, isPaused) { viewModel.togglePlayPause() }
        }
        BottomNavigation {
            items.forEachIndexed { index, item ->
                BottomNavigationItem(
                    icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                    label = { Text(item) },
                    selected = selected == index,
                    onClick = { onClicked(index) }
                )
            }
        }
    }
}




@Preview(
    showBackground = true,
    name="Main Screen Light Mode"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Main Screen Dark Mode"
)

@androidx.media3.common.util.UnstableApi
@Composable
fun DefaultPreview() {
   MainScreen(viewModel = viewModel())
}