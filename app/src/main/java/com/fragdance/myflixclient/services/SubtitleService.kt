package com.fragdance.myflixclient.services

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface SubtitleService {
    @GET("subtitle/get")
    fun getSubtitle(@Query("id") id:Int): Call<String>
}

val subtitleService = StringServiceBuilder.buildService(SubtitleService::class.java);