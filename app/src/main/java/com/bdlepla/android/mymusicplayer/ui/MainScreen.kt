package com.bdlepla.android.mymusicplayer.ui

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bdlepla.android.mymusicplayer.AutomaticPlaylistConfig
import com.bdlepla.android.mymusicplayer.MyMusicViewModel
import com.bdlepla.android.mymusicplayer.Navigation
import com.bdlepla.android.mymusicplayer.R
import com.bdlepla.android.mymusicplayer.SampleData
import com.bdlepla.android.mymusicplayer.business.AlbumInfo
import com.bdlepla.android.mymusicplayer.business.ArtistInfo
import com.bdlepla.android.mymusicplayer.business.CurrentPlayingStats
import com.bdlepla.android.mymusicplayer.business.PlaylistInfo
import com.bdlepla.android.mymusicplayer.business.SongInfo
import com.bdlepla.android.mymusicplayer.ui.theme.MyMusicPlayerTheme
import com.danrusu.pods4k.immutableArrays.ImmutableArray
import com.danrusu.pods4k.immutableArrays.emptyImmutableArray

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

    val onPlayImmediately: (ImmutableArray<SongInfo>) -> Unit = {
        viewModel.playPlaylistNow(it)
    }
    val onPlayNext: (ImmutableArray<SongInfo>) -> Unit = {
        viewModel.playAfterCurrentSong(PlaylistInfo("Next", it))
    }
    val onQueue: (ImmutableArray<SongInfo>) -> Unit = {
        viewModel.playAfterCurrentPlaylist(PlaylistInfo("Queued", it))
    }
    val onRepeatClick: ()->Unit = { viewModel.toggleRepeat() }
    val onShuffleClick: () -> Unit = { viewModel.toggleShuffle() }
    val onPlayPauseClick: ()->Unit = { viewModel.togglePlayPause() }
    val onNextClick:()->Unit = { viewModel.playNext() }

    val onCreateNewPlaylist: (String)->Unit = { viewModel.addNewPlaylist(it) }
    val onRemovePlaylist:(PlaylistInfo)->Unit = { viewModel.removePlaylist(it) }
    val onSaveAutomaticPlaylist: (AutomaticPlaylistConfig) -> Unit = { viewModel.saveAutomaticPlaylist(it) }
    val onUpdateAutomaticPlaylist: (String, AutomaticPlaylistConfig) -> Unit = { oldName, newConfig ->
        viewModel.updateAutomaticPlaylist(oldName, newConfig)
    }
    val onFavoriteToggle: (SongInfo) -> Unit = { viewModel.toggleFavorite(it) }
    val onRemoveSongFromPlaylist:(PlaylistInfo, SongInfo)->Unit = { playListInfo, songInfo ->
        viewModel.removeSongFromPlaylist(playListInfo, songInfo)
    }

    MainContent(
        mediaController = viewModel.browser,
        songs = songs,
        currentSongList = currentSongList,
        artists = artists,
        albums = albums,
        playlists = playlists,
        onCreateNewPlaylist = onCreateNewPlaylist,
        onRemovePlaylist = onRemovePlaylist,
        onSaveAutomaticPlaylist = onSaveAutomaticPlaylist,
        onUpdateAutomaticPlaylist = onUpdateAutomaticPlaylist,
        onPlayImmediately = onPlayImmediately,
        onPlayNext = onPlayNext,
        onQueue = onQueue,
        onShuffleClick = onShuffleClick,
        onRepeatClick = onRepeatClick,
        onPlayPauseClick = onPlayPauseClick,
        onNextClick = onNextClick,
        onRemoveSongFromPlaylist = onRemoveSongFromPlaylist,
        currentPlayingStats = currentlyPlayingStats,
        isPaused = isPaused,
        activity = activity,
        castState = castState,
        onFavoriteToggle = onFavoriteToggle,
        addSongsToPlaylist = { playlist, songsToAdd -> viewModel.addSongsToPlaylist(playlist, songsToAdd) }
    )
}

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
    onSaveAutomaticPlaylist: (AutomaticPlaylistConfig) -> Unit = emptyFunction1(),
    onUpdateAutomaticPlaylist: (String, AutomaticPlaylistConfig) -> Unit = emptyFunction2(),
    onPlayImmediately: (ImmutableArray<SongInfo>) -> Unit = emptyFunction1(),
    onPlayNext: (ImmutableArray<SongInfo>) -> Unit = emptyFunction1(),
    onQueue: (ImmutableArray<SongInfo>) -> Unit = emptyFunction1(),
    onShuffleClick: () -> Unit = emptyFunction(),
    onRepeatClick: () -> Unit = emptyFunction(),
    onPlayPauseClick: () -> Unit = emptyFunction(),
    onNextClick: () -> Unit = emptyFunction(),
    onRemoveSongFromPlaylist: (PlaylistInfo, SongInfo) -> Unit = emptyFunction2(),
    currentPlayingStats: CurrentPlayingStats? = null,
    isPaused: Boolean = false,
    activity: Context? = null,
    castState: Int = 0,
    onFavoriteToggle: (SongInfo) -> Unit = emptyFunction1(),
    addSongsToPlaylist: (PlaylistInfo, ImmutableArray<SongInfo>) -> Unit = emptyFunction2()
) {
    val shuffledSongs = remember{mutableStateOf(songs.shuffled())}
    val setShuffledSongs: (ImmutableArray<SongInfo>) -> Unit = {
        shuffledSongs.value = it
    }

    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val isPlayingRoute = currentRoute == NavigationItem.Playing.route

    val songsForAction = remember { mutableStateOf<ImmutableArray<SongInfo>>(emptyImmutableArray()) }
    val playlistForAction = remember { mutableStateOf<PlaylistInfo?>(null) }
    val showAddToPlaylistOption = remember { mutableStateOf(true) }
    val pickPlaylist = remember { mutableStateOf(false) }
    val showActionChoice = remember { mutableStateOf(false) }

    val onLongPressAction: (ImmutableArray<SongInfo>, PlaylistInfo?, Boolean)->Unit = { s, playlist, showAddToPlaylist ->
        songsForAction.value = s
        playlistForAction.value = playlist
        showAddToPlaylistOption.value = showAddToPlaylist
        showActionChoice.value = true
    }

    val getResolvedSongs: () -> ImmutableArray<SongInfo> = {
        val playlist = playlistForAction.value
        if (playlist?.automaticConfig != null) {
            playlist.evaluateSongs(songs)
        } else {
            songsForAction.value
        }
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
                            headlineContent = { Text("Play Immediately") },
                            modifier = Modifier.clickable {
                                onPlayImmediately(getResolvedSongs())
                                showActionChoice.value = false
                            }
                        )
                        ListItem(
                            headlineContent = { Text("Play Next") },
                            modifier = Modifier.clickable {
                                onPlayNext(getResolvedSongs())
                                showActionChoice.value = false
                            }
                        )
                        ListItem(
                            headlineContent = { Text("Add to Current Queue") },
                            modifier = Modifier.clickable {
                                onQueue(getResolvedSongs())
                                showActionChoice.value = false
                            }
                        )
                        if (showAddToPlaylistOption.value) {
                            ListItem(
                                headlineContent = { Text("Add to Playlist") },
                                modifier = Modifier.clickable {
                                    pickPlaylist.value = true
                                    showActionChoice.value = false
                                }
                            )
                        }
                        playlistForAction.value?.let { playlist ->
                            if (playlist.automaticConfig != null) {
                                ListItem(
                                    headlineContent = { Text("Edit Playlist") },
                                    modifier = Modifier.clickable {
                                        showActionChoice.value = false
                                        navController.navigate("edit_automatic_playlist/${playlist.name}")
                                    }
                                )
                            }
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
                    addSongsToPlaylist(it, songsForAction.value)
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

    MyMusicPlayerTheme {
        Surface {
            Scaffold(
                topBar = {
                    if (!isPlayingRoute) {
                        TopAppBar(
                            castState,
                            currentPlayingStats?.repeat ?: 0,
                            currentPlayingStats?.shuffle ?: false,
                            activity,
                            onShuffleClick,
                            onRepeatClick
                        )
                    }
                },
                bottomBar = {
                    if (!isPlayingRoute) {
                        BottomAppBar(
                            onPlayPauseClick,
                            onNextClick,
                            currentPlayingStats?.currentPlaying,
                            isPaused,
                            navController
                        )
                    }
                }
            ) { paddingValues ->
                // A surface container using the 'background' color from the theme
                Column(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
                    Navigation(
                        navController = navController,
                        mediaController = mediaController,
                        songs = songs,
                        currentSongList = currentSongList,
                        artists = artists,
                        albums = albums,
                        playlists = playlists,
                        onCreateNewPlaylist = onCreateNewPlaylist,
                        onSaveAutomaticPlaylist = onSaveAutomaticPlaylist,
                        onUpdateAutomaticPlaylist = onUpdateAutomaticPlaylist,
                        onLongPress = onLongPressAction,
                        onRemoveSongFromPlaylist = onRemoveSongFromPlaylist,
                        currentPlayingStats = currentPlayingStats,
                        isPaused = isPaused,
                        setShuffledSongs = setShuffledSongs,
                        onFavoriteToggle = onFavoriteToggle
                    )
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
            mediaController = null,
            songs = SampleData().songs,
            currentSongList = SampleData().songs,
            artists = SampleData().artists,
            albums = SampleData().albums,
            playlists = SampleData().playlists
        )
    }
}
