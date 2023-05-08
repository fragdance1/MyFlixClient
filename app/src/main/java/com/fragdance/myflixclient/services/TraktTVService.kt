package com.fragdance.myflixclient.services

import com.fragdance.myflixclient.models.IMovie
import com.fragdance.myflixclient.models.IMovieSearchResult
import com.fragdance.myflixclient.models.IVideo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TraktTVService {


    @GET("api/trakttv/set_movie_watched")
    fun setMovieWatched(@Query("access_token") access_token:String,
    @Query("id") id:String,
    @Query("watched_at") watched_at:String):Call<Unit>
    /* These needs to be renames (url) */
    @GET("api/trakttv/set_episode_watched")
    fun setEpisodeWatched(@Query("access_token") access_token:String,
                          @Query("id") id:String,
        @Query("watched_at") watched_at:String):Call<Unit>

    @GET("api/movie/search")
    fun search(@Query("query") query:String):Call<IMovieSearchResult>
}

val trakttvService  = JSONServiceBuilder.buildService(TraktTVService::class.java)