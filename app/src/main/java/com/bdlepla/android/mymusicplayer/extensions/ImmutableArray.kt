package com.bdlepla.android.mymusicplayer.extensions

import com.danrusu.pods4k.immutableArrays.ImmutableArray
import com.danrusu.pods4k.immutableArrays.ImmutableLongArray

fun <T>ImmutableArray<T>.any() = size > 0
fun ImmutableLongArray.any() = size > 0
fun <T>ImmutableArray<T>.count() = size


