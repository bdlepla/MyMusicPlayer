package com.bdlepla.android.mymusicplayer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.media3.session.MediaController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bdlepla.android.mymusicplayer.business.AlbumInfo
import com.bdlepla.android.mymusicplayer.business.ArtistInfo
import com.bdlepla.android.mymusicplayer.business.CurrentPlayingStats
import com.bdlepla.android.mymusicplayer.business.PlaylistInfo
import com.bdlepla.android.mymusicplayer.business.SongInfo
import com.bdlepla.android.mymusicplayer.ui.AlbumList
import com.bdlepla.android.mymusicplayer.ui.AlbumSongsScreen
import com.bdlepla.android.mymusicplayer.ui.ArtistList
import com.bdlepla.android.mymusicplayer.ui.ArtistSongsScreen
import com.bdlepla.android.mymusicplayer.ui.NavigationItem
import com.bdlepla.android.mymusicplayer.ui.PlayScreen
import com.bdlepla.android.mymusicplayer.ui.PlaylistScreen
import com.bdlepla.android.mymusicplayer.ui.PlaylistSongsScreen
import com.bdlepla.android.mymusicplayer.ui.SongList
import com.bdlepla.android.mymusicplayer.ui.emptyFunction1
import com.bdlepla.android.mymusicplayer.ui.emptyFunction2
import com.bdlepla.android.mymusicplayer.ui.emptyFunction3
import com.danrusu.pods4k.immutableArrays.ImmutableArray
import com.danrusu.pods4k.immutableArrays.emptyImmutableArray
import com.danrusu.pods4k.immutableArrays.multiplicativeSpecializations.flatMap

@Composable
fun Navigation(
    navController: NavHostController,
    mediaController: MediaController?,
    songs: ImmutableArray<SongInfo>,
    currentSongList: ImmutableArray<SongInfo>,
    artists: ImmutableArray<ArtistInfo>,
    albums: ImmutableArray<AlbumInfo>,
    playlists: ImmutableArray<PlaylistInfo>,
    onCreateNewPlaylist: (String) -> Unit = emptyFunction1(),
    onLongPress: (ImmutableArray<SongInfo>, PlaylistInfo?, Boolean) -> Unit = emptyFunction3(),
    onRemoveSongFromPlaylist:(PlaylistInfo, SongInfo)->Unit = emptyFunction2(),
    currentPlayingStats: CurrentPlayingStats? = null,
    isPaused: Boolean = false,
    setShuffledSongs: (ImmutableArray<SongInfo>) -> Unit = emptyFunction1()
) {
    NavHost(navController, startDestination = NavigationItem.Songs.route) {
        val isExpandedWindow = false

        composable(NavigationItem.Songs.route) {
            LaunchedEffect(songs) {
                setShuffledSongs(songs.shuffled())
            }
            SongList(songs, { onLongPress(it, null, true) })
        }

        composable(NavigationItem.Artists.route) {
            LaunchedEffect(artists) {
                val shuffledSongs = artists.shuffled()
                    .flatMap { artist -> artist.albumsByYear.shuffled() }
                    .flatMap { album -> album.songs }
                setShuffledSongs(shuffledSongs)
            }
            ArtistList(artists, navController) { onLongPress(it, null, true) }
        }

        composable("artistsongs/{artistId}") { backStack ->
            val args = backStack.arguments ?: return@composable
            val artistId = args.getString("artistId")?.toLong() ?: return@composable
            val theArtist = artists.firstOrNull { it.artistId == artistId } ?: return@composable
            val songsForArtist = theArtist.songs

            LaunchedEffect(songsForArtist) {
                setShuffledSongs(songsForArtist.shuffled())
            }

            ArtistSongsScreen(theArtist, songsForArtist, { onLongPress(it, null, true) })
        }

        composable(NavigationItem.Albums.route) {
            LaunchedEffect(albums) {
                setShuffledSongs(albums.shuffled().flatMap { album -> album.songs })
            }
            AlbumList(albums, navController) { onLongPress(it, null, true) }
        }

        composable("albumsongs/{albumId}") { backStack ->
            val args = backStack.arguments ?: return@composable
            val albumId = args.getString("albumId")?.toLong() ?: return@composable
            val theAlbum = albums.firstOrNull { it.albumId == albumId } ?: return@composable
            val songsInAlbum = theAlbum.songs

            LaunchedEffect(songsInAlbum) {
                setShuffledSongs(songsInAlbum.shuffled())
            }

            AlbumSongsScreen(isExpandedWindow, theAlbum, songsInAlbum, { onLongPress(it, null, true) })
        }

        composable(NavigationItem.Playing.route) {
            LaunchedEffect(Unit) {
                setShuffledSongs(emptyImmutableArray())
            }
            PlayScreen(currentPlayingStats, currentSongList, isPaused, mediaController)
        }

        composable(NavigationItem.Playlist.route) {
            val onClick: (PlaylistInfo) -> Unit = {
                val playlistName = it.name
                val route = "playlistsongs/${playlistName}"
                navController.navigate(route)
            }
            LaunchedEffect(playlists) {
                setShuffledSongs(playlists.shuffled().flatMap { playlist -> playlist.songs.shuffled() })
            }
            PlaylistScreen(playlists, onClick, onCreateNewPlaylist, onLongPress)
        }

        composable("playlistsongs/{playlistId}") { backStack ->
            val args = backStack.arguments ?: return@composable
            val playlistName = args.getString("playlistId") ?: return@composable
            val playlist = playlists.firstOrNull { it.name == playlistName } ?: return@composable
            val songsInPlaylist = playlist.songs

            LaunchedEffect(songsInPlaylist) {
                setShuffledSongs(songsInPlaylist.shuffled())
            }

            PlaylistSongsScreen(playlist, songsInPlaylist, onRemoveSongFromPlaylist)
        }
    }
}
