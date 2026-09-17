package com.example.kompa_app.ui.feed

import com.example.kompa_app.data.publicacion.Publicacion
import com.example.kompa_app.data.publicacion.PublicacionSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FeedViewModelTest {

    private val locales = listOf(
        Publicacion("1", "Título local", "Cuerpo local", "Autor", 1000L, null, null)
    )

    private val actualizadas = locales + Publicacion(
        "2", "Título nuevo", "Cuerpo nuevo", "Autor", 2000L, null, null
    )

    @Test
    fun `emite cargando, luego el cache local y finalmente las actualizadas`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            verificaSecuencia(
                repositorio = FakePublicacionSource(locales, refrescado = actualizadas),
                esperado = listOf(
                    FeedUiState.Cargando,
                    FeedUiState.Exito(locales),
                    FeedUiState.Exito(actualizadas)
                )
            )
        } finally {
            Dispatchers.resetMain()
        }
    }

    @Test
    fun `una falla inesperada durante el refresco deriva en estado de error`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            verificaSecuencia(
                repositorio = FakePublicacionSource(locales, falloRefresco = RuntimeException("boom")),
                esperado = listOf(
                    FeedUiState.Cargando,
                    FeedUiState.Exito(locales),
                    FeedUiState.Error
                )
            )
        } finally {
            Dispatchers.resetMain()
        }
    }

    private fun TestScope.verificaSecuencia(
        repositorio: PublicacionSource,
        esperado: List<FeedUiState>
    ) {
        val viewModel = FeedViewModel(
            repositorio,
            dispatcherIO = StandardTestDispatcher(testScheduler)
        )

        val estados = mutableListOf<FeedUiState>()
        val recopilador = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.estado.collect { estados.add(it) }
        }
        advanceUntilIdle()
        recopilador.cancel()

        assertEquals(esperado, estados.toList())
    }
}

private class FakePublicacionSource(
    private val locales: List<Publicacion>,
    private val refrescado: List<Publicacion> = locales,
    private val falloRefresco: Throwable? = null
) : PublicacionSource {

    override fun publicacionesLocales(): List<Publicacion> = locales

    override suspend fun refrescar(): List<Publicacion> {
        falloRefresco?.let { throw it }
        return refrescado
    }
}