package com.fragdance.myflixclient.models

import java.io.Serializable

class IMovie(
    var id:String,
    var title:String,
    var poster:String?,
    var overview:String?,
    var url:String?,
    var video_files:List<IVideoFile> = emptyList(),
    var progress:Float?
):Serializable

data class IMovieCardData(val id:String,val title:String,val subtitle:String?,val poster:String?,val progress:Float?):Serializable

data class IVideo(
    var id:Long,
    var extension:String,
    var title:String,
    var poster:String?,
    var overview:String?,
    var url:String?,
    var hash:String?,
    var subtitles:List<ISubtitle> = emptyList(),
    var type:String?,
    var tmdbId:Long?,
    var imdbId:String? // For movies

):Serializable

data class IPlayList(
    var videos:ArrayList<IVideo> = arrayListOf(),
    var position:Long = 0
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
    var imdb_id:String,
    var video_files:List<IVideoFile> = emptyList(),
    var progress:Float,
    var year:Int
):Serializable