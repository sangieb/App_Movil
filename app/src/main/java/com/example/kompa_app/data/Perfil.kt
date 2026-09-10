package com.example.kompa_app.data

data class Perfil(
    val nombre: String = "",
    val correo: String = "",
    val fechaNacimiento: String = "",
    val nacionalidad: String = "",
    val genero: String = "",
    val conectar: String = "",
    val idiomas: String = "",
    val intereses: String = "",
    val fotoUri: String? = null
)