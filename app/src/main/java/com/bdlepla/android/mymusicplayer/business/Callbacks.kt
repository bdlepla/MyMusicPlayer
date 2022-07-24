package com.bdlepla.android.mymusicplayer.business

import com.bdlepla.android.mymusicplayer.ui.emptyFunction

class Callbacks (
    val onPlay:()->Unit = emptyFunction(),
    val onPause:()->Unit = emptyFunction(),
    val onNext:()->Unit = emptyFunction(),
    val onForward:()->Unit = emptyFunction(),
    val onReverse:()->Unit = emptyFunction(),
    val onPrevious:()->Unit = emptyFunction()
    )