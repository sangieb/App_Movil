package com.example.kompa_app.data

import android.util.Log
import com.example.kompa_app.Constantes
import com.example.kompa_app.data.db.KompaDbHelper
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
        nacionalidades = valoresDe(Constantes.TIPO_NACIONALIDAD),
        idiomas = valoresDe(Constantes.TIPO_IDIOMA),
        intereses = valoresDe(Constantes.TIPO_INTERES)
    )
}

interface CatalogoSource {
    fun catalogosLocales(): CatalogosRegistro

    suspend fun refrescar(): CatalogosRegistro
}

class CatalogoRepository(
    private val dbHelper: KompaDbHelper,
    private val apiClient: ApiClient
) : CatalogoSource {

    override fun catalogosLocales(): CatalogosRegistro =
        CatalogosRegistro(
            nacionalidades = dbHelper.consultarCatalogos(Constantes.TIPO_NACIONALIDAD),
            idiomas = dbHelper.consultarCatalogos(Constantes.TIPO_IDIOMA),
            intereses = dbHelper.consultarCatalogos(Constantes.TIPO_INTERES)
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
        dbHelper.reemplazarCatalogos(catalogo)
        return catalogosLocales()
    }

    companion object {
        private const val TAG = "Kompa_CatalogoRepo"
    }
}