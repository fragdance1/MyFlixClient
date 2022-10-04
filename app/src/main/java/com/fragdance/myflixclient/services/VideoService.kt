package com.fragdance.myflixclient.services

import com.fragdance.myflixclient.models.IMovie
import com.fragdance.myflixclient.models.IMovieDetails

import retrofit2.Call
import retrofit2.http.*

data class ScrobbleData(private val progress: Float)
interface VideoService {
    @POST("video/start/{type}/{id}")
    fun start(@Path("type") type:String, @Path("id") id:String, @Body data:ScrobbleData):Call<Unit>
    @POST("video/stop/{type}/{id}")
    fun stop(@Path("type") type:String, @Path("id") id:String, @Body data:ScrobbleData):Call<Unit>

}

val videoService  = JSONServiceBuilder.buildService(VideoService::class.java)
