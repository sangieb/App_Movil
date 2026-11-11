package com.example.kompa_app.data.catalogo

import android.util.Log
import com.example.kompa_app.data.db.CatalogoDao
import com.example.kompa_app.data.db.aEntidadOrNull
import com.example.kompa_app.data.network.ApiClient
import com.example.kompa_app.data.network.CatalogoDto
import kotlinx.coroutines.CancellationException

data class CatalogosRegistro(
    val nacionalidades: List<String>,
    val idiomas: List<String>,
    val intereses: List<String>
)

fun List<CatalogoDto>.agruparPorTipo(): CatalogosRegistro {
    fun valoresDe(tipo: String): List<String> =
        asSequence()
            .filter { it.tipo == tipo }
            .sortedBy { it.orden ?: Int.MAX_VALUE }
            .mapNotNull { it.valor?.trim() }
            .filter { it.isNotEmpty() }
            .toList()

    return CatalogosRegistro(
        nacionalidades = valoresDe(CatalogoTipos.NACIONALIDAD),
        idiomas = valoresDe(CatalogoTipos.IDIOMA),
        intereses = valoresDe(CatalogoTipos.INTERES)
    )
}

interface CatalogoSource {
    fun catalogosLocales(): CatalogosRegistro

    suspend fun refrescar(): CatalogosRegistro
}

class CatalogoRepository(
    private val catalogoDao: CatalogoDao,
    private val apiClient: ApiClient
) : CatalogoSource {

    override fun catalogosLocales(): CatalogosRegistro =
        CatalogosRegistro(
            nacionalidades = catalogoDao.valoresDe(CatalogoTipos.NACIONALIDAD),
            idiomas = catalogoDao.valoresDe(CatalogoTipos.IDIOMA),
            intereses = catalogoDao.valoresDe(CatalogoTipos.INTERES)
        )

    override suspend fun refrescar(): CatalogosRegistro {
        val catalogo = try {
            apiClient.catalogoApi.obtenerCatalogos()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Refresco de catálogos fallido; se conserva el caché local", e)
            return catalogosLocales()
        }
        catalogoDao.reemplazar(catalogo.mapNotNull { it.aEntidadOrNull() })
        return catalogosLocales()
    }

    companion object {
        private const val TAG = "Kompa_CatalogoRepo"
    }
}