package com.example.kompa_app.data.network

import com.example.kompa_app.BuildConfig
import com.example.kompa_app.Constantes
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private val interceptorLog = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BASIC
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private fun cliente(lecturaSegundos: Long): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor { cadena ->
                val request = cadena.request().newBuilder()
                    .header("User-Agent", Constantes.USER_AGENT)
                    .build()
                cadena.proceed(request)
            }
            .addInterceptor(interceptorLog)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(lecturaSegundos, TimeUnit.SECONDS)
            .build()
    }

    val nominatimApi: NominatimApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")
            .client(cliente(lecturaSegundos = 20))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NominatimApi::class.java)
    }
}