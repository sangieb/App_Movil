package com.example.kompa_app.data.perfil

import android.content.Context
import androidx.core.content.edit

class PerfilStore(context: Context) : PerfilStorage {

    private val prefs = context.getSharedPreferences(PerfilPrefs.PREF_PERFIL, Context.MODE_PRIVATE)

    override fun guardar(perfil: Perfil) {
        prefs.edit {
            putString(PerfilPrefs.KEY_NOMBRE, perfil.nombre)
            putString(PerfilPrefs.KEY_CORREO, perfil.correo)
            putString(PerfilPrefs.KEY_FECHA_NACIMIENTO, perfil.fechaNacimiento)
            putString(PerfilPrefs.KEY_NACIONALIDAD, perfil.nacionalidad)
            putString(PerfilPrefs.KEY_GENERO, perfil.genero)
            putString(PerfilPrefs.KEY_CONECTAR, perfil.conectar)
            putString(PerfilPrefs.KEY_IDIOMAS, perfil.idiomas)
            putString(PerfilPrefs.KEY_INTERESES, perfil.intereses)
            perfil.fotoUri?.let {
            putString(PerfilPrefs.KEY_FOTO_URI, it)
        } ?: remove(PerfilPrefs.KEY_FOTO_URI)
        }
    }

    override fun perfilActual(): Perfil? {
        if (!prefs.contains(PerfilPrefs.KEY_NOMBRE)) {
            return null
        }
        return Perfil(
            nombre = prefs.getString(PerfilPrefs.KEY_NOMBRE, "").orEmpty(),
            correo = prefs.getString(PerfilPrefs.KEY_CORREO, "").orEmpty(),
            fechaNacimiento = prefs.getString(PerfilPrefs.KEY_FECHA_NACIMIENTO, "").orEmpty(),
            nacionalidad = prefs.getString(PerfilPrefs.KEY_NACIONALIDAD, "").orEmpty(),
            genero = prefs.getString(PerfilPrefs.KEY_GENERO, "").orEmpty(),
            conectar = prefs.getString(PerfilPrefs.KEY_CONECTAR, "").orEmpty(),
            idiomas = prefs.getString(PerfilPrefs.KEY_IDIOMAS, "").orEmpty(),
            intereses = prefs.getString(PerfilPrefs.KEY_INTERESES, "").orEmpty(),
            fotoUri = prefs.getString(PerfilPrefs.KEY_FOTO_URI, null)
        )
    }

    override fun nacionalidad(): String? =
        prefs.getString(PerfilPrefs.KEY_NACIONALIDAD, null)
}