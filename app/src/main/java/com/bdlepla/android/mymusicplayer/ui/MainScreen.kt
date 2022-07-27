package com.bdlepla.android.mymusicplayer.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.session.MediaController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bdlepla.android.mymusicplayer.MyMusicViewModel
import com.bdlepla.android.mymusicplayer.SampleData
import com.bdlepla.android.mymusicplayer.business.AlbumInfo
import com.bdlepla.android.mymusicplayer.business.ArtistInfo
import com.bdlepla.android.mymusicplayer.business.CurrentPlayingStats
import com.bdlepla.android.mymusicplayer.business.SongInfo
import com.bdlepla.android.mymusicplayer.ui.theme.MyMusicPlayerTheme

// Main Screen uses the view model to set the variables and callbacks
// to view model. This is so I can see previews for everything in
// the MainContents and its components.
@Composable
internal fun MainScreen(viewModel: MyMusicViewModel=viewModel()) {
    val songs by viewModel.allSongs.collectAsState()
    val artists by viewModel.allArtists.collectAsState()
    val albums by viewModel.allAlbums.collectAsState()

    val currentlyPlaying by viewModel.currentlyPlaying.collectAsState()
    val currentlyPlayingStats by viewModel.currentlyPlayingStats.collectAsState()

    val isPaused by viewModel.isPaused.collectAsState()
    val onSongClick: (SongInfo, List<SongInfo>, Boolean) -> Unit = { itSong, itSongs, itShuffle ->
        viewModel.setPlaylist(itSongs)
        viewModel.setCurrentlyPlaying(itSong)
    }
    val onShuffleClick: ()->Unit = {
        val shuffledSongs = songs.shuffled()
        viewModel.setPlaylist(shuffledSongs)
        viewModel.play()
    }
    val onRepeatClick: ()->Unit = { viewModel.toggleRepeat() }
    val onPlayPauseClick: ()->Unit = { viewModel.togglePlayPause() }
    val onNextClick:()->Unit = { viewModel.playNext() }

    MainContent(
        viewModel.browser,
        songs,
        artists,
        albums,
        onSongClick,
        onShuffleClick,
        onRepeatClick,
        onPlayPauseClick,
        onNextClick,
        currentlyPlaying,
        currentlyPlayingStats,
        isPaused
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainContent(
    mediaController: MediaController?,
    songs: List<SongInfo>,
    artists: List<ArtistInfo>,
    albums: List<AlbumInfo>,
    onSongClick: (SongInfo, List<SongInfo>, Boolean) -> Unit = emptyFunction3(),
    onShuffleClick: () -> Unit = emptyFunction(),
    onRepeatClick: () -> Unit = emptyFunction(),
    onPlayPauseClick: () -> Unit = emptyFunction(),
    onNextClick: () -> Unit = emptyFunction(),
    currentlyPlaying: SongInfo? = null,
    currentPlayingStats: CurrentPlayingStats? = null,
    isPaused: Boolean = false
) {

//    val myOnSongClick: (ISongInfo, List<ISongInfo>, Boolean)->Unit = {
//        song, songs, shuffle ->
//            onSongClick(song, songs, shuffle)
//            onCategoryClick(largePlayerId)
//    }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    //val currentRoute = navBackStackEntry?.destination?.route
    MyMusicPlayerTheme {
        Surface {
            Scaffold(
                topBar = { TopAppBar(onShuffleClick, onRepeatClick) },
                bottomBar = {
                    BottomAppBar(
                        onPlayPauseClick,
                        onNextClick, currentlyPlaying, isPaused,
                        navController
                    )
                }
            ) { paddingValues ->
                // A surface container using the 'background' color from the theme
                Column(modifier = Modifier.padding(paddingValues)) {
                    Navigation(navController, mediaController, songs, artists, albums,
                    onSongClick, currentlyPlaying, currentPlayingStats, isPaused)
                }
            }
        }
    }
}

@Composable
fun Navigation(
    navController: NavHostController,
    mediaController: MediaController?,
    songs: List<SongInfo>,
    artists: List<ArtistInfo>,
    albums: List<AlbumInfo>,
    onSongClick: (SongInfo, List<SongInfo>, Boolean) -> Unit = emptyFunction3(),
    currentlyPlaying: SongInfo? = null,
    currentPlayingStats: CurrentPlayingStats? = null,
    isPaused: Boolean = false

) {
    NavHost(navController, startDestination = NavigationItem.Songs.route) {
        composable(NavigationItem.Songs.route) {
            SongList(songs, onSongClick)
        }
        composable(NavigationItem.Artists.route) {
            ArtistList(artists, albums, songs, onSongClick)
        }
        composable(NavigationItem.Albums.route) {
            AlbumList(albums, songs, onSongClick)
        }
        composable(NavigationItem.Playing.route) {
            if (currentlyPlaying != null)
                PlayScreen(currentlyPlaying, currentPlayingStats, isPaused, mediaController)
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


