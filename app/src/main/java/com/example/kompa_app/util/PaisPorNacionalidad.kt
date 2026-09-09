package com.example.kompa_app.util

object PaisPorNacionalidad {

    fun codigoPais(nacionalidad: String?): String? {
        val normalizada = nacionalidad?.trim()?.lowercase() ?: return null
        return when {
            normalizada.contains("mexicana") -> "mx"
            normalizada.contains("colombiana") -> "co"
            normalizada.contains("argentina") -> "ar"
            normalizada.contains("chilena") -> "cl"
            normalizada.contains("peruana") -> "pe"
            normalizada.contains("espa") -> "es"
            normalizada.contains("estadounidense") -> "us"
            normalizada.contains("brasile") -> "br"
            normalizada.contains("ecuatoriana") -> "ec"
            else -> null
        }
    }
}