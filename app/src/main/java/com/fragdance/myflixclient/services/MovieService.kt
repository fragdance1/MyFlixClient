package com.fragdance.myflixclient.services

import com.fragdance.myflixclient.models.IMovie
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieService {
    @GET("movie/all")
    fun getLatestMovies(): Call<List<IMovie>>
}

val movieService  = JSONServiceBuilder.buildService(MovieService::class.java)
