package com.example.kompa_app.data

interface PerfilStorage {
    fun guardar(perfil: Perfil)
    fun perfilActual(): Perfil?
    fun nacionalidad(): String?
}