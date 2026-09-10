package com.example.kompa_app.data.cuenta

import com.example.kompa_app.core.util.PasswordHash
import com.example.kompa_app.data.db.KompaDbHelper

class CuentaStore(
    private val dbHelper: KompaDbHelper
) : CuentaStorage {

    override fun registrar(correo: String, password: String) {
        val datos = PasswordHash.hash(password)
        dbHelper.insertarCuenta(Cuenta(correo = correo, sal = datos.sal, hash = datos.hash))
    }

    override fun existe(correo: String): Boolean =
        dbHelper.consultarCuenta(correo) != null

    override fun verificar(correo: String, password: String): Boolean {
        val cuenta = dbHelper.consultarCuenta(correo) ?: return false
        return PasswordHash.verificar(password, cuenta.sal, cuenta.hash)
    }
}