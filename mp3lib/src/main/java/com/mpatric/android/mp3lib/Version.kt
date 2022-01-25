package com.mpatric.mp3agic

object Version {
    var version: String? = null
    const val url = "http://github.com/mpatric/mp3agic"
    fun asString(): String {
        return version + " - " + url
    }

    init { // get version from JAR manifest
        val implementationVersion = Version::class.java.getPackage().implementationVersion
        version = "UNKNOWN-SNAPSHOT"
            //if (com.mpatric.mp3agic.implementationVersion != null) com.mpatric.mp3agic.implementationVersion else "UNKNOWN-SNAPSHOT"
    }
}