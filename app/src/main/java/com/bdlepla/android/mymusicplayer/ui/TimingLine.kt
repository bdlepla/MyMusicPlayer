package com.bdlepla.android.mymusicplayer.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.bdlepla.android.mymusicplayer.ui.theme.MyMusicPlayerTheme

@Composable
fun TimingLine(currentSeconds:Int, maxSeconds:Int) {
    Column {
        Row {

        }
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
fun TimingLinePreview() {
    MyMusicPlayerTheme {
        TimingLine(50, 235)
    }
}