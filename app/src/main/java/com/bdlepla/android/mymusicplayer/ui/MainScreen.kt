package com.bdlepla.android.mymusicplayer.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.session.MediaController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
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

    val currentlyPlayingStats by viewModel.currentlyPlayingStats.collectAsState()

    val isPaused by viewModel.isPaused.collectAsState()
    val onSongClick: (SongInfo, List<SongInfo>) -> Unit = { itSong, itSongs ->
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
    onSongClick: (SongInfo, List<SongInfo>) -> Unit = emptyFunction2(),
    onShuffleClick: () -> Unit = emptyFunction(),
    onRepeatClick: () -> Unit = emptyFunction(),
    onPlayPauseClick: () -> Unit = emptyFunction(),
    onNextClick: () -> Unit = emptyFunction(),
    currentPlayingStats: CurrentPlayingStats? = null,
    isPaused: Boolean = false
) {

//    val myOnSongClick: (ISongInfo, List<ISongInfo>, Boolean)->Unit = {
//        song, songs, shuffle ->
//            onSongClick(song, songs, shuffle)
//            onCategoryClick(largePlayerId)
//    }

    val navController = rememberNavController()
    //val navBackStackEntry by navController.currentBackStackEntryAsState()
    //val currentRoute = navBackStackEntry?.destination?.route
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
                    onSongClick, currentPlayingStats, isPaused)
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
    onSongClick: (SongInfo, List<SongInfo>) -> Unit = emptyFunction2(),
    currentPlayingStats: CurrentPlayingStats? = null,
    isPaused: Boolean = false

) {
    NavHost(navController, startDestination = NavigationItem.Songs.route) {
        composable(NavigationItem.Songs.route) {
            val myOnClick:(SongInfo)->Unit = {
                onSongClick(it, songs)
            }
            SongList(songs, myOnClick)
        }
        composable(NavigationItem.Artists.route) {
            ArtistList(artists, navController)
        }
        composable("artistsongs/{artistId}") {
            val backStack = navController.currentBackStackEntry?: return@composable
            val args = backStack.arguments ?: return@composable
            val artistIdArg = args["artistId"] ?: return@composable
            val artistId = artistIdArg.toString().toLong()
            val theArtist = artists.firstOrNull{it.artistId == artistId} ?: return@composable
            val songsForArtist = songs
                .filter { it.artistId == artistId}
                .sortedBy { it.albumYear * 1_000 + it.trackNumber }
//                .onEach{
//                    val title = it.title
//                    val year = it.albumYear
//                    val track = it.trackNumber
//                    val message = "$year $track"
//                }
            val myOnClick:(SongInfo)->Unit = {
                onSongClick(it, songsForArtist)
            }
            ArtistSongsScreen(theArtist, songsForArtist, myOnClick)
        }
        composable(NavigationItem.Albums.route) {
            AlbumList(albums, navController)
        }
        composable("albumsongs/{albumId}") {
            val backStack = navController.currentBackStackEntry?: return@composable
            val args = backStack.arguments ?: return@composable
            val albumIdArg = args["albumId"] ?: return@composable
            val albumId = albumIdArg.toString().toLong()
            val theAlbum = albums.firstOrNull{it.albumId == albumId} ?: return@composable
            val songsInAlbum = songs
                .filter { it.albumId == albumId }
                .sortedBy { it.trackNumber }
            val myOnClick:(SongInfo)->Unit = {
                onSongClick(it, songsInAlbum)
            }
            AlbumSongsScreen(theAlbum, songsInAlbum, myOnClick)
        }
        composable(NavigationItem.Playing.route) {
                PlayScreen(currentPlayingStats, isPaused, mediaController)
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


