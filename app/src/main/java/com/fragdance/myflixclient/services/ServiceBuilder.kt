package com.fragdance.myflixclient.services

import com.fragdance.myflixclient.Settings
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import timber.log.Timber



object JSONServiceBuilder {
    var BASE_URL = Settings.SERVER+"/api/"
    private val okHttp = OkHttpClient.Builder()

    private val builder = Retrofit.Builder().baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttp.build())

    private val retrofit = builder.build()

    fun <T> buildService (serviceType :Class<T>):T{
        return retrofit.create(serviceType)
    }
}

object StringServiceBuilder {
    private val okHttp = OkHttpClient.Builder()
    var BASE_URL = Settings.SERVER+"/api/"
    private val builder = Retrofit.Builder().baseUrl(BASE_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .client(okHttp.build())

    private val retrofit = builder.build()

    fun <T> buildService (serviceType :Class<T>):T{
        return retrofit.create(serviceType)
    }
}