package com.fragdance.myflixclient.models
import java.io.Serializable
data class ITVShow (
    val id:Long,
    var title:String,
    var poster:String?,
    var backdrop:String?,
    val overview:String?
) : Serializable

data class ISeason (
    val seasonNumber:Long,
    val title:String?,
    val episodes:List<IEpisode>
):Serializable

data class IEpisode (
    val id:Long,
    var name:String,
    var seasonNumber:Int,
    var episodeNumber:Int,
    var overview:String?,
    val still:String?,
    val owned:Boolean,
    var video_files:List<IVideoFile> = emptyList()
):Serializable

data class IEpisodeCardData(val id:String,val title:String,val subtitle:String?,val poster:String?,val video:IVideo?):Serializable