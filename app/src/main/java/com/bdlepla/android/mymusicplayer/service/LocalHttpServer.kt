package com.bdlepla.android.mymusicplayer.service

import fi.iki.elonen.NanoHTTPD
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.net.NetworkInterface
import java.util.Collections

class LocalHttpServer(port: Int) : NanoHTTPD(port) {

    override fun serve(session: IHTTPSession): Response {
        val uri = session.uri
        val file = File(uri)

        if (file.exists() && file.isFile) {
            return try {
                val fis = FileInputStream(file)
                val mimeType = getMimeType(uri)
                newFixedLengthResponse(Response.Status.OK, mimeType, fis, file.length())
            } catch (_: IOException) {
                newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "Error reading file")
            }
        }
        return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "File not found")
    }

    private fun getMimeType(uri: String): String {
        return when {
            uri.endsWith(".mp3", ignoreCase = true) -> "audio/mpeg"
            uri.endsWith(".aac", ignoreCase = true) -> "audio/aac"
            uri.endsWith(".wav", ignoreCase = true) -> "audio/wav"
            uri.endsWith(".ogg", ignoreCase = true) -> "audio/ogg"
            uri.endsWith(".flac", ignoreCase = true) -> "audio/flac"
            uri.endsWith(".m4a", ignoreCase = true) -> "audio/mp4"
            uri.endsWith(".jpg", ignoreCase = true) || uri.endsWith(".jpeg", ignoreCase = true) -> "image/jpeg"
            uri.endsWith(".png", ignoreCase = true) -> "image/png"
            uri.endsWith(".css", ignoreCase = true) -> "text/css"
            else -> "application/octet-stream"
        }
    }

    companion object {
        fun getLocalIpAddress(): String? {
            try {
                val interfaces = NetworkInterface.getNetworkInterfaces()
                for (intf in Collections.list(interfaces)) {
                    val addrs = Collections.list(intf.inetAddresses)
                    for (addr in addrs) {
                        if (!addr.isLoopbackAddress) {
                            val sAddr = addr.hostAddress
                            val isIPv4 = sAddr!!.indexOf(':') < 0
                            if (isIPv4) return sAddr
                        }
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return null
        }
    }
}
