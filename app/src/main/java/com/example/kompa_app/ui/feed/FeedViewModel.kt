package com.example.kompa_app.ui.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kompa_app.data.Publicacion
import com.example.kompa_app.data.PublicacionRepository
import kotlinx.coroutines.CancellationException
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
    private val repositorio: PublicacionRepository
) : ViewModel() {

    private val _estado = MutableStateFlow<FeedUiState>(FeedUiState.Cargando)
    val estado: StateFlow<FeedUiState> = _estado

    fun cargar() {
        viewModelScope.launch {
            _estado.value = FeedUiState.Cargando
            _estado.value = try {
                val publicaciones = withContext(Dispatchers.IO) { repositorio.obtener() }
                FeedUiState.Exito(publicaciones)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                FeedUiState.Error
            }
        }
    }
}

class FeedViewModelFactory(
    private val repositorio: PublicacionRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FeedViewModel::class.java)) {
            return FeedViewModel(repositorio) as T
        }
        throw IllegalArgumentException("ViewModel no soportado: ${modelClass.name}")
    }
}