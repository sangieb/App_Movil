package com.example.kompa_app.core.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FechaFormatos {

    private const val PATRON_FECHA = "dd/MM/yyyy"
    private const val PATRON_FECHA_HORA = "dd/MM/yyyy HH:mm"
    private val LOCALE = Locale.forLanguageTag("es")

    fun fecha(epochMillis: Long): String =
        SimpleDateFormat(PATRON_FECHA, LOCALE).format(Date(epochMillis))

    fun fechaHora(epochMillis: Long): String =
        SimpleDateFormat(PATRON_FECHA_HORA, LOCALE).format(Date(epochMillis))

    fun parsearFecha(texto: String): Date? =
        SimpleDateFormat(PATRON_FECHA, LOCALE).parse(texto)
}