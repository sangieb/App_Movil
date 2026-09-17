package com.example.kompa_app.data.publicacion

import android.util.Log
import com.example.kompa_app.data.db.KompaDbHelper
import com.example.kompa_app.data.network.ApiClient
import kotlinx.coroutines.CancellationException

interface PublicacionSource {
    fun publicacionesLocales(): List<Publicacion>

    suspend fun refrescar(): List<Publicacion>
}

class PublicacionRepository(
    private val dbHelper: KompaDbHelper,
    private val apiClient: ApiClient
) : PublicacionSource {

    override fun publicacionesLocales(): List<Publicacion> =
        dbHelper.consultarPublicaciones()

    override suspend fun refrescar(): List<Publicacion> {
        val dto = try {
            apiClient.publicacionApi.obtenerPublicaciones()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Refresco de publicaciones fallido; se conserva el caché local", e)
            return publicacionesLocales()
        }
        dbHelper.sincronizarPublicaciones(dto.aPublicaciones())
        return dbHelper.consultarPublicaciones()
    }

    companion object {
        private const val TAG = "Kompa_PublicacionRepo"
    }
}