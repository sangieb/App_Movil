package com.example.kompa_app.data.network

import com.example.kompa_app.BuildConfig
import com.example.kompa_app.Constantes
import com.example.kompa_app.data.session.AuthInterceptor
import com.example.kompa_app.data.session.AuthRepository
import com.example.kompa_app.data.session.TokenAuthenticator
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient(
    private val authRepository: AuthRepository
) {

    private val interceptorLog = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BASIC
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    val nominatimApi: NominatimApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://nominatim.openstreetmap.org/")
            .client(cliente(lecturaSegundos = 20, autenticado = false))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NominatimApi::class.java)
    }

    val publicacionApi: PublicacionApi by lazy {
        Retrofit.Builder()
            .baseUrl(Constantes.API_BASE_URL)
            .client(cliente(lecturaSegundos = 20, autenticado = true))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PublicacionApi::class.java)
    }

    private fun cliente(lecturaSegundos: Long, autenticado: Boolean): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor { cadena ->
                val request = cadena.request().newBuilder()
                    .header("User-Agent", Constantes.USER_AGENT)
                    .build()
                cadena.proceed(request)
            }
            .addInterceptor(interceptorLog)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(lecturaSegundos, TimeUnit.SECONDS)
        if (autenticado) {
            builder
                .addInterceptor(AuthInterceptor(authRepository))
                .authenticator(TokenAuthenticator(authRepository))
        }
        return builder.build()
    }
}