package com.example.kompa_app.data.cuenta

class FakeCuentaStorage : CuentaStorage {

    private val contrasenas = HashMap<String, String>()

    override fun registrar(correo: String, password: String) {
        contrasenas[correo.trim().lowercase()] = password
    }

    override fun existe(correo: String): Boolean =
        contrasenas.containsKey(correo.trim().lowercase())

    override fun verificar(correo: String, password: String): Boolean =
        contrasenas[correo.trim().lowercase()] == password
}