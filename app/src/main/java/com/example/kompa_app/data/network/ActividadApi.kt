package com.example.kompa_app.data.network

import retrofit2.http.GET
import retrofit2.http.Query

interface ActividadApi {

    @GET("actividades")
    suspend fun obtenerActividades(
        @Query("limite") limite: Int = 100
    ): List<ActividadDto>
}