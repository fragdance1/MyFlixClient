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
    var url:String?,
    var subtitles:List<ISubtitle> = emptyList()
):Serializable

data class IPlayList(
    var videos:ArrayList<IVideo> = arrayListOf()
):Serializable

data class IMovieDetails(
    var id:Int,
    var url:String?,
    var title:String,
    var overview:String?,
    var poster:String,
    var backdrop:String,
    var cast:List<ICast>,
    var crew:List<ICrew>,
    var video_files:List<IVideoFile> = emptyList()
):Serializable