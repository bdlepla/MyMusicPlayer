package com.bdlepla.android.mymusicplayer

import android.Manifest
import android.media.AudioManager
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bdlepla.android.mymusicplayer.ui.MainScreen
import com.bdlepla.android.mymusicplayer.ui.PermissionScreen
import com.bdlepla.android.mymusicplayer.ui.theme.MyMusicPlayerTheme
import com.meticha.permissions_compose.AppPermission
import com.meticha.permissions_compose.rememberAppPermissionState
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val myMusicViewModel:MyMusicViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            MyMusicPlayerTheme {
                val permissions = rememberAppPermissionState(
                    permissions = listOf(
                        AppPermission(
                            permission = Manifest.permission.READ_MEDIA_AUDIO,
                            description = "Media read access is needed to play music. Please grant this permission.",
                            isRequired = true
                        )
                    )
                )
                if (!permissions.allRequiredGranted()) {
                    PermissionScreen(permissions)
                }
                else {
                    MainScreen(myMusicViewModel, this)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        volumeControlStream = AudioManager.STREAM_MUSIC
    }
}

