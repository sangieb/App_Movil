package com.example.kompa_app.data.session

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val authRepository: AuthRepository
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.request.header("Authorization") == null) {
            return null
        }
        val renovado = runBlocking(Dispatchers.IO) {
            authRepository.renovarSiEsNecesario()
        }
        if (!renovado) {
            return null
        }
        val token = authRepository.tokenAcceso() ?: return null
        return response.request.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()
    }
}