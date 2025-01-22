package com.bdlepla.android.mymusicplayer

import androidx.compose.runtime.Composable
import androidx.media3.session.MediaController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bdlepla.android.mymusicplayer.business.AlbumInfo
import com.bdlepla.android.mymusicplayer.business.ArtistInfo
import com.bdlepla.android.mymusicplayer.business.CurrentPlayingStats
import com.bdlepla.android.mymusicplayer.business.PlaylistInfo
import com.bdlepla.android.mymusicplayer.business.SongInfo
import com.bdlepla.android.mymusicplayer.extensions.shuffled
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
    onRemovePlaylist: (PlaylistInfo) -> Unit = emptyFunction1(),
    pickPlaylistToAddSongs: (ImmutableArray<SongInfo>)->Unit = emptyFunction1(),
    onRemoveSongFromPlaylist:(PlaylistInfo, SongInfo)->Unit = emptyFunction2(),
    onSongClick: (SongInfo, ImmutableArray<SongInfo>) -> Unit = emptyFunction2(),
    currentPlayingStats: CurrentPlayingStats? = null,
    isPaused: Boolean = false,
    setShuffledSongs: (ImmutableArray<SongInfo>) -> Unit = emptyFunction1()
) {
    val onAddSongsToPlaylist: (ImmutableArray<SongInfo>)->Unit = { pickPlaylistToAddSongs(it) }

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
            setShuffledSongs(emptyImmutableArray())
            PlayScreen(currentPlayingStats, currentSongList, isPaused, mediaController)
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
