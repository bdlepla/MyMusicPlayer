package com.bdlepla.android.mymusicplayer.Extensions

import androidx.compose.runtime.Composable
import coil.compose.ImagePainter
import coil.compose.rememberImagePainter
import java.io.File

@Composable
fun String?.toImagePainter(): ImagePainter =
    rememberImagePainter(this?.let{File(this)})
