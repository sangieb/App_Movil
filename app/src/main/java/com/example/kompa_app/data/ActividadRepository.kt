package com.example.kompa_app.data

import com.example.kompa_app.data.network.ApiClient
import com.example.kompa_app.data.network.NominatimResult
import kotlinx.coroutines.CancellationException

class ActividadRepository(
    private val store: ActividadStore
) {

    fun obtenerLocales(): List<Actividad> =
        store.cargar().sortedByDescending { it.fechaCreacionLong ?: 0L }

    fun guardarNueva(actividad: Actividad) {
        store.guardar(actividad)
    }

    suspend fun nombreUbicacion(lat: Double, lon: Double): String {
        return try {
            ApiClient.nominatimApi.reverse(latitud = lat, longitud = lon).display_name.orEmpty()
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            ""
        }
    }

    suspend fun buscarDireccion(
        direccion: String,
        viewbox: String? = null,
        paises: String? = null
    ): NominatimResult? {
        return try {
            ApiClient.nominatimApi.buscar(
                direccion = direccion,
                viewbox = viewbox,
                acotado = if (viewbox != null) 1 else null,
                paises = paises
            ).firstOrNull()
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            null
        }
    }
}