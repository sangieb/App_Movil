package com.example.kompa_app.ui.lista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kompa_app.data.Actividad
import com.example.kompa_app.data.ActividadRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

data class FiltrosCatalogo(
    val texto: String = "",
    val origen: String? = null,
    val duracionMinima: Int? = null,
    val duracionMaxima: Int? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class CatalogViewModel(
    private val repositorio: ActividadRepository
) : ViewModel() {

    private val _filtros = MutableStateFlow(FiltrosCatalogo())

    val resultados: StateFlow<List<Actividad>> = _filtros
        .flatMapLatest { filtros ->
            flow {
                emit(withContext(Dispatchers.IO) {
                    repositorio.buscarCatalogo(
                        texto = filtros.texto,
                        origen = filtros.origen,
                        duracionMinima = filtros.duracionMinima,
                        duracionMaxima = filtros.duracionMaxima
                    )
                })
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun aplicarFiltros(filtros: FiltrosCatalogo) {
        _filtros.value = filtros
    }
}

class CatalogViewModelFactory(
    private val repositorio: ActividadRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CatalogViewModel::class.java)) {
            return CatalogViewModel(repositorio) as T
        }
        throw IllegalArgumentException("ViewModel no soportado: ${modelClass.name}")
    }
}