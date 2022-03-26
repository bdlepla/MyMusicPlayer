package com.bdlepla.android.mymusicplayer.ui

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.Log
import com.bdlepla.android.mymusicplayer.Extensions.toImagePainter
import com.bdlepla.android.mymusicplayer.SampleData
import com.bdlepla.android.mymusicplayer.business.AlbumInfo
import com.bdlepla.android.mymusicplayer.business.ArtistInfo
import com.bdlepla.android.mymusicplayer.business.ISongInfo
import com.bdlepla.android.mymusicplayer.ui.theme.MyMusicPlayerTheme

@Composable
fun ArtistList(
        artistList:List<ArtistInfo>,
        allAlbums:List<AlbumInfo> = emptyList(),
        allSongs:List<ISongInfo> = emptyList(),
        onSongClick:(ISongInfo, List<ISongInfo>, Boolean)->Unit = emptyFunction3()

    ) {
    val listState = rememberLazyListState()
    val selectedArtist: MutableState<ArtistInfo?> = remember { mutableStateOf(null) }
    val onClick: (ArtistInfo)->Unit = { selectedArtist.value = it }

    BackHandler(enabled = selectedArtist.value != null) {
        selectedArtist.value = null
    }

    if (selectedArtist.value != null) {
        val theArtist = selectedArtist.value!!
        val artistId = theArtist.artistId
        val songsForArtist = allSongs
            .filter { it.artistId == artistId}
            .sortedBy { it.albumYear * 1_000 + it.trackNumber }
            .onEach{
                val title = it.title
                val year = it.albumYear
                val track = it.trackNumber
                val message = "$year $track"
                Log.e(title, message)
            }
        val myOnClick:(ISongInfo)->Unit = {
            onSongClick(it, songsForArtist, false)
        }
        ArtistSongsScreen(theArtist, songsForArtist, myOnClick)
    }
    else {
        LazyColumn(state = listState) {
            items(items = artistList, key = { it.artistId }) { artistInfo ->
                val album = allAlbums.filter { it.artistId == artistInfo.artistId }.randomOrNull()
                Artist(artistInfo, album, onClick)
                Divider(color = MaterialTheme.colors.primary)
            }
        }
    }
}

@Composable
fun Artist(
        artistInfo:ArtistInfo,
        albumForArtist: AlbumInfo? = null,
        onClick: (ArtistInfo) -> Unit = emptyFunction1()) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(artistInfo) }
            .padding(all = 4.dp)
            .semantics(mergeDescendants = true) {}) {
        Image(
            painter = albumForArtist?.albumArt.toImagePainter(),
            contentDescription = albumForArtist?.name,
            modifier = Modifier.size(50.dp)
        )
        Spacer(modifier=Modifier.padding(all=4.dp))
        Text(
            text = artistInfo.name,
            style = MaterialTheme.typography.subtitle2,
            color = MaterialTheme.colors.secondaryVariant
        )
    }
}

@Preview(
    showBackground = true,
    name="Artist Light Mode"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Artist Dark Mode"
)
@Composable
fun ArtistPreview() {
    MyMusicPlayerTheme {
        Artist(SampleData.Artists[0])
    }
}

@Preview(
    showBackground = true,
    name = "Song List Light Mode"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Song List Dark Mode"
)
@Composable
fun ArtistListPreview() {
    MyMusicPlayerTheme {
        ArtistList(SampleData.Artists)
    }
}
