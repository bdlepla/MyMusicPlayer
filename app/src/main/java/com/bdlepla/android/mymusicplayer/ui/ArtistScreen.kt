package com.bdlepla.android.mymusicplayer.ui

import android.content.res.Configuration
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.bdlepla.android.mymusicplayer.SampleData
import com.bdlepla.android.mymusicplayer.business.ArtistInfo
import com.bdlepla.android.mymusicplayer.ui.theme.MyMusicPlayerTheme

@Composable
fun ArtistList(artistList:List<ArtistInfo>) {
    val listState = rememberLazyListState()
    LazyColumn(state = listState) {
        items(items = artistList) { artistInfo ->
            Artist(artistInfo)
            Divider(color = MaterialTheme.colors.primary)
        }
    }
}

@Composable
fun Artist(artistInfo:ArtistInfo) {
    Text(
        text=artistInfo.name,
        style = MaterialTheme.typography.subtitle2,
        color = MaterialTheme.colors.secondaryVariant
    )
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