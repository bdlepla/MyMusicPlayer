package com.mpatric.mp3agic

import com.mpatric.mp3agic.BaseException

class NotSupportedException : BaseException {
    constructor() : super() {}
    constructor(message: String?) : super(message) {}
    constructor(message: String?, cause: Throwable?) : super(message, cause) {}

    companion object {
        private const val serialVersionUID = 1L
    }
}