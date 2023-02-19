package com.bdlepla.android.mymusicplayer.extensions


fun Int.toMinutesSeconds():String {
    val min = this / 60
    val sec = this % 60
    return "${min}:" + "%02d".format(sec)
}
