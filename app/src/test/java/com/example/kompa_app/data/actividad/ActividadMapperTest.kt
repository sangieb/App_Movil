package com.example.kompa_app.data.actividad

import com.example.kompa_app.data.network.ActividadDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ActividadMapperTest {

    @Test
    fun `mapea el dto al dominio marcando origen api`() {
        val dto = ActividadDto(
            id = "a1",
            nombre = "Caminata",
            descripcion = "Descripción",
            ubicacion = "Bogotá",
            lat = 4.7,
            lon = -74.07,
            creadoPor = "Servidor",
            duracionMin = 120,
            fechaCreacionLong = 1234L,
            fechaActividadLong = 2345L
        )

        val resultado = dto.aDominio()

        assertEquals("a1", resultado.id)
        assertEquals("Caminata", resultado.nombre)
        assertEquals("Descripción", resultado.descripcion)
        assertEquals("Bogotá", resultado.ubicacion)
        assertEquals(4.7, resultado.lat, 0.0)
        assertEquals(-74.07, resultado.lon, 0.0)
        assertEquals("Servidor", resultado.creadoPor)
        assertEquals(120, resultado.duracionMin)
        assertEquals(1234L, resultado.fechaCreacionLong)
        assertEquals(2345L, resultado.fechaActividadLong)
        assertNull(resultado.fotoRuta)
        assertEquals(ActividadOrigenes.API, resultado.origen)
    }

    @Test
    fun `descarta filas sin id y elimina duplicados`() {
        val entrada = listOf(
            ActividadDto(id = "a1", nombre = "Primera"),
            ActividadDto(id = null, nombre = "Sin id"),
            ActividadDto(id = "a1", nombre = "Duplicada")
        )

        val resultado = entrada.aActividadesApi()

        assertEquals(listOf("a1"), resultado.map { it.id })
        assertEquals("Primera", resultado.single().nombre)
    }
}