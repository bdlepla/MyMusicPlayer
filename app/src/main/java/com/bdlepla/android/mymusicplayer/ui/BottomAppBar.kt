package com.bdlepla.android.mymusicplayer.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bdlepla.android.mymusicplayer.R
import com.bdlepla.android.mymusicplayer.SampleData
import com.bdlepla.android.mymusicplayer.business.SongInfo
import com.bdlepla.android.mymusicplayer.ui.theme.MyMusicPlayerTheme

sealed class NavigationItem(var route: String, var icon: Int, var title: String) {
    object Songs : NavigationItem("songs", R.drawable.ic_library_music, "Songs")
    object Artists : NavigationItem("artists", R.drawable.ic_people_alt, "Artists")
    object Albums : NavigationItem("albums", R.drawable.ic_album, "Albums")
    object Playing : NavigationItem("playing", R.drawable.ic_airplay, "Playing")
}

@Composable
fun BottomAppBar(
    onPlayPauseClick: () -> Unit = emptyFunction(),
    onNextClick: () -> Unit = emptyFunction(),
    currentlyPlaying: SongInfo? = null,
    isPaused: Boolean = true,
    navController: NavHostController = rememberNavController(),
) {
    val items = listOf(
        NavigationItem.Songs,
        NavigationItem.Artists,
        NavigationItem.Albums,
        NavigationItem.Playing
    )
    val isCurrentlyPlaying = currentlyPlaying != null
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Column {
        if (isCurrentlyPlaying && currentRoute != NavigationItem.Playing.route) {
            CurrentlyPlayingSmallScreen(
                currentlyPlaying!!,
                isPaused,
                onPlayPauseClick,
                onNextClick,
            )
        }

        BottomNavigation(backgroundColor = MaterialTheme.colorScheme.primaryContainer) {
            items.forEach { item ->
                BottomNavigationItem(
                    icon = {Icon(painterResource(id = item.icon), contentDescription = item.title)},
                    label = { item.title },
                    selected =currentRoute == item.route,
                    onClick = {
                        navController.navigate(item.route) {
                            // Pop up to the start destination of the graph to
                            // avoid building up a large stack of destinations
                            // on the back stack as users select items
                            navController.graph.startDestinationRoute?.let { route ->
                                popUpTo(route) {
                                    saveState = true
                                }
                            }
                            // Avoid multiple copies of the same destination when
                            // reselecting the same item
                            launchSingleTop = true
                            // Restore state when reselecting a previously selected item
                            restoreState = true
                        }
                    }
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    name="Bottom App Bar Light Mode"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Bottom App Bar Dark Mode"
)

@Composable
fun BottomAppBarPreview() {
    MyMusicPlayerTheme {
        BottomAppBar()
    }
}

@Preview(
    showBackground = true,
    name="Bottom App Bar With Small Player Light Mode"
)
@Preview(
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    name = "Bottom App Bar With Small Player Dark Mode"
)

@Composable
fun BottomAppBarWithCurrentlyPlayingPreview() {
    MyMusicPlayerTheme {
        BottomAppBar(currentlyPlaying = SampleData().songs[0])
    }
}