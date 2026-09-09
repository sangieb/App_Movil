package com.example.kompa_app.data

data class Actividad(
    val id: String,
    val nombre: String,
    val descripcion: String,
    val ubicacion: String,
    val lat: Double,
    val lon: Double,
    val creadoPor: String,
    val duracionMin: Int?,
    val fechaCreacionLong: Long?,
    val fechaActividadLong: Long? = null,
    val fotoRuta: String?,
    val origen: String
)