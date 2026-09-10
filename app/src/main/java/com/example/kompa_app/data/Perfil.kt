package com.example.kompa_app.data

import android.os.Bundle
import com.example.kompa_app.Constantes

data class Perfil(
    val nombre: String = "",
    val correo: String = "",
    val fechaNacimiento: String = "",
    val nacionalidad: String = "",
    val genero: String = "",
    val conectar: String = "",
    val idiomas: String = "",
    val intereses: String = "",
    val fotoUri: String? = null
) {
    companion object {
        fun deBundle(bundle: Bundle): Perfil = Perfil(
            nombre = bundle.getString(Constantes.EXTRA_NOMBRE).orEmpty(),
            correo = bundle.getString(Constantes.EXTRA_CORREO).orEmpty(),
            fechaNacimiento = bundle.getString(Constantes.EXTRA_FECHA_NACIMIENTO).orEmpty(),
            nacionalidad = bundle.getString(Constantes.EXTRA_NACIONALIDAD).orEmpty(),
            genero = bundle.getString(Constantes.EXTRA_GENERO).orEmpty(),
            conectar = bundle.getString(Constantes.EXTRA_CONECTAR).orEmpty(),
            idiomas = bundle.getString(Constantes.EXTRA_IDIOMAS).orEmpty(),
            intereses = bundle.getString(Constantes.EXTRA_INTERESES).orEmpty(),
            fotoUri = bundle.getString(Constantes.EXTRA_FOTO_URI)
        )
    }
}