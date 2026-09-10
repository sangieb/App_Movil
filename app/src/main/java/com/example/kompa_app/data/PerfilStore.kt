package com.example.kompa_app.data

import android.content.Context
import androidx.core.content.edit
import com.example.kompa_app.Constantes

class PerfilStore(context: Context) : PerfilStorage {

    private val prefs = context.getSharedPreferences(Constantes.PREF_PERFIL, Context.MODE_PRIVATE)

    override fun guardar(perfil: Perfil) {
        prefs.edit {
            putString(Constantes.KEY_NOMBRE, perfil.nombre)
            putString(Constantes.KEY_CORREO, perfil.correo)
            putString(Constantes.KEY_FECHA_NACIMIENTO, perfil.fechaNacimiento)
            putString(Constantes.KEY_NACIONALIDAD, perfil.nacionalidad)
            putString(Constantes.KEY_GENERO, perfil.genero)
            putString(Constantes.KEY_CONECTAR, perfil.conectar)
            putString(Constantes.KEY_IDIOMAS, perfil.idiomas)
            putString(Constantes.KEY_INTERESES, perfil.intereses)
            perfil.fotoUri?.let { putString(Constantes.KEY_FOTO_URI, it) }
        }
    }

    override fun perfilActual(): Perfil? {
        if (!prefs.contains(Constantes.KEY_NOMBRE)) {
            return null
        }
        return Perfil(
            nombre = prefs.getString(Constantes.KEY_NOMBRE, "").orEmpty(),
            correo = prefs.getString(Constantes.KEY_CORREO, "").orEmpty(),
            fechaNacimiento = prefs.getString(Constantes.KEY_FECHA_NACIMIENTO, "").orEmpty(),
            nacionalidad = prefs.getString(Constantes.KEY_NACIONALIDAD, "").orEmpty(),
            genero = prefs.getString(Constantes.KEY_GENERO, "").orEmpty(),
            conectar = prefs.getString(Constantes.KEY_CONECTAR, "").orEmpty(),
            idiomas = prefs.getString(Constantes.KEY_IDIOMAS, "").orEmpty(),
            intereses = prefs.getString(Constantes.KEY_INTERESES, "").orEmpty(),
            fotoUri = prefs.getString(Constantes.KEY_FOTO_URI, null)
        )
    }

    override fun nacionalidad(): String? =
        prefs.getString(Constantes.KEY_NACIONALIDAD, null)
}