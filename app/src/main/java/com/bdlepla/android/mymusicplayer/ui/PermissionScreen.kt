package com.bdlepla.android.mymusicplayer.ui

import android.Manifest
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.meticha.permissions_compose.PermissionState

@Composable
fun PermissionScreen(permissions:PermissionState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Display current permission statuses
        Text("Audio Read Permission: ${if (permissions.isGranted(Manifest.permission.READ_MEDIA_AUDIO)) "Granted" else "Not Granted"}")

        Button(
            onClick = { permissions.requestPermission() },
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Request Permissions")
        }
    }
}