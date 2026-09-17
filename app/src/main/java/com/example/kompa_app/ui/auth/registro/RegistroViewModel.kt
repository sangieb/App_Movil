package com.example.kompa_app.ui.auth.registro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kompa_app.data.CatalogosRegistro
import com.example.kompa_app.data.CatalogoRepository
import com.example.kompa_app.data.CatalogoSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface RegistroUiState {
    data object Cargando : RegistroUiState
    data class Listo(val catalogos: CatalogosRegistro) : RegistroUiState
    data object Error : RegistroUiState
}

class RegistroViewModel(
    private val repositorio: CatalogoSource,
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _estado = MutableStateFlow<RegistroUiState>(RegistroUiState.Cargando)
    val estado: StateFlow<RegistroUiState> = _estado

    init {
        cargarCatalogos()
    }

    fun cargarCatalogos() {
        viewModelScope.launch {
            val locales = withContext(dispatcherIO) { repositorio.catalogosLocales() }
            _estado.value = RegistroUiState.Listo(locales)
            try {
                val actualizados = withContext(dispatcherIO) { repositorio.refrescar() }
                _estado.value = RegistroUiState.Listo(actualizados)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _estado.value = RegistroUiState.Error
            }
        }
    }
}

class RegistroViewModelFactory(
    private val repositorio: CatalogoRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegistroViewModel::class.java)) {
            return RegistroViewModel(repositorio) as T
        }
        throw IllegalArgumentException("ViewModel no soportado: ${modelClass.name}")
    }
}