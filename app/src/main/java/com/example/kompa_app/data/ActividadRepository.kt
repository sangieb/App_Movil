package com.example.kompa_app.data

import android.util.Log
import com.example.kompa_app.data.db.KompaDbHelper
import com.example.kompa_app.data.network.ApiClient
import com.example.kompa_app.data.network.NominatimResult
import kotlinx.coroutines.CancellationException

class ActividadRepository(
    private val dbHelper: KompaDbHelper,
    private val apiClient: ApiClient
) {

    fun obtenerLocales(): List<Actividad> =
        dbHelper.consultarActividades()

    fun buscarCatalogo(
        texto: String = "",
        origen: String? = null,
        duracionMinima: Int? = null,
        duracionMaxima: Int? = null
    ): List<Actividad> =
        dbHelper.consultarActividades(
            texto = texto,
            origen = origen,
            duracionMinima = duracionMinima,
            duracionMaxima = duracionMaxima
        )

    fun guardarNueva(actividad: Actividad) {
        dbHelper.insertarActividad(actividad)
    }

    suspend fun nombreUbicacion(lat: Double, lon: Double): String {
        return try {
            apiClient.nominatimApi.reverse(latitud = lat, longitud = lon).display_name.orEmpty()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Reverse geocoding fallido ($lat, $lon)", e)
            ""
        }
    }

    suspend fun buscarDireccion(
        direccion: String,
        viewbox: String? = null,
        paises: String? = null
    ): NominatimResult? {
        return try {
            apiClient.nominatimApi.buscar(
                direccion = direccion,
                viewbox = viewbox,
                acotado = if (viewbox != null) 1 else null,
                paises = paises
            ).firstOrNull()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Búsqueda de dirección fallida: $direccion", e)
            null
        }
    }

    companion object {
        private const val TAG = "Kompa_ActividadRepo"
    }
}