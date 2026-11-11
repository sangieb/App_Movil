package com.example.kompa_app.data.auth.cuenta

import com.example.kompa_app.core.util.PasswordHash
import com.example.kompa_app.data.db.CuentaDao
import com.example.kompa_app.data.db.aDominio
import com.example.kompa_app.data.db.aEntidad

class CuentaStore(
    private val cuentaDao: CuentaDao
) : CuentaStorage {

    override fun registrar(correo: String, password: String) {
        val datos = PasswordHash.hash(password)
        cuentaDao.insertar(Cuenta(correo = correo, sal = datos.sal, hash = datos.hash).aEntidad())
    }

    override fun existe(correo: String): Boolean =
        cuentaDao.buscar(correo) != null

    override fun verificar(correo: String, password: String): Boolean {
        val cuenta = cuentaDao.buscar(correo)?.aDominio() ?: return false
        return PasswordHash.verificar(password, cuenta.sal, cuenta.hash)
    }
}