package com.bdlepla.android.mymusicplayer.ui

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.media3.session.MediaController
import com.bdlepla.android.mymusicplayer.*
import com.bdlepla.android.mymusicplayer.Extensions.toImagePainter
import com.bdlepla.android.mymusicplayer.business.SongInfo
import com.bdlepla.android.mymusicplayer.ui.theme.MyMusicPlayerTheme

@Composable
fun PlayScreen(currentSong: SongInfo) {
    Column {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = currentSong.albumArt.toImagePainter(),
                contentDescription = currentSong.albumArt,
                modifier = Modifier
                    .size(256.dp)
                    .align(Alignment.Center)
            )
        }
        TimingLine(0, 245)
        Spacer(modifier = Modifier.padding(all = 4.dp))
        Column(modifier = Modifier.padding(all = 4.dp)) {
            Text(
                text = currentSong.title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Row {
                Text(
                    text = currentSong.artist,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = currentSong.album,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun PlayScreen2(mediaController: MediaController?=null) {
    val context = LocalContext.current
    Text("To Do")
//    AndroidView(
//        modifier = Modifier.testTag("VideoPlayer").fillMaxSize(),
//        factory = {
//            // exo player view for our video player
//            PlayerViewCompat(context).apply {
//                player = mediaController
//                useController=true
//                layoutParams = FrameLayout.LayoutParams(
//                        ViewGroup.LayoutParams.MATCH_PARENT,
//                        ViewGroup.LayoutParams.MATCH_PARENT)
//            }
//        }
//    )
}

@Preview(
    showBackground = true,
    name="Currently Playing Small Light Mode"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Currently Playing Small Dark Mode"
)
@Composable
fun PlayScreenPreview() {
    MyMusicPlayerTheme {
        PlayScreen(SampleData().songs[0])
    }
}

@Preview(
    showBackground = true,
    name="Currently Playing Small Light Mode"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Currently Playing Small Dark Mode"
)
@Composable
fun PlayScreen2Preview() {
    MyMusicPlayerTheme {
        PlayScreen2(null)
    }
}
