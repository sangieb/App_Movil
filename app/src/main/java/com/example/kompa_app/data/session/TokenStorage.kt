package com.example.kompa_app.data.session

interface TokenStorage {
    fun guardar(usuarioId: String, accessToken: String, refreshToken: String, expiraEn: Long)
    fun actualizarTokens(accessToken: String, refreshToken: String, expiraEn: Long)
    fun accessToken(): String?
    fun refreshToken(): String?
    fun expiraEn(): Long?
    fun usuarioId(): String?
    fun sesionActiva(): Boolean
    fun limpiar()
}