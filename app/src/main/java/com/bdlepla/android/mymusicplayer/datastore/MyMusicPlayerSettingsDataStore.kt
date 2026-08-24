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
        get() = context.getExternalFilesDir(null)?.absolutePath?.let { Path(it) }

    private val getFullFilename
        get() = appStoragePath?.resolve("MyMusicPlayerSettings.pb")?.toString()

    private val exists
        get() = getFullFilename?.let { Path(it).exists() } ?: false

    private suspend fun readFile(): MyMusicPlayerSettings =
        getFullFilename?.let { filename ->
            Path(filename).toFile().inputStream().use {
                return MyMusicPlayerSettingsSerializer().readFrom(it)
            }
        } ?: MyMusicPlayerSettingsSerializer().defaultValue

    private suspend fun writeFile(setting: MyMusicPlayerSettings) {
        getFullFilename?.let { filename ->
            Path(filename).toFile().outputStream().use {
                MyMusicPlayerSettingsSerializer().writeTo(setting, it)
            }
        }
    }
}
