package com.bdlepla.android.mymusicplayer.extensions

import androidx.compose.runtime.Composable
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter

@Composable
fun String?.toImagePainter(): AsyncImagePainter =
    rememberAsyncImagePainter(model = this)
