package com.bdlepla.android.mymusicplayer.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.session.MediaController
import androidx.navigation.compose.rememberNavController
import com.bdlepla.android.mymusicplayer.MyMusicViewModel
import com.bdlepla.android.mymusicplayer.Navigation
import com.bdlepla.android.mymusicplayer.SampleData
import com.bdlepla.android.mymusicplayer.business.*
import com.bdlepla.android.mymusicplayer.ui.theme.MyMusicPlayerTheme

// Main Screen uses the view model to set the variables and callbacks
// to view model. This is so I can see previews for everything in
// the MainContents and its components.
@Composable
internal fun MainScreen(viewModel: MyMusicViewModel=viewModel()) {
    val songs by viewModel.allSongs.collectAsState()
    val artists by viewModel.allArtists.collectAsState()
    val albums by viewModel.allAlbums.collectAsState()
    val playlists by viewModel.allPlaylists.collectAsState()
    val currentlyPlayingStats by viewModel.currentlyPlayingStats.collectAsState()
    val isPaused by viewModel.isPaused.collectAsState()

    val songsByScreen = remember { mutableStateOf(songs) }
    val onSongClick: (SongInfo, List<SongInfo>) -> Unit = { itSong, itSongs ->
        viewModel.setPlaylist(itSongs)
        viewModel.setCurrentlyPlaying(itSong)
    }
    val onShuffleClick: ()->Unit = {
        val shuffledSongs = songsByScreen.value.shuffled()
        viewModel.setPlaylist(shuffledSongs)
        viewModel.play()
    }
    val onRepeatClick: ()->Unit = { viewModel.toggleRepeat() }
    val onPlayPauseClick: ()->Unit = { viewModel.togglePlayPause() }
    val onNextClick:()->Unit = { viewModel.playNext() }

    val setSongListForScreen: (songs:List<SongInfo>)->Unit = {
        songsByScreen.value = it
    }

    MainContent(
        viewModel.browser,
        songs,
        artists,
        albums,
        playlists,
        setSongListForScreen,
        onSongClick,
        onShuffleClick,
        onRepeatClick,
        onPlayPauseClick,
        onNextClick,
        currentlyPlayingStats,
        isPaused
    )
}

@Composable
private fun MainContent(
    mediaController: MediaController?,
    songs: List<SongInfo>,
    artists: List<ArtistInfo>,
    albums: List<AlbumInfo>,
    playlists: List<PlaylistInfo>,
    setSongsForScreen: (List<SongInfo>) -> Unit = emptyFunction1(),
    onSongClick: (SongInfo, List<SongInfo>) -> Unit = emptyFunction2(),
    onShuffleClick: () -> Unit = emptyFunction(),
    onRepeatClick: () -> Unit = emptyFunction(),
    onPlayPauseClick: () -> Unit = emptyFunction(),
    onNextClick: () -> Unit = emptyFunction(),
    currentPlayingStats: CurrentPlayingStats? = null,
    isPaused: Boolean = false
) {
    val navController = rememberNavController()
    MyMusicPlayerTheme {
        Surface {
            Scaffold(
                topBar = { TopAppBar(onShuffleClick, onRepeatClick) },
                bottomBar = {
                    BottomAppBar(
                        onPlayPauseClick,
                        onNextClick,
                        currentPlayingStats?.currentPlaying,
                        isPaused,
                        navController
                    )
                }
            ) { paddingValues ->
                // A surface container using the 'background' color from the theme
                Column(modifier = Modifier.padding(paddingValues)) {
                    Navigation(navController, mediaController, songs, artists, albums,
                    playlists, setSongsForScreen, onSongClick, currentPlayingStats, isPaused)
                }
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
            SampleData().albums,
            SampleData().playlists
        )
    }
}


