package com.bdlepla.android.mymusicplayer.Extensions

import androidx.compose.runtime.Composable
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import java.io.File

@Composable
fun String?.toImagePainter(): ImagePainter =
    rememberImagePainter(
        if (this != null) File(this) else null
    )
