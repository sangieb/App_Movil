package com.example.kompa_app.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kompa_app.data.publicacion.Publicacion
import com.example.kompa_app.data.publicacion.PublicacionSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface FeedUiState {
    data object Cargando : FeedUiState
    data class Exito(val publicaciones: List<Publicacion>) : FeedUiState
    data object Error : FeedUiState
}

class FeedViewModel(
    private val repositorio: PublicacionSource,
    private val dispatcherIO: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _estado = MutableStateFlow<FeedUiState>(FeedUiState.Cargando)
    val estado: StateFlow<FeedUiState> = _estado

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            val locales = withContext(dispatcherIO) { repositorio.publicacionesLocales() }
            _estado.value = FeedUiState.Exito(locales)
            try {
                val actualizadas = withContext(dispatcherIO) { repositorio.refrescar() }
                _estado.value = FeedUiState.Exito(actualizadas)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _estado.value = FeedUiState.Error
            }
        }
    }
}

class FeedViewModelFactory(
    private val repositorio: PublicacionSource
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FeedViewModel::class.java)) {
            return FeedViewModel(repositorio) as T
        }
        throw IllegalArgumentException("ViewModel no soportado: ${modelClass.name}")
    }
}