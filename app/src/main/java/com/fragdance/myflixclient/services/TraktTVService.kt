package com.fragdance.myflixclient.services

import com.fragdance.myflixclient.models.IMovie
import com.fragdance.myflixclient.models.IVideo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TraktTVService {
    @GET("trakttv/start_movie")
    fun startMovie(@Query("access_token") access_token:String,
    @Query("id") id:String,
    @Query("progress") progress:String):Call<Unit>

    @GET("trakttv/stop_movie")
    fun stopMovie(@Query("access_token") access_token:String,
    @Query("id") id:String,
    @Query("progress") progress:Float):Call<Unit>

    @GET("trakttv/set_movie_watched")
    fun setMovieWatched(@Query("access_token") access_token:String,
    @Query("id") id:String,
    @Query("watched_at") watched_at:String):Call<Unit>

    @GET("trakttv/start_episode")
    fun startEpisode(@Query("access_token") access_token:String,
        @Query("id") id:String,
        @Query("progress") progress:String): Call<Unit>

    @GET("trakttv/stop_episode")
    fun stopEpisode(@Query("access_token") access_token:String,
         @Query("id") id:String,
         @Query("progress") progress:Float): Call<Unit>

    @GET("trakttv/set_episode_watched")
    fun setEpisodeWatched(@Query("access_token") access_token:String,
                          @Query("id") id:String,
        @Query("watched_at") watched_at:String):Call<Unit>

    @GET("trakttv/search")
    fun search(@Query("query") query:String,@Query("count") count:Int = 10,@Query("page") page:Int = 1):Call<List<IMovie>>
}

val trakttvService  = JSONServiceBuilder.buildService(TraktTVService::class.java)