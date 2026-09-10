package com.example.kompa_app.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class PerfilRepositoryTest {

    private lateinit var repositorio: PerfilRepository
    private lateinit var storage: FakePerfilStorage

    private val perfil = Perfil(
        nombre = "María Torres",
        correo = "maria@correo.com",
        fechaNacimiento = "1995-04-12",
        nacionalidad = "Mexicana/o",
        genero = "Mujer",
        conectar = "Todes",
        idiomas = "Español",
        intereses = "Senderismo, Viajes",
        fotoUri = null
    )

    @Before
    fun setUp() {
        storage = FakePerfilStorage()
        repositorio = PerfilRepository(storage)
    }

    @Test
    fun `recupera el perfil completo guardado`() {
        repositorio.guardar(perfil)

        val obtenido = repositorio.perfilActual()
        assertEquals(perfil, obtenido)
    }

    @Test
    fun `devuelve nulo cuando no hay perfil guardado`() {
        assertNull(repositorio.perfilActual())
    }

    @Test
    fun `devuelve la nacionalidad guardada`() {
        repositorio.guardar(perfil)

        assertEquals("Mexicana/o", repositorio.nacionalidad())
    }

    @Test
    fun `devuelve nulo sin nacionalidad sin perfil`() {
        assertNull(repositorio.nacionalidad())
    }

    @Test
    fun `sobreescribe el perfil anterior`() {
        repositorio.guardar(perfil)

        val nuevo = perfil.copy(nombre = "Ana López", idiomas = "Inglés, Francés")
        repositorio.guardar(nuevo)

        assertEquals("Ana López", repositorio.perfilActual()?.nombre)
        assertEquals("Inglés, Francés", repositorio.perfilActual()?.idiomas)
    }
}