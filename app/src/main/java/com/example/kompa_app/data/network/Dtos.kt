package com.example.kompa_app.data.network

data class NominatimResult(
    val display_name: String? = null,
    val lat: String? = null,
    val lon: String? = null
)

data class PublicacionDto(
    val id: String? = null,
    val titulo: String? = null,
    val cuerpo: String? = null,
    val autor: String? = null,
    val fechaCreacionLong: Long? = null,
    val lat: Double? = null,
    val lon: Double? = null
)