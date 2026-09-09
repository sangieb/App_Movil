package com.example.kompa_app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.kompa_app.data.Actividad
import com.example.kompa_app.data.ActividadRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val repositorio: ActividadRepository
) : ViewModel() {

    private val _actividades = MutableStateFlow<List<Actividad>>(emptyList())
    val actividades: StateFlow<List<Actividad>> = _actividades

    fun cargar() {
        viewModelScope.launch {
            val locales = withContext(Dispatchers.IO) { repositorio.obtenerLocales() }
            _actividades.value = locales
        }
    }
}

class HomeViewModelFactory(
    private val repositorio: ActividadRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(repositorio) as T
        }
        throw IllegalArgumentException("ViewModel no soportado: ${modelClass.name}")
    }
}