package com.fragdance.myflixclient.services

import com.fragdance.myflixclient.models.IMovie
import com.fragdance.myflixclient.models.ISeason
import com.fragdance.myflixclient.models.ITVShow
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SonarrService {
    @GET("sonarr/seasons")
    fun getSeasons(@Query("id") id:Long):Call<List<ISeason>>
}

val sonarrService  = JSONServiceBuilder.buildService(SonarrService::class.java)