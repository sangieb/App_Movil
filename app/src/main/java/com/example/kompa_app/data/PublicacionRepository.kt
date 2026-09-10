package com.example.kompa_app.data

import com.example.kompa_app.data.network.ApiClient
import com.example.kompa_app.data.network.PublicacionDto
import java.util.UUID

class PublicacionRepository(
    private val apiClient: ApiClient
) {

    suspend fun obtener(): List<Publicacion> =
        apiClient.publicacionApi.obtenerPublicaciones()
            .map { it.aDominio() }
            .distinctBy { it.id }

    private fun PublicacionDto.aDominio(): Publicacion =
        Publicacion(
            id = id ?: UUID.randomUUID().toString(),
            titulo = titulo.orEmpty(),
            cuerpo = cuerpo.orEmpty(),
            autor = autor.orEmpty(),
            fechaCreacionLong = fechaCreacionLong,
            lat = lat,
            lon = lon
        )
}