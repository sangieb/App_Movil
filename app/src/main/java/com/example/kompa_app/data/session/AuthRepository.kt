package com.example.kompa_app.data.session

import com.example.kompa_app.data.cuenta.CuentaStorage
import java.util.UUID
import java.util.concurrent.TimeUnit

enum class ResultadoLogin {
    EXITO,
    CUENTA_INEXISTENTE,
    CONTRASENA_INCORRECTA
}

class AuthRepository(
    private val storage: TokenStorage,
    private val cuentas: CuentaStorage,
    private val reloj: () -> Long = System::currentTimeMillis
) {

    fun login(correo: String, password: String): ResultadoLogin {
        if (correo.isBlank() || password.isBlank() || password.length < MIN_PASSWORD_LENGTH) {
            return ResultadoLogin.CONTRASENA_INCORRECTA
        }
        if (!cuentas.existe(correo)) {
            return ResultadoLogin.CUENTA_INEXISTENTE
        }
        if (!cuentas.verificar(correo, password)) {
            return ResultadoLogin.CONTRASENA_INCORRECTA
        }
        crearSesion(correo)
        return ResultadoLogin.EXITO
    }

    fun crearSesion(correo: String) {
        val ahora = reloj()
        storage.guardar(
            usuarioId = UUID.randomUUID().toString(),
            accessToken = tokenSimulado(ACCESS_TOKEN_PREFIJO),
            refreshToken = tokenSimulado(REFRESH_TOKEN_PREFIJO),
            expiraEn = ahora + ACCESS_DURACION_MS
        )
    }

    fun tokenAcceso(): String? = storage.accessToken()

    fun usuarioId(): String? = storage.usuarioId()

    fun sesionActiva(): Boolean = storage.sesionActiva()

    @Synchronized
    fun renovarSiEsNecesario(): Boolean {
        val expira = storage.expiraEn() ?: return false
        if (reloj() < expira) {
            return true
        }
        return refrescar()
    }

    @Synchronized
    fun refrescar(): Boolean {
        if (storage.refreshToken() == null) {
            return false
        }
        storage.actualizarTokens(
            accessToken = tokenSimulado(ACCESS_TOKEN_PREFIJO),
            refreshToken = tokenSimulado(REFRESH_TOKEN_PREFIJO),
            expiraEn = reloj() + ACCESS_DURACION_MS
        )
        return true
    }

    fun logout() {
        storage.limpiar()
    }

    private fun tokenSimulado(prefijo: String): String =
        "$prefijo${UUID.randomUUID()}"

    private companion object {
        const val MIN_PASSWORD_LENGTH = 6
        const val ACCESS_TOKEN_PREFIJO = "mock_access_"
        const val REFRESH_TOKEN_PREFIJO = "mock_refresh_"
        val ACCESS_DURACION_MS: Long = TimeUnit.MINUTES.toMillis(15)
    }
}