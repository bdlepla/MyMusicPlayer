package com.bdlepla.android.mymusicplayer

import android.content.res.Configuration
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.bdlepla.android.mymusicplayer.ui.SongList
import com.bdlepla.android.mymusicplayer.ui.theme.MyMusicPlayerTheme

@Composable
internal fun MainScreen() {
    MyMusicPlayerTheme {
        Scaffold (
            topBar = { TopAppBar() },
            bottomBar = { BottomAppBar() }
        ){ MainContent() }
    }
}

@Composable
private fun MainContent() {
    // A surface container using the 'background' color from the theme
    Surface(color = MaterialTheme.colors.background) {
        SongList(SampleData.Songs)
    }
}

@Composable
private fun TopAppBar() {
    TopAppBar(
        title = {
            Text(text = "My Music Player",
                style = MaterialTheme.typography.h3)

        },
        actions = {

            IconButton(onClick = { /* doSomething() */ }) {
                Icon(Icons.Filled.Favorite, contentDescription = null)
            }
            IconButton(onClick = { /*TODO*/ }) {
                Icon(Icons.Filled.Refresh, contentDescription = null)
            }
        }
    )
}

@Composable
private fun BottomAppBar() {
    var selectedItem by remember { mutableStateOf(0) }
    val items = listOf("Songs", "Artists", "Playlists")

    BottomNavigation {
        items.forEachIndexed { index, item ->
            BottomNavigationItem(
                icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                label = { Text(item) },
                selected = selectedItem == index,
                onClick = { selectedItem = index }
            )
        }
    }
}




@Preview(
    showBackground = true,
    name="Main Screen Light Mode"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Main Screen Dark Mode"
)
@Composable
fun DefaultPreview() {
   MainScreen()
}