package com.example.kompa_app.data.perfil

class PerfilRepository(
    private val storage: PerfilStorage
) {

    fun guardar(perfil: Perfil) {
        storage.guardar(perfil)
    }

    fun perfilActual(): Perfil? = storage.perfilActual()

    fun nacionalidad(): String? = storage.nacionalidad()
}