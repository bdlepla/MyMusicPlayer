package com.bdlepla.android.mymusicplayer.extensions

import androidx.compose.runtime.Composable
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import java.io.File

@Composable
fun String?.toImagePainter(): AsyncImagePainter =
    rememberAsyncImagePainter(this?.let{File(this)})
