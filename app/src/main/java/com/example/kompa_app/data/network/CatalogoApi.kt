package com.example.kompa_app.data.network

import retrofit2.http.GET

interface CatalogoApi {

    @GET("catalogos")
    suspend fun obtenerCatalogos(): List<CatalogoDto>
}