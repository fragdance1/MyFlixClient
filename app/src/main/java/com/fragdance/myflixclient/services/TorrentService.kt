package com.fragdance.myflixclient.services

import com.fragdance.myflixclient.models.*
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TorrentService {
    @GET("torrent/search/{imdb}")
    fun getMovieTorrents(@Path("imdb") imdb:String): Call<List<ITorrent>>

    @GET("torrent/add")
    fun addMovieTorrent(@Query("url")  url:String, @Query("id") id:Long,@Query("hash") hash:String):Call<IStatus>
}

val torrentService  = JSONServiceBuilder.buildService(TorrentService::class.java)