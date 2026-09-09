package com.example.kompa_app.data

import android.content.Context
import androidx.core.content.edit
import com.example.kompa_app.Constantes

class PerfilStore(context: Context) {

    private val prefs = context.getSharedPreferences(Constantes.PREF_PERFIL, Context.MODE_PRIVATE)

    fun guardarNacionalidad(nacionalidad: String) {
        prefs.edit {
            putString(Constantes.KEY_NACIONALIDAD, nacionalidad)
        }
    }

    fun nacionalidad(): String? =
        prefs.getString(Constantes.KEY_NACIONALIDAD, null)
}