package com.bdlepla.android.mymusicplayer.ui

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.bdlepla.android.mymusicplayer.Extensions.toMinutesSeconds
import com.bdlepla.android.mymusicplayer.business.CurrentPlayingStats
import com.bdlepla.android.mymusicplayer.ui.theme.MyMusicPlayerTheme

@Composable
fun TimingLine(currentPlayingStats: CurrentPlayingStats?) {
    var size by remember { mutableStateOf(IntSize.Zero) }

    Column(Modifier.fillMaxWidth().onSizeChanged { size = it }) {
        val currentSeconds = currentPlayingStats?.currentPosition ?: 0
        val maxSeconds = currentPlayingStats?.maxPosition ?: 0
        Canvas(Modifier){
            val width = size.width.toFloat()
            val playedWidth = width * currentSeconds / maxSeconds
            drawLine(
                start = Offset(x = 0f, y = 0f),
                end = Offset(x = playedWidth, y = 0f),
                color = Color.Blue,
                strokeWidth = 15f
            )
            drawLine(
                start = Offset(x = playedWidth, y=0f),
                end = Offset(x = width, y = 0f),
                color = Color.DarkGray,
                strokeWidth = 15f
            )
        }
        Spacer(Modifier.padding(5.dp))
        Row {
            val currentString = " " + currentSeconds.toMinutesSeconds()
            Text(currentString, textAlign = TextAlign.Left, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.weight(1f))
            val remainingSeconds = maxSeconds - currentSeconds
            val remaining = "-" + remainingSeconds.toMinutesSeconds() + " "
            Text(remaining, textAlign = TextAlign.End, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Preview(
    showBackground = true,
    name="Timing Line Light Mode"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Timing Line Dark Mode"
)
@Composable
fun TimingLinePreview() {
    MyMusicPlayerTheme {
        TimingLine(CurrentPlayingStats(null,50, 235))
    }
}