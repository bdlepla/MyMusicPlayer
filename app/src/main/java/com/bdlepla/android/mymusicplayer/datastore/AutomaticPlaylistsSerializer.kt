package com.bdlepla.android.mymusicplayer.datastore

import androidx.datastore.core.Serializer
import com.bdlepla.android.mymusicplayer.AutomaticPlaylists
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class AutomaticPlaylistsSerializer @Inject constructor() : Serializer<AutomaticPlaylists> {
    override val defaultValue: AutomaticPlaylists = AutomaticPlaylists.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): AutomaticPlaylists =
        try {
            AutomaticPlaylists.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            AutomaticPlaylists.getDefaultInstance()
        }

    override suspend fun writeTo(t: AutomaticPlaylists, output: OutputStream) {
        t.writeTo(output)
    }
}
