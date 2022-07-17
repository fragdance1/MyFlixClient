package com.fragdance.myflixclient.services

import com.fragdance.myflixclient.models.IOpenSubtitle
import com.fragdance.myflixclient.models.ISubtitle
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SubtitleService {

    @GET("subtitle/search")
    fun searchSubtitle(@Query("url") url:String?,@Query("name") name:String?,@Query("imdb") imdb:String?):Call<List<ISubtitle>>
}
interface SubtitleStringService {
    @GET("subtitle/download")
    fun downloadSubtitle(@Query("url") url:String,@Query("video_id") vide_if:Long?):Call<String>
    @GET("subtitle/{id}")
    fun get(@Path("id") id:Int):Call<String>
}
val subtitleService = JSONServiceBuilder.buildService(SubtitleService::class.java);
val subtitleStringService = StringServiceBuilder.buildService(SubtitleStringService::class.java);
