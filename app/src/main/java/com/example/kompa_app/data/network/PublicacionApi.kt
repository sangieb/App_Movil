package com.example.kompa_app.data.network

import retrofit2.http.GET
import retrofit2.http.Query

interface PublicacionApi {

    @GET("publicaciones")
    suspend fun obtenerPublicaciones(
        @Query("limite") limite: Int = 50
    ): List<PublicacionDto>
}