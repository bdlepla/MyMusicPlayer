package com.bdlepla.android.mymusicplayer.Extensions

import androidx.compose.runtime.Composable
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import java.io.File

@Composable
fun String?.toImagePainter(): AsyncImagePainter =
    rememberAsyncImagePainter(this?.let{File(this)})
