package com.bdlepla.android.mymusicplayer

import androidx.compose.runtime.Composable
import androidx.media3.session.MediaController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bdlepla.android.mymusicplayer.business.*
import com.bdlepla.android.mymusicplayer.ui.*

@Composable
fun Navigation(
    navController: NavHostController,
    mediaController: MediaController?,
    songs: List<SongInfo>,
    artists: List<ArtistInfo>,
    albums: List<AlbumInfo>,
    playlists: List<PlaylistInfo>,
    onCreateNewPlaylist: (String) -> Unit = emptyFunction1(),
    onRemovePlaylist: (PlaylistInfo) -> Unit = emptyFunction1(),
    pickPlaylistToAddSongs: (List<SongInfo>)->Unit = emptyFunction1(),
    onRemoveSongFromPlaylist:(PlaylistInfo, SongInfo)->Unit = emptyFunction2(),
    setSongsForScreen: (List<SongInfo>) -> Unit = emptyFunction1(),
    onSongClick: (SongInfo, List<SongInfo>) -> Unit = emptyFunction2(),
    currentPlayingStats: CurrentPlayingStats? = null,
    isPaused: Boolean = false
) {
    val onAddSongsToPlaylist: (List<SongInfo>)->Unit = { pickPlaylistToAddSongs(it) }

    NavHost(navController, startDestination = NavigationItem.Songs.route) {
        val isExpandedWindow = false

        composable(NavigationItem.Songs.route) {
            setSongsForScreen(songs)
            val myOnClick:(SongInfo)->Unit = { onSongClick(it, songs) }
            SongList(songs, myOnClick, onAddSongsToPlaylist)
        }

        composable(NavigationItem.Artists.route) {
            ArtistList(artists, navController, onAddSongsToPlaylist)
        }

        composable("artistsongs/{artistId}") {
            val backStack = navController.currentBackStackEntry ?: return@composable
            val args = backStack.arguments ?: return@composable
            val artistId = args.getString("artistId")?.toLong()?: return@composable
            val theArtist = artists.firstOrNull{it.artistId == artistId} ?: return@composable
            val songsForArtist = theArtist.songs
            setSongsForScreen(songsForArtist)
            val myOnClick:(SongInfo)->Unit = { onSongClick(it, songsForArtist) }
            ArtistSongsScreen(theArtist, songsForArtist, myOnClick)
        }

        composable(NavigationItem.Albums.route) {
            AlbumList(albums, navController, onAddSongsToPlaylist)
        }

        composable("albumsongs/{albumId}") {
            val backStack = navController.currentBackStackEntry?: return@composable
            val args = backStack.arguments ?: return@composable
            val albumId = args.getString("albumId")?.toLong() ?: return@composable
            val theAlbum = albums.firstOrNull{it.albumId == albumId} ?: return@composable
            val songsInAlbum = theAlbum.songs
            setSongsForScreen(songsInAlbum)
            val myOnClick:(SongInfo)->Unit = { onSongClick(it, songsInAlbum) }
            AlbumSongsScreen(isExpandedWindow, theAlbum, songsInAlbum, myOnClick)
        }

        composable(NavigationItem.Playing.route) {
            PlayScreen(currentPlayingStats, isPaused, mediaController)
        }

        composable(NavigationItem.Playlist.route) {
            val onClick: (PlaylistInfo) -> Unit = {
                val playlistName = it.name
                val route = "playlistsongs/${playlistName}"
                navController.navigate(route)
            }
            PlaylistScreen(playlists, onClick, onCreateNewPlaylist, onRemovePlaylist)
        }

        composable("playlistsongs/{playlistId}") {
            val backStack = navController.currentBackStackEntry?: return@composable
            val args = backStack.arguments ?: return@composable
            val playlistName = args.getString("playlistId") ?: return@composable
            val playlist = playlists.firstOrNull{it.name == playlistName} ?: return@composable
            val songsInPlaylist = playlist.songs
            setSongsForScreen(songsInPlaylist)
            val myOnClick:(SongInfo)->Unit = { onSongClick(it, songsInPlaylist) }
            PlaylistSongsScreen(playlist, songsInPlaylist, myOnClick, onRemoveSongFromPlaylist)
        }
    }
}
