package com.bdlepla.android.mymusicplayer.extensions

import com.danrusu.pods4k.immutableArrays.ImmutableArray
import com.danrusu.pods4k.immutableArrays.ImmutableLongArray
import com.danrusu.pods4k.immutableArrays.asList
import com.danrusu.pods4k.immutableArrays.indexOf
import com.danrusu.pods4k.immutableArrays.toImmutableArray

fun <T>ImmutableArray<T>.shuffled(): ImmutableArray<T> = asList().shuffled().toImmutableArray()
fun <T>ImmutableArray<T>.any() = size > 0
fun ImmutableLongArray.any() = size > 0
fun <T>ImmutableArray<T>.count() = size
fun <T>ImmutableArray<T>.random():T = asList().random()
fun <T>ImmutableArray<T>.find(predicate:(T)->Boolean):T? {
    val idx = indexOf(predicate)
    return if (idx == -1) null else this[idx]
}

