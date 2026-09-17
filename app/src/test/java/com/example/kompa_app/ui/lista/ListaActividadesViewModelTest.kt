package com.example.kompa_app.ui.lista

import androidx.lifecycle.viewModelScope
import com.example.kompa_app.data.actividad.Actividad
import com.example.kompa_app.data.actividad.ActividadOrigenes
import com.example.kompa_app.data.actividad.ActividadSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ListaActividadesViewModelTest {

    private val locales = listOf(actividad("a1"))

    private val remotas = listOf(actividad("a1"), actividad("a2"))

    @Test
    fun `sincronizar re-emite resultados con las actividades remotas`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            verificaSecuencia()
        } finally {
            Dispatchers.resetMain()
        }
    }

    private fun TestScope.verificaSecuencia() {
        val repositorio = FakeActividadSource(locales, remotas)
        val viewModel = ListaActividadesViewModel(
            repositorio,
            dispatcherIO = StandardTestDispatcher(testScheduler)
        )

        val resultados = mutableListOf<List<Actividad>>()
        val recopilador = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.resultados.collect { resultados.add(it) }
        }
        advanceUntilIdle()
        assertEquals(locales, resultados.last())

        viewModel.sincronizar()
        advanceUntilIdle()
        recopilador.cancel()
        viewModel.viewModelScope.cancel()
        advanceUntilIdle()

        assertEquals(remotas, resultados.last())
        assertTrue(resultados.toList().contains(locales))
    }

    private fun actividad(id: String) = Actividad(
        id = id,
        nombre = "Actividad $id",
        descripcion = "Descripción",
        ubicacion = "Bogotá",
        lat = 4.6,
        lon = -74.07,
        creadoPor = "Sistema",
        duracionMin = 90,
        fechaCreacionLong = 1735689600000L,
        fotoRuta = null,
        origen = ActividadOrigenes.API
    )
}

private class FakeActividadSource(
    private var datosLocales: List<Actividad>,
    private val datosRemotos: List<Actividad> = datosLocales
) : ActividadSource {

    override fun buscarCatalogo(
        texto: String,
        origen: String?,
        duracionMinima: Int?,
        duracionMaxima: Int?
    ): List<Actividad> = datosLocales

    override suspend fun sincronizarDesdeApi(): List<Actividad> {
        datosLocales = datosRemotos
        return datosLocales
    }
}