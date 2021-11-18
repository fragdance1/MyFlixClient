package com.fragdance.myflixclient.models

import java.io.Serializable
data class IMovie(
    var id:Int,
    var title:String,
    var poster:String?,
    var overview:String?,
    var video_files:List<IVideoFile> = emptyList()
):Serializable
