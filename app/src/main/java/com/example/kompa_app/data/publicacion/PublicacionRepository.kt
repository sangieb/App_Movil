package com.example.kompa_app.data.publicacion

import android.util.Log
import com.example.kompa_app.data.db.PublicacionDao
import com.example.kompa_app.data.db.aDominio
import com.example.kompa_app.data.db.aEntidad
import com.example.kompa_app.data.network.ApiClient
import kotlinx.coroutines.CancellationException

interface PublicacionSource {
    fun publicacionesLocales(): List<Publicacion>

    suspend fun refrescar(): List<Publicacion>
}

class PublicacionRepository(
    private val publicacionDao: PublicacionDao,
    private val apiClient: ApiClient
) : PublicacionSource {

    override fun publicacionesLocales(): List<Publicacion> =
        publicacionDao.todas().map { it.aDominio() }

    override suspend fun refrescar(): List<Publicacion> {
        val dto = try {
            apiClient.publicacionApi.obtenerPublicaciones()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Refresco de publicaciones fallido; se conserva el caché local", e)
            return publicacionesLocales()
        }
        publicacionDao.guardarTodas(dto.aPublicaciones().map { it.aEntidad() })
        return publicacionesLocales()
    }

    companion object {
        private const val TAG = "Kompa_PublicacionRepo"
    }
}