package com.example.kompa_app

object Constantes {
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

    // Perfil: persistencia local.
    const val PREF_PERFIL = "pref_perfil"
    const val KEY_NACIONALIDAD = "nacionalidad"
    const val KEY_NOMBRE = "nombre"
    const val KEY_CORREO = "correo"
    const val KEY_FECHA_NACIMIENTO = "fecha_nacimiento"
    const val KEY_GENERO = "genero"
    const val KEY_CONECTAR = "conectar"
    const val KEY_IDIOMAS = "idiomas"
    const val KEY_INTERESES = "intereses"
    const val KEY_FOTO_URI = "foto_uri"

    // Sesión: persistencia local segura (MODE_PRIVATE).
    const val PREF_SESION = "pref_sesion"
    const val KEY_USUARIO_ID = "usuario_id"
    const val KEY_ACCESS_TOKEN = "access_token"
    const val KEY_REFRESH_TOKEN = "refresh_token"
    const val KEY_EXPIRA_EN = "expira_en"

    // API propia (endpoint del feed). Ajustar a la URL real del backend.
    const val API_BASE_URL = "https://api.kompa.example/"

    // Actividades: persistencia local.
    const val PREF_ACTIVIDADES = "pref_actividades"
    const val KEY_ACTIVIDADES = "lista_actividades"
    const val PREF_OSMDROID = "pref_osmdroid"
    const val LAT_DEFAULT = 4.7110
    const val LON_DEFAULT = -74.0721
    const val USER_AGENT = "KompaApp/1.0 (demo academica)"
}