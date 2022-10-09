package com.fragdance.myflixclient.services

import com.fragdance.myflixclient.models.IMovie
import com.fragdance.myflixclient.models.IVideo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ServerService {
    @GET("ping")
    fun ping():Call<Boolean>

}

val serverService  = StringServiceBuilder.buildService(ServerService::class.java)