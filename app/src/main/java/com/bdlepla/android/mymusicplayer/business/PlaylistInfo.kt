package com.bdlepla.android.mymusicplayer.business

import com.danrusu.pods4k.immutableArrays.ImmutableArray

data class PlaylistInfo(val name:String, val songs:ImmutableArray<SongInfo>, val artwork:String?=null)
