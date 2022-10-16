package com.fragdance.myflixclient.services

import com.fragdance.myflixclient.models.IMovie
import com.fragdance.myflixclient.models.IMovieDetails
import com.fragdance.myflixclient.models.IPersonDetails
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PersonService {
    @GET("api/person/details/{id}")
    fun getPersonDetails(@Path("id") id:Int):Call<IPersonDetails>

    @GET("api/person/movies/{id}")
    fun getMovies(@Path("id") id:String):Call<List<IMovie>>
}

val personService  = JSONServiceBuilder.buildService(PersonService::class.java)
