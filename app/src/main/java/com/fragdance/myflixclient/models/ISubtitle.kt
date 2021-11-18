package com.fragdance.myflixclient.models

import java.io.Serializable

data class ISubtitle(
    val id:Int,
    val video_file:IVideoFile?,
    val type:String,
    val language:String = "und",
    val filename:String
):Serializable
