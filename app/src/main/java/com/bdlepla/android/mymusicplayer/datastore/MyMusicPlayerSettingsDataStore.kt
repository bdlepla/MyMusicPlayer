package com.bdlepla.android.mymusicplayer.datastore

import android.content.Context
import com.bdlepla.android.mymusicplayer.MyMusicPlayerSettings
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import kotlin.io.path.Path
import kotlin.io.path.exists

class MyMusicPlayerSettingsDataStore @Inject constructor(private val context: Context) {
    private var _settings: MyMusicPlayerSettings = MyMusicPlayerSettingsSerializer().defaultValue
    val settings: MyMusicPlayerSettings
        get() = _settings

    init {
        if (exists) {
            _settings = runBlocking { readFile() }
        }
    }

    suspend fun saveSettings(setting: MyMusicPlayerSettings) {
        _settings = setting
        writeFile(setting)
    }

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
}
