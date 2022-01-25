package com.mpatric.mp3agic

open class BaseException : Exception {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)

    val detailedMessage: String
        get() {
            var t: Throwable? = this
            val s = StringBuilder()
            while (true) {
                s.append('[')
                s.append(t!!.javaClass.name)
                if (t.message != null && t.message!!.isNotEmpty()) {
                    s.append(": ")
                    s.append(t.message)
                }
                s.append(']')
                t = t.cause
                if (t != null) {
                    s.append(" caused by ")
                } else {
                    break
                }
            }
            return s.toString()
        }

    companion object {
        private const val serialVersionUID = 1L
    }
}