package com.fragdance.myflixclient.services

import com.fragdance.myflixclient.models.IMovie
import com.fragdance.myflixclient.models.IMovieDetails
import com.fragdance.myflixclient.models.IPersonDetails
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PersonService {
    @GET("person/details")
    fun getPersonDetails(@Query("id") id:Int):Call<IPersonDetails>
}

val personService  = JSONServiceBuilder.buildService(PersonService::class.java)
