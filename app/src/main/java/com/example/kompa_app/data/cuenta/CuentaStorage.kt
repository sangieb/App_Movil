package com.example.kompa_app.data.cuenta

interface CuentaStorage {
    fun registrar(correo: String, password: String)
    fun existe(correo: String): Boolean
    fun verificar(correo: String, password: String): Boolean
}