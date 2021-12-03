package com.fragdance.myflixclient.models

import java.io.Serializable
data class IMovie(
    var id:Int,
    var title:String,
    var poster:String?,
    var overview:String?,
    var url:String,
    var video_files:List<IVideoFile> = emptyList()

):Serializable

data class IVideo(
    var id:Int,
    var extension:String,
    var title:String,
    var poster:String?,
    var overview:String?,
    var url:String,
    var subtitles:List<ISubtitle> = emptyList()
):Serializable

data class IPlayList(
    var videos:ArrayList<IVideo> = arrayListOf()
):Serializable