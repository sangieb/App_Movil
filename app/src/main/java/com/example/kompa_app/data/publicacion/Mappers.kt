package com.example.kompa_app.data.publicacion

import com.example.kompa_app.data.network.PublicacionDto
import java.util.UUID

fun PublicacionDto.aDominio(): Publicacion =
    Publicacion(
        id = id ?: UUID.randomUUID().toString(),
        titulo = titulo.orEmpty(),
        cuerpo = cuerpo.orEmpty(),
        autor = autor.orEmpty(),
        fechaCreacionLong = fechaCreacionLong,
        lat = lat,
        lon = lon
    )

fun List<PublicacionDto>.aPublicaciones(soloConIdEstable: Boolean = true): List<Publicacion> =
    asSequence()
        .filter { !soloConIdEstable || !it.id.isNullOrBlank() }
        .map { it.aDominio() }
        .distinctBy { it.id }
        .toList()