package com.fragdance.myflixclient.services

import com.fragdance.myflixclient.models.IOpenSubtitle
import com.fragdance.myflixclient.models.ISubtitle
import retrofit2.Call
import retrofit2.http.*

interface SubtitleService {
    @GET("api/subtitle/search")
    fun searchSubtitle(@Query("url") url:String?,@Query("name") name:String?,@Query("imdb") imdb:String?):Call<List<ISubtitle>>
    @POST("api/subtitle/download")
    fun downloadSubtitle(@Query("video_id") video_id:Long?,@Query("hash") hash:String?, @Body data:ISubtitle):Call<String>
}

interface SubtitleStringService {

    @GET("api/subtitle/{id}")
    fun get(@Path("id") id:Int):Call<String>
}
val subtitleService = JSONServiceBuilder.buildService(SubtitleService::class.java);
val subtitleStringService = StringServiceBuilder.buildService(SubtitleStringService::class.java);
