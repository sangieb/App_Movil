package com.example.kompa_app.core.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.graphics.scale
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.UUID

object FotoUtil {

    private const val TAG = "Kompa_FotoUtil"
    private const val MAX_LADO = 800
    private const val CALIDAD_JPEG = 80

    fun guardarFoto(context: Context, uri: Uri): String? {
        return try {
            val (ancho, alto) = leerDimensiones(context, uri)
            val sample = calcularSample(ancho, alto)
            val opciones = BitmapFactory.Options().apply { inSampleSize = sample }
            val original = context.contentResolver.openInputStream(uri)?.use {
                BitmapFactory.decodeStream(it, null, opciones)
            } ?: return null

            val escalada = escalar(original)
            val carpeta = File(context.filesDir, "actividades").apply { mkdirs() }
            val archivo = File(carpeta, "${UUID.randomUUID()}.jpg")
            FileOutputStream(archivo).use { salida ->
                escalada.compress(Bitmap.CompressFormat.JPEG, CALIDAD_JPEG, salida)
            }
            if (escalada !== original) {
                original.recycle()
            }
            archivo.absolutePath
        } catch (e: IOException) {
            Log.w(TAG, "No se pudo guardar la foto", e)
            null
        }
    }

    private fun leerDimensiones(context: Context, uri: Uri): Pair<Int, Int> {
        return context.contentResolver.openInputStream(uri)?.use { entrada ->
            val opciones = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeStream(entrada, null, opciones)
            opciones.outWidth to opciones.outHeight
        } ?: (0 to 0)
    }

    private fun calcularSample(ancho: Int, alto: Int): Int {
        var sample = 1
        val mayor = maxOf(ancho, alto)
        while (mayor / (sample * 2) >= MAX_LADO) {
            sample *= 2
        }
        return sample
    }

    private fun escalar(bitmap: Bitmap): Bitmap {
        val mayor = maxOf(bitmap.width, bitmap.height)
        if (mayor <= MAX_LADO) {
            return bitmap
        }
        val escala = MAX_LADO.toFloat() / mayor
        val nuevoAncho = (bitmap.width * escala).toInt()
        val nuevoAlto = (bitmap.height * escala).toInt()
        return bitmap.scale(nuevoAncho, nuevoAlto, true)
    }
}