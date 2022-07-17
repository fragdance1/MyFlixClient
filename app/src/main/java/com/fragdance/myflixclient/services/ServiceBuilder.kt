package com.fragdance.myflixclient.services

import com.fragdance.myflixclient.Settings
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit


object JSONServiceBuilder {
    var BASE_URL = Settings.SERVER+"/api/"
    private val okHttp = OkHttpClient.Builder().callTimeout(60,TimeUnit.SECONDS).build()

    private val builder = Retrofit.Builder().baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttp)

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