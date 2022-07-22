package com.fragdance.myflixclient.services

import com.fragdance.myflixclient.models.*

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TVShowService {
    @GET("tvshows")
    fun getShows(): Call<List<ITVShow>>
    @GET("tvshow/seasons/{id}")
    fun getSeasons(@Path("id") id:Long):Call<List<ISeason>>
    @GET("tvshow/playlist/{id}")
    fun getPlaylist(@Path("id") id:Long): Call<List<IVideo>>

    @GET("trakttv/start_episode")
    fun startEpisode(@Query("id") id:Long, @Query("access_token") access_token:String,@Query("progress") progress:Int):Call<Unit>

}

val tvShowService  = JSONServiceBuilder.buildService(TVShowService::class.java)
