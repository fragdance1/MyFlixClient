package com.fragdance.myflixclient.services

import com.fragdance.myflixclient.models.IMovieDetails
import com.fragdance.myflixclient.models.IMovieTorrent
import com.fragdance.myflixclient.models.ITorrent
import com.fragdance.myflixclient.models.ITorrentDetails
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TorrentService {
    @GET("torrent/movie")
    fun getMovieTorrents(@Query("id") id:String): Call<IMovieTorrent>

    @GET("torrent/add")
    fun addMovieTorrent(@Query("url")  url:String, @Query("id") id:Int):Call<ITorrentDetails>
}

val torrentService  = JSONServiceBuilder.buildService(TorrentService::class.java)