package com.bdlepla.android.mymusicplayer.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bdlepla.android.mymusicplayer.extensions.toImagePainter
import com.bdlepla.android.mymusicplayer.SampleData
import com.bdlepla.android.mymusicplayer.business.AlbumInfo
import com.bdlepla.android.mymusicplayer.business.SongInfo
import com.bdlepla.android.mymusicplayer.ui.theme.MyMusicPlayerTheme

@Composable
fun AlbumSongsScreen(isExpandedWindowSize: Boolean = false,
                     albumInfo: AlbumInfo,
                     songsInAlbum: List<SongInfo>,
                     onSongClick:(SongInfo)->Unit = emptyFunction1()) {
    if (isExpandedWindowSize) {
        AlbumSongsScreenLandscape(albumInfo, songsInAlbum, onSongClick)
    }
    else {
        AlbumSongsScreenPortrait(albumInfo, songsInAlbum, onSongClick)
    }
}

@Composable
fun AlbumSongsScreenPortrait(
    albumInfo: AlbumInfo,
    songsInAlbum: List<SongInfo>,
    onSongClick:(SongInfo)->Unit = emptyFunction1()) {
    Column {
        Box (modifier = Modifier.fillMaxWidth()){
            Image(
                painter = albumInfo.albumArt.toImagePainter(),
                contentDescription = albumInfo.name,
                modifier = Modifier
                    .size(256.dp)
                    .align(Alignment.Center)
            )
        }
        Spacer(modifier= Modifier.padding(all=4.dp))
        SongList(songsInAlbum, onSongClick)
    }
}

@Preview(
    showBackground = true,
    name = "Album Songs Screen Light Mode"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Album Songs Screen Dark Mode"
)
@Composable
fun AlbumSongsScreenPortraitPreview() {
    MyMusicPlayerTheme {
        AlbumSongsScreenPortrait(SampleData().albums[0], SampleData().songs)
    }
}
@Composable
fun AlbumSongsScreenLandscape(
    albumInfo: AlbumInfo,
    songsInAlbum: List<SongInfo>,
    onSongClick:(SongInfo)->Unit = emptyFunction1()) {
    Row {
        //PlayScreen(currentPlayingStats = playingStats, isPaused = isPaused)
        //SongList
    }
    Column {
        Box (modifier = Modifier.fillMaxWidth()){
            Image(
                painter = albumInfo.albumArt.toImagePainter(),
                contentDescription = albumInfo.name,
                modifier = Modifier
                    .size(256.dp)
                    .align(Alignment.Center)
            )
        }
        Spacer(modifier= Modifier.padding(all=4.dp))
        SongList(songsInAlbum, onSongClick)
    }
}

@Preview(
    showBackground = true,
    name = "Album Songs Wide Screen Light Mode",
    device = Devices.AUTOMOTIVE_1024p,
    widthDp = 1024
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Album Songs Wide Screen Dark Mode",
    device = Devices.AUTOMOTIVE_1024p,
    widthDp = 1024
)
@Composable
fun AlbumSongsScreenLandscapePreview() {
    MyMusicPlayerTheme {
        AlbumSongsScreenLandscape(SampleData().albums[0], SampleData().songs)
    }
}
