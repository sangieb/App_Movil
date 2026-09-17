package com.example.kompa_app.ui.auth.registro

import com.example.kompa_app.data.CatalogosRegistro
import com.example.kompa_app.data.CatalogoSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.TestScope
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegistroViewModelTest {

    private val locales = CatalogosRegistro(
        nacionalidades = listOf("Mexicana/o"),
        idiomas = listOf("Español"),
        intereses = listOf("Yoga", "Buceo")
    )

    private val actualizados = CatalogosRegistro(
        nacionalidades = listOf("Mexicana/o", "Colombiana/o"),
        idiomas = listOf("Español", "Inglés"),
        intereses = listOf("Yoga", "Buceo", "Playa")
    )

    @Test
    fun `emite cargando, luego el cache local y finalmente los datos actualizados`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        try {
            verificaSecuencia(
                repositorio = FakeCatalogoRepository(locales, refrescado = actualizados),
                esperado = listOf(
                    RegistroUiState.Cargando,
                    RegistroUiState.Listo(locales),
                    RegistroUiState.Listo(actualizados)
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
                repositorio = FakeCatalogoRepository(locales, falloRefresco = RuntimeException("boom")),
                esperado = listOf(
                    RegistroUiState.Cargando,
                    RegistroUiState.Listo(locales),
                    RegistroUiState.Error
                )
            )
        } finally {
            Dispatchers.resetMain()
        }
    }

    private fun TestScope.verificaSecuencia(
        repositorio: CatalogoSource,
        esperado: List<RegistroUiState>
    ) {
        val viewModel = RegistroViewModel(
            repositorio,
            dispatcherIO = StandardTestDispatcher(testScheduler)
        )

        val estados = mutableListOf<RegistroUiState>()
        val recopilador = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.estado.collect { estados.add(it) }
        }
        advanceUntilIdle()
        recopilador.cancel()

        assertEquals(esperado, estados.toList())
    }
}

private class FakeCatalogoRepository(
    private val locales: CatalogosRegistro,
    private val refrescado: CatalogosRegistro = locales,
    private val falloRefresco: Throwable? = null
) : CatalogoSource {

    override fun catalogosLocales(): CatalogosRegistro = locales

    override suspend fun refrescar(): CatalogosRegistro {
        falloRefresco?.let { throw it }
        return refrescado
    }
}