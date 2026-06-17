package com.bdlepla.android.mymusicplayer.ui

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.session.MediaController
import androidx.navigation.compose.rememberNavController
import com.bdlepla.android.mymusicplayer.MyMusicViewModel
import com.bdlepla.android.mymusicplayer.Navigation
import com.bdlepla.android.mymusicplayer.R
import com.bdlepla.android.mymusicplayer.SampleData
import com.bdlepla.android.mymusicplayer.business.AlbumInfo
import com.bdlepla.android.mymusicplayer.business.ArtistInfo
import com.bdlepla.android.mymusicplayer.business.CurrentPlayingStats
import com.bdlepla.android.mymusicplayer.business.PlaylistInfo
import com.bdlepla.android.mymusicplayer.business.SongInfo
import com.bdlepla.android.mymusicplayer.extensions.any
import com.bdlepla.android.mymusicplayer.ui.theme.MyMusicPlayerTheme
import com.danrusu.pods4k.immutableArrays.ImmutableArray
import com.danrusu.pods4k.immutableArrays.emptyImmutableArray

// Main Screen uses the view model to set the variables and callbacks
// to view model. This is so I can see previews for everything in
// the MainContents and its components.
@Composable
internal fun MainScreen(viewModel: MyMusicViewModel=viewModel(), activity: Context) {
    val songs by viewModel.allSongs.collectAsState()
    val artists by viewModel.allArtists.collectAsState()
    val albums by viewModel.allAlbums.collectAsState()
    val playlists by viewModel.allPlaylists.collectAsState()
    val currentlyPlayingStats by viewModel.currentlyPlayingStats.collectAsState()
    val isPaused by viewModel.isPaused.collectAsState()
    val currentSongList by viewModel.currentSongList.collectAsState()
    val castState by viewModel.castState.collectAsState()


    val onSongClick: (SongInfo, ImmutableArray<SongInfo>) -> Unit = { itSong, _ ->
        viewModel.playSongNow(itSong)
    }
    val onPlayNext: (ImmutableArray<SongInfo>) -> Unit = {
        viewModel.playAfterCurrentSong(PlaylistInfo("Next", it))
    }
    val onQueue: (ImmutableArray<SongInfo>) -> Unit = {
        viewModel.playAfterCurrentPlaylist(PlaylistInfo("Queued", it))
    }
    val onRepeatClick: ()->Unit = { viewModel.toggleRepeat() }
    val onPlayPauseClick: ()->Unit = { viewModel.togglePlayPause() }
    val onNextClick:()->Unit = { viewModel.playNext() }

    val onCreateNewPlaylist: (String)->Unit = { viewModel.addNewPlaylist(it) }
    val onRemovePlaylist:(PlaylistInfo)->Unit = { viewModel.removePlaylist(it) }

    val songsForAction = remember { mutableStateOf<ImmutableArray<SongInfo>>(emptyImmutableArray()) }
    val playlistForAction = remember { mutableStateOf<PlaylistInfo?>(null) }
    val pickPlaylist = remember { mutableStateOf(false) }
    val showActionChoice = remember { mutableStateOf(false) }

    val onLongPressAction: (ImmutableArray<SongInfo>, PlaylistInfo?)->Unit = { songs, playlist ->
        songsForAction.value = songs
        playlistForAction.value = playlist
        showActionChoice.value = true
    }

    if (showActionChoice.value) {
        MyMusicPlayerTheme {
            Dialog(onDismissRequest = { showActionChoice.value = false }) {
                Box(
                    Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = "Choose Action",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Spacer(modifier = Modifier.padding(all = 8.dp))
                        ListItem(
                            headlineContent = { Text("Play Next") },
                            modifier = Modifier.clickable {
                                onPlayNext(songsForAction.value)
                                showActionChoice.value = false
                            }
                        )
                        ListItem(
                            headlineContent = { Text("Queue") },
                            modifier = Modifier.clickable {
                                onQueue(songsForAction.value)
                                showActionChoice.value = false
                            }
                        )
                        ListItem(
                            headlineContent = { Text("Add to Playlist") },
                            modifier = Modifier.clickable {
                                pickPlaylist.value = true
                                showActionChoice.value = false
                            }
                        )
                        playlistForAction.value?.let { playlist ->
                            ListItem(
                                headlineContent = { Text("Remove Playlist", color = Color.Red) },
                                modifier = Modifier.clickable {
                                    onRemovePlaylist(playlist)
                                    showActionChoice.value = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (pickPlaylist.value) {
        MyMusicPlayerTheme {
            Dialog(onDismissRequest = { pickPlaylist.value = false }) {
                val onCLick: (PlaylistInfo) -> Unit = {
                    viewModel.addSongsToPlaylist(it, songsForAction.value)
                    pickPlaylist.value = false
                }

                Box(
                    Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .size(500.dp, 300.dp)
                ) {
                    Column {
                        Row {
                            Spacer(modifier = Modifier.padding(all = 4.dp))
                            Text(
                                text = stringResource(R.string.pick_playlist),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                        Spacer(modifier = Modifier.padding(all = 4.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.primary)
                        PlaylistList(playlists, onCLick)
                    }
                }
            }
        }
    }

    val onRemoveSongFromPlaylist:(PlaylistInfo, SongInfo)->Unit = { playListInfo, songInfo ->
        viewModel.removeSongFromPlaylist(playListInfo, songInfo)
    }

    MainContent(
        viewModel.browser,
        songs,
        currentSongList,
        artists,
        albums,
        playlists,
        onCreateNewPlaylist,
        onRemovePlaylist,
        onLongPressAction,
        onRemoveSongFromPlaylist,
        onSongClick,
        onPlayNext,
        onQueue,
        onRepeatClick,
        onPlayPauseClick,
        onNextClick,
        currentlyPlayingStats,
        isPaused,
        activity,
        castState
    )
}

// Main content is the main screen but without relying on the view model
// so previews will work.
@Composable
private fun MainContent(
    mediaController: MediaController?,
    songs: ImmutableArray<SongInfo>,
    currentSongList: ImmutableArray<SongInfo>,
    artists: ImmutableArray<ArtistInfo>,
    albums: ImmutableArray<AlbumInfo>,
    playlists: ImmutableArray<PlaylistInfo>,
    onCreateNewPlaylist: (String) -> Unit = emptyFunction1(),
    onRemovePlaylist: (PlaylistInfo) -> Unit = emptyFunction1(),
    onLongPress: (ImmutableArray<SongInfo>, PlaylistInfo?)->Unit = emptyFunction2(),
    onRemoveSongFromPlaylist:(PlaylistInfo, SongInfo)->Unit = emptyFunction2(),
    onSongClick: (SongInfo, ImmutableArray<SongInfo>) -> Unit = emptyFunction2(),
    onPlayNext: (ImmutableArray<SongInfo>) -> Unit = emptyFunction1(),
    onQueue: (ImmutableArray<SongInfo>) -> Unit = emptyFunction1(),
    onRepeatClick: () -> Unit = emptyFunction(),
    onPlayPauseClick: () -> Unit = emptyFunction(),
    onNextClick: () -> Unit = emptyFunction(),
    currentPlayingStats: CurrentPlayingStats? = null,
    isPaused: Boolean = false,
    activity: Context? = null,
    castState: Int = 0
) {
    val shuffledSongs = remember{mutableStateOf(songs.shuffled())}
    val setShuffledSongs: (ImmutableArray<SongInfo>) -> Unit = {
        shuffledSongs.value = it
    }
    val onShuffleClick: () -> Unit = {
        if (shuffledSongs.value.any()) {
            onSongClick(shuffledSongs.value[0], shuffledSongs.value)
        }
    }

    val navController = rememberNavController()
    MyMusicPlayerTheme {
        Surface {
            Scaffold(
                topBar = { TopAppBar(castState, currentPlayingStats?.repeat ?: 0, activity, onShuffleClick, onRepeatClick) },
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
                    Navigation(navController, mediaController, songs, currentSongList, artists,
                        albums, playlists, onCreateNewPlaylist, onRemovePlaylist,
                        onLongPress, onRemoveSongFromPlaylist, onSongClick,
                        onPlayNext, onQueue,
                        currentPlayingStats, isPaused, setShuffledSongs)
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
            SampleData().songs,
            SampleData().artists,
            SampleData().albums,
            SampleData().playlists
        )
    }
}


