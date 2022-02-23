package com.bdlepla.android.mymusicplayer.Extensions

import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.UnstableApi
import com.bdlepla.android.mymusicplayer.business.SongInfo
import com.bdlepla.android.mymusicplayer.repository.Repository

@UnstableApi
fun MediaItem.toSongInfo(): SongInfo = SongInfo(this)

