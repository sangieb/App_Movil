package com.example.kompa_app.data

import android.content.Context
import androidx.core.content.edit
import com.example.kompa_app.Constantes

class ActividadStore(context: Context) {

    private val prefs = context.getSharedPreferences(Constantes.PREF_ACTIVIDADES, Context.MODE_PRIVATE)
    private val codec = ActividadJsonCodec()

    fun cargar(): List<Actividad> {
        if (!prefs.contains(Constantes.KEY_ACTIVIDADES)) {
            val ejemplos = ActividadesEjemplo.lista()
            prefs.edit {
                putString(Constantes.KEY_ACTIVIDADES, codec.aJson(ejemplos))
            }
            return ejemplos
        }
        val json = prefs.getString(Constantes.KEY_ACTIVIDADES, "[]") ?: "[]"
        return codec.desdeJson(json).distinctBy { it.id }
    }

    fun guardar(nueva: Actividad) {
        val lista = cargar() + nueva
        prefs.edit {
            putString(Constantes.KEY_ACTIVIDADES, codec.aJson(lista))
        }
    }
}