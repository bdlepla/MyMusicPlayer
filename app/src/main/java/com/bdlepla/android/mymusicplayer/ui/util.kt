package com.bdlepla.android.mymusicplayer.ui

fun emptyFunction(): () -> Unit = {}
fun <T> emptyFunction1(): (T) -> Unit = {}
//fun <T, U> emptyFunction2(): (T, U) -> Unit = { _, _ -> }
fun <T, U, V> emptyFunction3():(T, U, V) -> Unit = { _, _, _ -> }
