package com.example.kompa_app.data.actividad

import com.example.kompa_app.data.network.ActividadDto
import java.util.UUID

fun ActividadDto.aDominio(): Actividad =
    Actividad(
        id = id ?: UUID.randomUUID().toString(),
        nombre = nombre.orEmpty(),
        descripcion = descripcion.orEmpty(),
        ubicacion = ubicacion.orEmpty(),
        lat = lat ?: 0.0,
        lon = lon ?: 0.0,
        creadoPor = creadoPor.orEmpty(),
        duracionMin = duracionMin,
        fechaCreacionLong = fechaCreacionLong,
        fechaActividadLong = fechaActividadLong,
        fotoRuta = null,
        origen = ActividadOrigenes.API
    )

fun List<ActividadDto>.aActividadesApi(): List<Actividad> =
    asSequence()
        .filter { !it.id.isNullOrBlank() }
        .map { it.aDominio() }
        .distinctBy { it.id }
        .toList()