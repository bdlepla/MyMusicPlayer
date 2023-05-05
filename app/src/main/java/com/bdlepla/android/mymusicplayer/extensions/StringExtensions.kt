package com.bdlepla.android.mymusicplayer.extensions


val ignoreBeginningArticles = listOf("A ", "The Very Best Of ", "The Best Of ", "The ", "An ", "El ")
fun String.forSorting(): String {
    val s = if (this.startsWith("(")){
        val idx = this.indexOf(')')
        this.substring(idx+1).trim().lowercase()
    } else {
        this.lowercase()
    }
    for (i in ignoreBeginningArticles) {
        if (s.startsWith(i,  ignoreCase = true)) {
            return s.substring(i.length).lowercase()
        }
    }
    return s.lowercase()
}
