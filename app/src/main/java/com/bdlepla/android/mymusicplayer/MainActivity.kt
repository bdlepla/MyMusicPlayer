package com.bdlepla.android.mymusicplayer

import android.Manifest
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bdlepla.android.mymusicplayer.ui.MainScreen
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val myMusicViewModel:MyMusicViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkPermissions()

        setContent { MainScreen(myMusicViewModel,this) }
    }

    private fun checkPermissions() {
        val readAudioPermission = Manifest.permission.READ_MEDIA_AUDIO
        if (checkSelfPermission(readAudioPermission) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(readAudioPermission), 1)
        }
    }

    override fun onResume() {
        super.onResume()
        volumeControlStream = AudioManager.STREAM_MUSIC
    }
}

