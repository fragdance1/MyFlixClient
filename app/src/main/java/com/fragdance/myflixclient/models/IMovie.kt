package com.fragdance.myflixclient.models

import java.io.Serializable

class IMovie(
    var id:String,
    var title:String,
    var poster:String?,
    var overview:String?,
    var url:String?,
    var video_files:List<IVideoFile> = emptyList(),
    var discs:List<IDisc> = emptyList(),
    var progress:Float?,
    var disc:Boolean?,
    var videofile:Boolean?,
    var watched:Boolean?
):Serializable

class IMovieSearchResult(
    var page:Int,
    var results:List<IMovie> = emptyList(),
    var total_pages:Int,
    var total_result:Int,

)
data class IFilter(val id:Int,val name:String):Serializable
data class IMovieCardData(val id:String,val title:String,val subtitle:String?,val poster:String?,val progress:Float?,val disc:Boolean?,val videofile:Boolean?,val watched:Boolean?):Serializable

data class IVideo(
    var id:Long,
    var extension:String,
    var title:String,
    var poster:String?,
    var overview:String?,
    var url:String?,
    var hash:String?,
    var subtitles:MutableList<ISubtitle> = mutableListOf(),
    var type:String?,
    var tmdbId:Long?,
    var imdbId:String?, // For movies
    var progress:Float = 0.0f

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