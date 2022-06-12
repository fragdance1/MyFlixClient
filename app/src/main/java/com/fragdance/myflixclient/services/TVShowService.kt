package com.fragdance.myflixclient.services

import com.fragdance.myflixclient.models.IMovie
import com.fragdance.myflixclient.models.IMovieDetails
import com.fragdance.myflixclient.models.IVideo

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TVShowService {
    @GET("tvshow/playlist")
    fun getPlaylist(@Query("id") id:Long): Call<List<IVideo>>

    @GET("trakttv/start_episode")
    fun startEpisode(@Query("id") id:Long, @Query("access_token") access_token:String,@Query("progress") progress:Int):Call<Unit>

}

val tvShowService  = JSONServiceBuilder.buildService(TVShowService::class.java)
