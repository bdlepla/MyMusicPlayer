package com.bdlepla.android.mymusicplayer.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bdlepla.android.mymusicplayer.business.PlaylistInfo
import com.bdlepla.android.mymusicplayer.business.SongInfo
import com.bdlepla.android.mymusicplayer.extensions.any
import com.bdlepla.android.mymusicplayer.extensions.toImagePainter
import com.danrusu.pods4k.immutableArrays.ImmutableArray

@Composable
fun PlaylistSongsScreen(
    playlist:PlaylistInfo,
    songs: ImmutableArray<SongInfo>,
    onSongClick:(SongInfo)->Unit = emptyFunction1(),
    onRemoveSongFromPlayList:(PlaylistInfo, SongInfo)->Unit = emptyFunction2()) {
    val onLongPress:(ImmutableArray<SongInfo>)->Unit = {
        if (it.any()) {
            onRemoveSongFromPlayList(playlist, it.first())
        }
    }
    Column {
        Box (modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = playlist.artwork.toImagePainter(),
                contentDescription = playlist.name,
                modifier = Modifier
                    .size(256.dp)
                    .align(Alignment.Center)
            )
        }
        Spacer(modifier= Modifier.padding(all=4.dp))
        SongList(songInfos = songs, onSongClick, onLongPress)
    }
}