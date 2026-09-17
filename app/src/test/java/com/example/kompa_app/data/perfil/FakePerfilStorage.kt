package com.example.kompa_app.data.perfil

class FakePerfilStorage : PerfilStorage {

    private var perfil: Perfil? = null

    override fun guardar(perfil: Perfil) {
        this.perfil = perfil
    }

    override fun perfilActual(): Perfil? = perfil

    override fun nacionalidad(): String? = perfil?.nacionalidad
}