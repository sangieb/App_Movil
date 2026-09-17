package com.example.kompa_app.data.perfil

import android.os.Bundle

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
        const val BUNDLE_DATOS = "bundle_datos_perfil"

        const val EXTRA_NOMBRE = "extra_nombre"
        const val EXTRA_CORREO = "extra_correo"
        const val EXTRA_PASSWORD = "extra_password"
        const val EXTRA_FECHA_NACIMIENTO = "extra_fecha_nacimiento"
        const val EXTRA_NACIONALIDAD = "extra_nacionalidad"
        const val EXTRA_GENERO = "extra_genero"
        const val EXTRA_CONECTAR = "extra_conectar"
        const val EXTRA_IDIOMAS = "extra_idiomas"
        const val EXTRA_INTERESES = "extra_intereses"
        const val EXTRA_FOTO_URI = "extra_foto_uri"

        fun deBundle(bundle: Bundle): Perfil = Perfil(
            nombre = bundle.getString(EXTRA_NOMBRE).orEmpty(),
            correo = bundle.getString(EXTRA_CORREO).orEmpty(),
            fechaNacimiento = bundle.getString(EXTRA_FECHA_NACIMIENTO).orEmpty(),
            nacionalidad = bundle.getString(EXTRA_NACIONALIDAD).orEmpty(),
            genero = bundle.getString(EXTRA_GENERO).orEmpty(),
            conectar = bundle.getString(EXTRA_CONECTAR).orEmpty(),
            idiomas = bundle.getString(EXTRA_IDIOMAS).orEmpty(),
            intereses = bundle.getString(EXTRA_INTERESES).orEmpty(),
            fotoUri = bundle.getString(EXTRA_FOTO_URI)
        )
    }
}