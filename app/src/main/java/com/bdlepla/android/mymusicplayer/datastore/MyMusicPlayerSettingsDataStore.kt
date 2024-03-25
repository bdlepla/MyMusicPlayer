package com.bdlepla.android.mymusicplayer.datastore

import android.content.Context
import com.bdlepla.android.mymusicplayer.MyMusicPlayerSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.io.path.Path
import kotlin.io.path.exists

class MyMusicPlayerSettingsDataStore @Inject constructor(private val context: Context) {
    private var _settings:MyMusicPlayerSettings = MyMusicPlayerSettingsSerializer().defaultValue
    private var settings:MyMusicPlayerSettings
        get() = _settings
        set(value)  {_settings = value}

    init {
        if (exists) { settings = runBlocking { readFile() } }
    }

    val playingPosition: Long = settings.playingPosition
    val playingSongId: Long = settings.playingSongId
    val playingList: List<Long> = settings.currentListIdsList
    private val appStoragePath
        get() = Path(context.getExternalFilesDir(null).toString())

    private val getFullFilename
        get() = "$appStoragePath/MyMusicPlayerSettings.pb"
    private val exists
        get() = Path(getFullFilename).exists()

    private suspend fun readFile():MyMusicPlayerSettings =
        Path(getFullFilename).toFile().inputStream().use{
            return MyMusicPlayerSettingsSerializer().readFrom(it)
        }
    private suspend fun writeFile(setting:MyMusicPlayerSettings) {
        Path(getFullFilename).toFile().outputStream().use {
            return MyMusicPlayerSettingsSerializer().writeTo(setting, it)
        }
    }

    suspend fun saveCurrentList(ids:List<Long>) {
        settings = settings.toBuilder().clearCurrentListIds().addAllCurrentListIds(ids).build()
        withContext(Dispatchers.IO) { writeFile(settings) }
    }

    suspend fun saveCurrentPlaying(songId:Long, position:Long) {
        settings = settings.toBuilder().setPlayingSongId(songId).setPlayingPosition(position).build()
        withContext(Dispatchers.IO) { writeFile(settings) }
    }
}