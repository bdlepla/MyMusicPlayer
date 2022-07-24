package com.bdlepla.android.mymusicplayer.Extensions


fun Int.toMinutesSeconds():String {
    val min = this / 60
    val sec = this % 60
    return "${min}:" + "%02d".format(sec)
}
