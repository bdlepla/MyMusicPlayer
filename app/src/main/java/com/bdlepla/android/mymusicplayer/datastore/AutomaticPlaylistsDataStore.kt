package com.bdlepla.android.mymusicplayer.datastore

import android.content.Context
import com.bdlepla.android.mymusicplayer.AutomaticPlaylists
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import kotlin.io.path.Path
import kotlin.io.path.exists

class AutomaticPlaylistsDataStore @Inject constructor(private val context: Context) {
    private var _settings: AutomaticPlaylists = AutomaticPlaylistsSerializer().defaultValue
    val settings: AutomaticPlaylists
        get() = _settings

    init {
        if (exists) {
            _settings = runBlocking { readFile() }
        }
    }

    suspend fun saveSettings(setting: AutomaticPlaylists) {
        _settings = setting
        writeFile(setting)
    }

    private val appStoragePath
        get() = context.getExternalFilesDir(null)?.absolutePath?.let { Path(it) }

    private val getFullFilename
        get() = appStoragePath?.resolve("AutomaticPlaylists.pb")?.toString()

    private val exists
        get() = getFullFilename?.let { Path(it).exists() } ?: false

    private suspend fun readFile(): AutomaticPlaylists =
        getFullFilename?.let { filename ->
            Path(filename).toFile().inputStream().use {
                return AutomaticPlaylistsSerializer().readFrom(it)
            }
        } ?: AutomaticPlaylistsSerializer().defaultValue

    private suspend fun writeFile(setting: AutomaticPlaylists) {
        getFullFilename?.let { filename ->
            Path(filename).toFile().outputStream().use {
                AutomaticPlaylistsSerializer().writeTo(setting, it)
            }
        }
    }
}
