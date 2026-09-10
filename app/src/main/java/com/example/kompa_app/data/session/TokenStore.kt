package com.example.kompa_app.data.session

import android.content.Context
import androidx.core.content.edit
import com.example.kompa_app.Constantes

class TokenStore(context: Context) : TokenStorage {

    private val prefs = context.getSharedPreferences(Constantes.PREF_SESION, Context.MODE_PRIVATE)

    override fun guardar(usuarioId: String, accessToken: String, refreshToken: String, expiraEn: Long) {
        prefs.edit {
            putString(Constantes.KEY_USUARIO_ID, usuarioId)
            putString(Constantes.KEY_ACCESS_TOKEN, accessToken)
            putString(Constantes.KEY_REFRESH_TOKEN, refreshToken)
            putLong(Constantes.KEY_EXPIRA_EN, expiraEn)
        }
    }

    override fun actualizarTokens(accessToken: String, refreshToken: String, expiraEn: Long) {
        prefs.edit {
            putString(Constantes.KEY_ACCESS_TOKEN, accessToken)
            putString(Constantes.KEY_REFRESH_TOKEN, refreshToken)
            putLong(Constantes.KEY_EXPIRA_EN, expiraEn)
        }
    }

    override fun accessToken(): String? =
        prefs.getString(Constantes.KEY_ACCESS_TOKEN, null)

    override fun refreshToken(): String? =
        prefs.getString(Constantes.KEY_REFRESH_TOKEN, null)

    override fun expiraEn(): Long? =
        prefs.getLong(Constantes.KEY_EXPIRA_EN, -1L).takeIf { it >= 0L }

    override fun usuarioId(): String? =
        prefs.getString(Constantes.KEY_USUARIO_ID, null)

    override fun sesionActiva(): Boolean =
        prefs.contains(Constantes.KEY_ACCESS_TOKEN)

    override fun limpiar() {
        prefs.edit {
            remove(Constantes.KEY_USUARIO_ID)
            remove(Constantes.KEY_ACCESS_TOKEN)
            remove(Constantes.KEY_REFRESH_TOKEN)
            remove(Constantes.KEY_EXPIRA_EN)
        }
    }
}