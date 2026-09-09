package com.example.kompa_app.data.network

import retrofit2.http.GET
import retrofit2.http.Query

interface NominatimApi {

    @GET("reverse")
    suspend fun reverse(
        @Query("format") formato: String = "jsonv2",
        @Query("lat") latitud: Double,
        @Query("lon") longitud: Double,
        @Query("accept-language") idioma: String = "es",
        @Query("zoom") zoom: Int = 16
    ): NominatimResult

    @GET("search")
    suspend fun buscar(
        @Query("q") direccion: String,
        @Query("format") formato: String = "jsonv2",
        @Query("accept-language") idioma: String = "es",
        @Query("limit") limite: Int = 1,
        @Query("viewbox") viewbox: String? = null,
        @Query("bounded") acotado: Int? = null,
        @Query("countrycodes") paises: String? = null
    ): List<NominatimResult>
}