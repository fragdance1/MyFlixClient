package com.fragdance.myflixclient.services

import com.fragdance.myflixclient.Settings
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okio.IOException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

class HostSelectionInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        Settings.SERVER_IP?.let { host ->
            Timber.tag(Settings.TAG).d("New url "+host)
            val request = chain.request()

            val newUrl = request.url.newBuilder().host(host).port(8000).build()

            val newRequest = request.newBuilder().url(newUrl).build()
            return chain.proceed(newRequest)
        }
        throw IOException("Unknown Server")
    }
}

object JSONServiceBuilder {
    var BASE_URL = "http://127.0.0.1"
    private val okHttp = OkHttpClient.Builder().addInterceptor(HostSelectionInterceptor()).callTimeout(60,TimeUnit.SECONDS).build()

    private val builder = Retrofit.Builder().baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttp)

    private val retrofit = builder.build()

    fun <T> buildService (serviceType :Class<T>):T{
        return retrofit.create(serviceType)
    }
}

object StringServiceBuilder {
    private val okHttp = OkHttpClient.Builder().addInterceptor(HostSelectionInterceptor())
    var BASE_URL = "http://127.0.0.1"//Settings.SERVER+"/api/"
    private val builder = Retrofit.Builder().baseUrl(BASE_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .client(okHttp.build())

    private val retrofit = builder.build()

    fun <T> buildService (serviceType :Class<T>):T{
        return retrofit.create(serviceType)
    }

}