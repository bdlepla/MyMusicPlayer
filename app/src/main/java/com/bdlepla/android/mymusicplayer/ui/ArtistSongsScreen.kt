package com.bdlepla.android.mymusicplayer.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bdlepla.android.mymusicplayer.Extensions.toImagePainter
import com.bdlepla.android.mymusicplayer.SampleData
import com.bdlepla.android.mymusicplayer.business.ArtistInfo
import com.bdlepla.android.mymusicplayer.business.SongInfo
import com.bdlepla.android.mymusicplayer.ui.theme.MyMusicPlayerTheme

@Composable
fun ArtistSongsScreen(
    artistInfo: ArtistInfo,
    songsInArtist: List<SongInfo>,
    onSongClick:(SongInfo)->Unit = emptyFunction1()) {
    Column {
        Box (modifier = Modifier.fillMaxWidth()){
            Image(
                painter = artistInfo.anAlbum?.albumArt.toImagePainter(),
                contentDescription = artistInfo.name,
                modifier = Modifier
                    .size(256.dp)
                    .align(Alignment.Center)
            )
        }
        Spacer(modifier= Modifier.padding(all=4.dp))
        SongList(songInfos = songsInArtist, onSongClick)
    }
}

@Preview(
    showBackground = true,
    name = "Artist Songs Screen Light Mode"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Artist Songs Screen Dark Mode"
)
@Composable
fun ArtistSongsScreenPreview() {
    MyMusicPlayerTheme {
        ArtistSongsScreen(SampleData().artists[0], SampleData().songs)
    }
}