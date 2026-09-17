package com.example.kompa_app.data.publicacion

import com.example.kompa_app.data.network.PublicacionDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PublicacionMapperTest {

    @Test
    fun `mapea los campos del dto al dominio`() {
        val dto = PublicacionDto(
            id = "p1",
            titulo = "Título",
            cuerpo = "Cuerpo",
            autor = "Autor",
            fechaCreacionLong = 1234L,
            lat = 4.7,
            lon = -74.07
        )

        val resultado = dto.aDominio()

        assertEquals("p1", resultado.id)
        assertEquals("Título", resultado.titulo)
        assertEquals("Cuerpo", resultado.cuerpo)
        assertEquals("Autor", resultado.autor)
        assertEquals(1234L, resultado.fechaCreacionLong)
        assertEquals(4.7, resultado.lat!!, 0.0)
        assertEquals(-74.07, resultado.lon!!, 0.0)
    }

    @Test
    fun `por defecto descarta filas sin id estable`() {
        val entrada = listOf(
            PublicacionDto(id = "p1", titulo = "Con id", cuerpo = "x", autor = "a"),
            PublicacionDto(id = null, titulo = "Sin id", cuerpo = "x", autor = "a"),
            PublicacionDto(id = "", titulo = "Id vacío", cuerpo = "x", autor = "a")
        )

        val resultado = entrada.aPublicaciones()

        assertEquals(listOf("p1"), resultado.map { it.id })
    }

    @Test
    fun `con soloConIdEstable false conserva filas sin id para mostrar`() {
        val entrada = listOf(
            PublicacionDto(id = "p1", titulo = "Con id", cuerpo = "x", autor = "a"),
            PublicacionDto(id = null, titulo = "Sin id", cuerpo = "x", autor = "a")
        )

        val resultado = entrada.aPublicaciones(soloConIdEstable = false)

        assertEquals(2, resultado.size)
        assertEquals("p1", resultado[0].id)
        assertTrue(resultado[1].id.isNotBlank())
    }

    @Test
    fun `elimina duplicados por id conservando la primera aparicion`() {
        val entrada = listOf(
            PublicacionDto(id = "p1", titulo = "Primera", cuerpo = "x", autor = "a"),
            PublicacionDto(id = "p1", titulo = "Duplicada", cuerpo = "y", autor = "b")
        )

        val resultado = entrada.aPublicaciones()

        assertEquals(listOf("p1"), resultado.map { it.id })
        assertEquals("Primera", resultado.single().titulo)
    }
}