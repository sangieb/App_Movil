package com.example.kompa_app.data.session

import java.util.HashMap

class FakeTokenStorage : TokenStorage {

    private val datos = HashMap<String, Any?>()

    override fun guardar(usuarioId: String, accessToken: String, refreshToken: String, expiraEn: Long) {
        datos["usuarioId"] = usuarioId
        datos["accessToken"] = accessToken
        datos["refreshToken"] = refreshToken
        datos["expiraEn"] = expiraEn
    }

    override fun actualizarTokens(accessToken: String, refreshToken: String, expiraEn: Long) {
        datos["accessToken"] = accessToken
        datos["refreshToken"] = refreshToken
        datos["expiraEn"] = expiraEn
    }

    override fun accessToken(): String? = datos["accessToken"] as String?

    override fun refreshToken(): String? = datos["refreshToken"] as String?

    override fun expiraEn(): Long? = datos["expiraEn"] as Long?

    override fun usuarioId(): String? = datos["usuarioId"] as String?

    override fun sesionActiva(): Boolean = datos.containsKey("accessToken")

    override fun limpiar() {
        datos.clear()
    }
}