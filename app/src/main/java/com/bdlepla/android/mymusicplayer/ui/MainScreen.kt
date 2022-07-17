package com.bdlepla.android.mymusicplayer.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.session.MediaController
import com.bdlepla.android.mymusicplayer.MyMusicViewModel
import com.bdlepla.android.mymusicplayer.R
import com.bdlepla.android.mymusicplayer.SampleData
import com.bdlepla.android.mymusicplayer.business.AlbumInfo
import com.bdlepla.android.mymusicplayer.business.ArtistInfo
import com.bdlepla.android.mymusicplayer.business.SongInfo
import com.bdlepla.android.mymusicplayer.ui.theme.MyMusicPlayerTheme

// Main Screen uses the viewmodel to set the variables and callbacks
// to view model. This is so I can see previews for everything in
// the MainContents and its components.
@Composable
internal fun MainScreen(viewModel: MyMusicViewModel=viewModel()) {
    var selectedCategory by remember { mutableStateOf(0) }
    val songs = viewModel.allSongs.collectAsState()
    val artists = viewModel.allArtists.collectAsState()
    val albums = viewModel.allAlbums.collectAsState()

    val currentlyPlaying by viewModel.currentlyPlaying.collectAsState()
    val isPaused by viewModel.isPaused.collectAsState()
    val onSongClick: (SongInfo, List<SongInfo>, Boolean) -> Unit = { itSong, itSongs, itShuffle ->
        viewModel.setPlaylist(itSongs)
        viewModel.setCurrentlyPlaying(itSong)
    }
    val onCategoryClick:(Int)->Unit = { selectedCategory = it }
    val onShuffleClick: ()->Unit = {
        viewModel.setPlaylist(songs.value)
        viewModel.toggleShuffle()
        viewModel.play()
    }
    val onRepeatClick: ()->Unit = { viewModel.toggleRepeat() }
    val onPlayPauseClick: ()->Unit = { viewModel.togglePlayPause() }
    val onNextClick:()->Unit = { viewModel.playNext() }

    MainContent(
        null,
        songs.value,
        artists.value,
        albums.value,
        selectedCategory,
        onSongClick,
        onShuffleClick,
        onRepeatClick,
        onCategoryClick,
        onPlayPauseClick,
        onNextClick,
        currentlyPlaying,
        isPaused)
}

val categoryItems = listOf("Songs", "Artists", "Albums", "Playing")//, "Genres", "Playlists")
val songsId = 0
val artistsId = 1
val albumsId = 2
val playingId = 3 //categoryItems.count()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainContent(
    mediaController: MediaController?,
    songs:List<SongInfo>,
    artists:List<ArtistInfo>,
    albums:List<AlbumInfo>,
    categorySelected:Int=0,
    onSongClick:(SongInfo, List<SongInfo>, Boolean)->Unit= emptyFunction3(),
    onShuffleClick:()->Unit = emptyFunction(),
    onRepeatClick:()->Unit = emptyFunction(),
    onCategoryClick:(Int)->Unit = emptyFunction1(),
    onPlayPauseClick:()->Unit = emptyFunction(),
    onNextClick:()->Unit = emptyFunction(),
    currentlyPlaying:SongInfo? = null,
    isPaused:Boolean = false) {

//    val myOnSongClick: (ISongInfo, List<ISongInfo>, Boolean)->Unit = {
//        song, songs, shuffle ->
//            onSongClick(song, songs, shuffle)
//            onCategoryClick(largePlayerId)
//    }

    MyMusicPlayerTheme {
        Surface {
            Scaffold(
                topBar = { TopAppBar(onShuffleClick, onRepeatClick) },
                bottomBar = {
                    BottomAppBar(
                        categoryItems, categorySelected, onCategoryClick,
                        onPlayPauseClick, onNextClick, currentlyPlaying, isPaused
                    )
                }
            ) { paddingValues ->
                // A surface container using the 'background' color from the theme
                Column(modifier = Modifier.padding(paddingValues)) {
                    when (categorySelected) {
                        artistsId -> ArtistList(artists, albums, songs, onSongClick)
                        albumsId -> AlbumList(albums, songs, onSongClick)
                        playingId -> { if (currentlyPlaying != null) PlayScreen2(mediaController)}
                        else -> SongList(songs, onSongClick)
                    }
                }
            }
        }
    }
}

@Composable
private fun TopAppBar(
    onShuffleClick: ()->Unit = emptyFunction(),
    onRepeatClick: ()->Unit = emptyFunction()) {
    Column {
        SmallTopAppBar(
            title = {
                Text(
                    text = "My Music Player",
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            actions = {
                IconButton(onClick = { onShuffleClick() }) {
                    Icon(painter=painterResource(id = R.drawable.ic_shuffle), contentDescription = "Shuffle")
                }
                IconButton(onClick = { onRepeatClick() }) {
                    Icon(painter=painterResource(id = R.drawable.ic_repeat), contentDescription = "Repeat")
                }
                IconButton(onClick = { /*TODO*/ }) {
                    Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                }
            }
        )
    }
}

@Composable
private fun BottomAppBar(
    items:List<String>,
    selected:Int=0,
    onCategoryClick:(Int)->Unit= emptyFunction1(),
    onPlayPauseClick:()->Unit= emptyFunction(),
    onNextClick:()->Unit= emptyFunction(),
    currentlyPlaying: SongInfo? = null,
    isPaused:Boolean = true
        ) {
    val isCurrentlyPlaying = currentlyPlaying != null

    Column {
        if (isCurrentlyPlaying) {
            val onClickSmallToLargePlayer: (SongInfo, List<SongInfo>, Boolean)->Unit = {
                    _, _, _ ->
            }
            CurrentlyPlayingSmallScreen(
                currentlyPlaying!!,
                isPaused,
                onPlayPauseClick,
                onNextClick,
            )
        }

        NavigationBar {
            items.forEachIndexed { index, item ->
                BottomNavigationItem(
                    icon = { Icon(Icons.Filled.Info, contentDescription = null) },
                    label = { Text(item) },
                    selected = selected == index,
                    onClick = { onCategoryClick(index) }
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    name="Main Content Light Mode"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Main Content Dark Mode"
)

@Composable
fun MainContentPreview() {
    MyMusicPlayerTheme {
        MainContent(
            null,
            SampleData().songs,
            SampleData().artists,
            SampleData().albums
        )
    }
}


@Preview(
    showBackground = true,
    name="Top App Bar Light Mode"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Top App Bar Dark Mode"
)

@Composable
fun TopAppBarPreview() {
    MyMusicPlayerTheme {
        TopAppBar()
    }
}


@Preview(
    showBackground = true,
    name="Bottom App Bar Light Mode"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Bottom App Bar Dark Mode"
)

@Composable
fun BottomAppBarPreview() {
    MyMusicPlayerTheme {
        BottomAppBar(listOf("Songs", "Artists", "Albums"))
    }
}

@Preview(
    showBackground = true,
    name="Bottom App Bar With Small Player Light Mode"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Bottom App Bar With Small Player Dark Mode"
)

@Composable
fun BottomAppBarWithCurrentlyPlayingPreview() {
    MyMusicPlayerTheme {
        BottomAppBar(listOf("Songs", "Artists", "Albums"), currentlyPlaying = SampleData().songs[0])
    }
}