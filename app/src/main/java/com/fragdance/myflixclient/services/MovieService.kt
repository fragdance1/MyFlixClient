package com.fragdance.myflixclient.services

import com.fragdance.myflixclient.models.IMovie
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface MovieService {
    @GET("movie/all")
    fun getLatestMovies(): Call<List<IMovie>>
}

interface SubtitleService {
    @GET("subtitle/get")
    fun getSubtitle(@Query("id") id:Int):Call<String>
}

val movieService  = JSONServiceBuilder.buildService(MovieService::class.java)
val subtitleService = StringServiceBuilder.buildService(SubtitleService::class.java);