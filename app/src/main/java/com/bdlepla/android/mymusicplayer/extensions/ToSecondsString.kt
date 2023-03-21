package com.bdlepla.android.mymusicplayer.extensions


fun Int.toMinutesSeconds():String {
    val min = this / 60
    val sec = this % 60
    return "${min}:" + "%02d".format(sec)
}

fun Int.toHourMinutesSeconds():String {
    val hour = this / 3600
    val minutes = this % 3600
    val min = minutes / 60
    val sec = minutes % 60
    return "${hour}:" + "%02d".format(min) + ":" +  "%02d".format(sec)
}
