package com.fragdance.myflixclient.services

import com.fragdance.myflixclient.models.IMovie
import com.fragdance.myflixclient.models.IMovieDetails

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieService {
    @GET("movies")
    fun getLocalMovies(): Call<List<IMovie>>

    @GET("movie/latest")
    fun getLatestMovies(): Call<List<IMovie>>

    @GET("movie/details/{id}")
    fun getMovieDetails(@Path("id") id:String):Call<IMovieDetails>

    @GET("movie/boxoffice")
    fun getBoxOfficeMovies():Call<List<IMovie>>

    @GET("movie/recommended")
    fun getRecommendedMovies():Call<List<IMovie>>

    @GET("movie/trending")
    fun getTrendingMovies():Call<List<IMovie>>

    @GET("movie/inprogress")
    fun getInProgressMovies():Call<List<IMovie>>
    @GET("movie/genre/{genre}")
    fun getMoviesByGenre(@Path("genre") genre:String):Call<List<IMovie>>
}

val movieService  = JSONServiceBuilder.buildService(MovieService::class.java)
