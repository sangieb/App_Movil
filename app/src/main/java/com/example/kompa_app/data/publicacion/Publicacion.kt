package com.example.kompa_app.data.publicacion

data class Publicacion(
    val id: String,
    val titulo: String,
    val cuerpo: String,
    val autor: String,
    val fechaCreacionLong: Long?,
    val lat: Double?,
    val lon: Double?
)