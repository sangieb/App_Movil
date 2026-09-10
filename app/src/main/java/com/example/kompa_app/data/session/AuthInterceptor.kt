package com.example.kompa_app.data.session

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val authRepository: AuthRepository
) : Interceptor {

    override fun intercept(cadena: Interceptor.Chain): Response {
        val token = authRepository.tokenAcceso()
        val request = if (token == null) {
            cadena.request()
        } else {
            cadena.request().newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        }
        return cadena.proceed(request)
    }
}