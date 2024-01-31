package com.bdlepla.android.mymusicplayer.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.bdlepla.android.mymusicplayer.MyMusicPlayerSettings
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream
import javax.inject.Inject

class MyMusicPlayerSettingsSerializer @Inject constructor() : Serializer<MyMusicPlayerSettings> {
    override val defaultValue: MyMusicPlayerSettings = MyMusicPlayerSettings.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): MyMusicPlayerSettings =
        try {
            // readFrom is already called on the data store background thread
            MyMusicPlayerSettings.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }

    override suspend fun writeTo(t: MyMusicPlayerSettings, output: OutputStream) {
        // writeTo is already called on the data store background thread
        t.writeTo(output)
    }
}