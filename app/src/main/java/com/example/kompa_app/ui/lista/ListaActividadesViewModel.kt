package com.example.kompa_app.ui.lista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kompa_app.data.actividad.Actividad
import com.example.kompa_app.data.actividad.ActividadSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class FiltrosCatalogo(
    val texto: String = "",
    val origen: String? = null,
    val duracionMinima: Int? = null,
    val duracionMaxima: Int? = null
)

@OptIn(ExperimentalCoroutinesApi::class)
class ListaActividadesViewModel(
    private val repositorio: ActividadSource,
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _filtros = MutableStateFlow(FiltrosCatalogo())
    private val _recarga = MutableStateFlow(0)

    val resultados: StateFlow<List<Actividad>> = combine(_filtros, _recarga) { filtros, _ -> filtros }
        .flatMapLatest { filtros ->
            flow {
                emit(withContext(dispatcherIO) {
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

    fun sincronizar() {
        viewModelScope.launch {
            withContext(dispatcherIO) { repositorio.sincronizarDesdeApi() }
            _recarga.value = _recarga.value + 1
        }
    }
}

class ListaActividadesViewModelFactory(
    private val repositorio: ActividadSource
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListaActividadesViewModel::class.java)) {
            return ListaActividadesViewModel(repositorio) as T
        }
        throw IllegalArgumentException("ViewModel no soportado: ${modelClass.name}")
    }
}