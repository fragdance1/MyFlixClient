package com.fragdance.myflixclient.services

import com.fragdance.myflixclient.models.*

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TVShowService {
    @GET("api/tvshows")
    fun getShows(): Call<List<ITVShow>>
    @GET("api/tvshow/seasons/{id}")
    fun getSeasons(@Path("id") id:Long):Call<List<ISeason>>
    @GET("api/tvshow/{id}/lastwatched")
    fun getLastWatched(@Path("id") id:Long):Call<IEpisode>
    @GET("api/tvshow/playlist/{id}")
    fun getPlaylist(@Path("id") id:Long): Call<List<IVideo>>

    @GET("api/trakttv/start_episode")
    fun startEpisode(@Query("id") id:Long, @Query("access_token") access_token:String,@Query("progress") progress:Int):Call<Unit>

}

val tvShowService  = JSONServiceBuilder.buildService(TVShowService::class.java)
