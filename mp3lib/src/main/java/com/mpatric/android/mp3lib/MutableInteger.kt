package com.mpatric.mp3agic

class MutableInteger(var value: Int) {
    fun increment() {
        value++
    }

    override fun hashCode(): Int {
        val prime = 31
        var result = 1
        result = prime * result + value
        return result
    }

    override fun equals(obj: Any?): Boolean {
        if (this === obj) return true
        if (obj == null) return false
        if (javaClass != obj.javaClass) return false
        val other = obj as MutableInteger
        return if (value != other.value) false else true
    }
}