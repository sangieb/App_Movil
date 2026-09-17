package com.example.kompa_app.data.auth.session

import android.content.Context
import androidx.core.content.edit

class TokenStore(context: Context) : TokenStorage {

    private val prefs = context.getSharedPreferences(SesionPrefs.PREF_SESION, Context.MODE_PRIVATE)

    override fun guardar(usuarioId: String, accessToken: String, refreshToken: String, expiraEn: Long) {
        prefs.edit {
            putString(SesionPrefs.KEY_USUARIO_ID, usuarioId)
            putString(SesionPrefs.KEY_ACCESS_TOKEN, accessToken)
            putString(SesionPrefs.KEY_REFRESH_TOKEN, refreshToken)
            putLong(SesionPrefs.KEY_EXPIRA_EN, expiraEn)
        }
    }

    override fun actualizarTokens(accessToken: String, refreshToken: String, expiraEn: Long) {
        prefs.edit {
            putString(SesionPrefs.KEY_ACCESS_TOKEN, accessToken)
            putString(SesionPrefs.KEY_REFRESH_TOKEN, refreshToken)
            putLong(SesionPrefs.KEY_EXPIRA_EN, expiraEn)
        }
    }

    override fun accessToken(): String? =
        prefs.getString(SesionPrefs.KEY_ACCESS_TOKEN, null)

    override fun refreshToken(): String? =
        prefs.getString(SesionPrefs.KEY_REFRESH_TOKEN, null)

    override fun expiraEn(): Long? =
        prefs.getLong(SesionPrefs.KEY_EXPIRA_EN, -1L).takeIf { it >= 0L }

    override fun usuarioId(): String? =
        prefs.getString(SesionPrefs.KEY_USUARIO_ID, null)

    override fun sesionActiva(): Boolean =
        prefs.contains(SesionPrefs.KEY_ACCESS_TOKEN)

    override fun limpiar() {
        prefs.edit {
            remove(SesionPrefs.KEY_USUARIO_ID)
            remove(SesionPrefs.KEY_ACCESS_TOKEN)
            remove(SesionPrefs.KEY_REFRESH_TOKEN)
            remove(SesionPrefs.KEY_EXPIRA_EN)
        }
    }
}