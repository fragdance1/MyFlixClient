package com.fragdance.myflixclient.services

import com.fragdance.myflixclient.models.IFilter
import com.fragdance.myflixclient.models.IMovie
import com.fragdance.myflixclient.models.IMovieDetails

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieService {
    @GET("api/movies")
    fun getLocalMovies(): Call<List<IMovie>>

    @GET("api/movie/filter/{id}")
    fun getFilteredMovies(@Path("id") id:Int):Call<List<IMovie>>

    @GET("api/movie/filters")
    fun getMovieFilters():Call<List<IFilter>>
    @GET("api/movie/latest")
    fun getLatestMovies(): Call<List<IMovie>>

    @GET("api/movie/details/{id}")
    fun getMovieDetails(@Path("id") id:String):Call<IMovieDetails>

    @GET("api/movies/boxoffice")
    fun getBoxOfficeMovies():Call<List<IMovie>>

    @GET("api/movies/recommended")
    fun getRecommendedMovies():Call<List<IMovie>>

    @GET("api/movie/inprogress")
    fun getInProgressMovies():Call<List<IMovie>>
    @GET("api/movie/genre/{genre}")
    fun getMoviesByGenre(@Path("genre") genre:String):Call<List<IMovie>>
}

val movieService  = JSONServiceBuilder.buildService(MovieService::class.java)
