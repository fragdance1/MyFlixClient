package com.fragdance.myflixclient.services

import com.fragdance.myflixclient.models.IMovie
import com.fragdance.myflixclient.models.IMovieDetails
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieService {

    @GET("movie/all")
    fun getLatestMovies(): Call<List<IMovie>>

    @GET("movie/details")
    fun getMovieDetails(@Query("id") id:Int):Call<IMovieDetails>


}

val movieService  = JSONServiceBuilder.buildService(MovieService::class.java)
