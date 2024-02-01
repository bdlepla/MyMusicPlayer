package com.bdlepla.android.mymusicplayer.datastore

import android.content.Context
import com.bdlepla.android.mymusicplayer.MyMusicPlayerSettings
import kotlinx.coroutines.runBlocking
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

    val PlayingPosition: Long = settings.playingPosition
    val PlayingSongId: Long = settings.playingSongId
    val PlayingList: List<Long> = settings.currentListIdsList
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



    fun  saveCurrentList(ids:List<Long>) {
        settings = settings.toBuilder().clearCurrentListIds().addAllCurrentListIds(ids).build()
        runBlocking{writeFile(settings)}
    }
    fun saveCurrentPlaying(songId:Long, position:Long) {
        settings = settings.toBuilder().setPlayingSongId(songId).setPlayingPosition(position).build()
        runBlocking { writeFile(settings) }
    }
}