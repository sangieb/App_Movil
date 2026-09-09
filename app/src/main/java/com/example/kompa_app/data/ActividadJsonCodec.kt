package com.example.kompa_app.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ActividadJsonCodec {

    private val gson = Gson()
    private val tipo = object : TypeToken<List<Actividad>>() {}.type

    fun aJson(lista: List<Actividad>): String = gson.toJson(lista)

    fun desdeJson(json: String): List<Actividad> {
        return try {
            gson.fromJson<List<Actividad>>(json, tipo) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}