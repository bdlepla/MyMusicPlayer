package com.bdlepla.android.mymusicplayer.Extensions


val ignoreBeginningArticles = listOf("A ", "The Very Best Of ", "The Best Of ", "The ")
fun String.forSorting(): String {
    val s = if (this.startsWith("(")){
        val idx = this.indexOf(')')
        this.substring(idx+1).trim()
    } else {
        this
    }
    for (i in ignoreBeginningArticles) {
        if (s.startsWith(i,  ignoreCase = true)) {
            return s.substring(i.length)
        }
    }
    return s
}
