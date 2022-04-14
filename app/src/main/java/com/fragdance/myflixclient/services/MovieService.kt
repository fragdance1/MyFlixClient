package com.fragdance.myflixclient.services

import com.fragdance.myflixclient.models.IMovie
import com.fragdance.myflixclient.models.IMovieDetails

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieService {
    @GET("movie/all")
    fun getLocalMovies(): Call<List<IMovie>>

    @GET("movie/latest")
    fun getLatestMovies(): Call<List<IMovie>>

    @GET("movie/details")
    fun getMovieDetails(@Query("id") id:String):Call<IMovieDetails>

    @GET("movie/boxoffice")
    fun getBoxOfficeMovies():Call<List<IMovie>>

    @GET("movie/recommended")
    fun getRecommendedMovies():Call<List<IMovie>>

    @GET("movie/trending")
    fun getTrendingMovies():Call<List<IMovie>>

    @GET("movie/genre")
    fun getMoviesByGenre(@Query("genre") genre:String):Call<List<IMovie>>
}

val movieService  = JSONServiceBuilder.buildService(MovieService::class.java)
