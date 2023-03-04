package com.bdlepla.android.mymusicplayer.business

data class PlaylistInfo(val name:String, val songs:List<SongInfo>, val artwork:String?=null)