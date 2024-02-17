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
    onSongClick: (SongInfo, List<SongInfo>) -> Unit = emptyFunction2(),
    currentPlayingStats: CurrentPlayingStats? = null,
    isPaused: Boolean = false,
    setShuffledSongs: (List<SongInfo>) -> Unit = emptyFunction1()
) {
    val onAddSongsToPlaylist: (List<SongInfo>)->Unit = { pickPlaylistToAddSongs(it) }

    NavHost(navController, startDestination = NavigationItem.Songs.route) {
        val isExpandedWindow = false

        composable(NavigationItem.Songs.route) {
            val myOnClick:(SongInfo)->Unit = { onSongClick(it, songs) }
            setShuffledSongs(songs.shuffled())
            SongList(songs, myOnClick, onAddSongsToPlaylist)
        }

        composable(NavigationItem.Artists.route) {
            val shuffledArtists = artists.shuffled()
            val shuffledArtistAlbums = shuffledArtists.flatMap{artist -> artist.albumsByYear.shuffled()}
            val shuffledSongs = shuffledArtistAlbums.flatMap { album -> album.songs }
            setShuffledSongs(shuffledSongs )
            ArtistList(artists, navController, onAddSongsToPlaylist)
        }

        composable("artistsongs/{artistId}") { backStack ->
            val args = backStack.arguments ?: return@composable
            val artistId = args.getString("artistId")?.toLong()?: return@composable
            val theArtist = artists.firstOrNull{it.artistId == artistId} ?: return@composable
            val songsForArtist = theArtist.songs
            setShuffledSongs(songsForArtist.shuffled())
            val myOnClick:(SongInfo)->Unit = { onSongClick(it, songsForArtist) }
            ArtistSongsScreen(theArtist, songsForArtist, myOnClick, onAddSongsToPlaylist)
        }

        composable(NavigationItem.Albums.route) {
            setShuffledSongs(albums.shuffled().flatMap{album -> album.songs})
            AlbumList(albums, navController, onAddSongsToPlaylist)
        }

        composable("albumsongs/{albumId}") { backStack ->
            val args = backStack.arguments ?: return@composable
            val albumId = args.getString("albumId")?.toLong() ?: return@composable
            val theAlbum = albums.firstOrNull{it.albumId == albumId} ?: return@composable
            val songsInAlbum = theAlbum.songs
            setShuffledSongs(songsInAlbum.shuffled())
            val myOnClick:(SongInfo)->Unit = { onSongClick(it, songsInAlbum) }
            AlbumSongsScreen(isExpandedWindow, theAlbum, songsInAlbum, myOnClick, onAddSongsToPlaylist)
        }

        composable(NavigationItem.Playing.route) {
            setShuffledSongs(songs.shuffled())
            PlayScreen(currentPlayingStats, isPaused, mediaController)
        }

        composable(NavigationItem.Playlist.route) {
            val onClick: (PlaylistInfo) -> Unit = {
                val playlistName = it.name
                val route = "playlistsongs/${playlistName}"
                navController.navigate(route)
            }
            setShuffledSongs(playlists.shuffled().flatMap { playlist -> playlist.songs.shuffled() })
            PlaylistScreen(playlists, onClick, onCreateNewPlaylist, onRemovePlaylist)
        }

        composable("playlistsongs/{playlistId}") {
            val backStack = navController.currentBackStackEntry?: return@composable
            val args = backStack.arguments ?: return@composable
            val playlistName = args.getString("playlistId") ?: return@composable
            val playlist = playlists.firstOrNull{it.name == playlistName} ?: return@composable
            val songsInPlaylist = playlist.songs
            setShuffledSongs(songsInPlaylist.shuffled())
            val myOnClick:(SongInfo)->Unit = { onSongClick(it, songsInPlaylist) }
            PlaylistSongsScreen(playlist, songsInPlaylist, myOnClick, onRemoveSongFromPlaylist)
        }
    }
}
