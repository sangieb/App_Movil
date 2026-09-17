package com.example.kompa_app.data.catalogo

import com.example.kompa_app.data.network.CatalogoDto
import org.junit.Assert.assertEquals
import org.junit.Test

class CatalogoMapperTest {

    @Test
    fun `agrupa por tipo y conserva el orden declarado`() {
        val entrada = listOf(
            CatalogoDto(tipo = CatalogoTipos.INTERES, valor = "Buceo", orden = 1),
            CatalogoDto(tipo = CatalogoTipos.IDIOMA, valor = "Inglés", orden = 1),
            CatalogoDto(tipo = CatalogoTipos.NACIONALIDAD, valor = "Mexicana/o", orden = 0),
            CatalogoDto(tipo = CatalogoTipos.INTERES, valor = "Senderismo", orden = 0),
            CatalogoDto(tipo = CatalogoTipos.IDIOMA, valor = "Español", orden = 0),
            CatalogoDto(tipo = CatalogoTipos.NACIONALIDAD, valor = "Colombiana/o", orden = 1)
        )

        val resultado = entrada.agruparPorTipo()

        assertEquals(listOf("Senderismo", "Buceo"), resultado.intereses)
        assertEquals(listOf("Español", "Inglés"), resultado.idiomas)
        assertEquals(listOf("Mexicana/o", "Colombiana/o"), resultado.nacionalidades)
    }

    @Test
    fun `ignora valores nulos vacios o de tipos desconocidos`() {
        val entrada = listOf(
            CatalogoDto(tipo = "desconocido", valor = "Ignorado", orden = 0),
            CatalogoDto(tipo = CatalogoTipos.IDIOMA, valor = "Español", orden = 0),
            CatalogoDto(tipo = CatalogoTipos.IDIOMA, valor = "", orden = 1),
            CatalogoDto(tipo = CatalogoTipos.IDIOMA, valor = null, orden = 2),
            CatalogoDto(tipo = CatalogoTipos.IDIOMA, valor = "  Inglés  ", orden = 3)
        )

        val resultado = entrada.agruparPorTipo()

        assertEquals(listOf("Español", "Inglés"), resultado.idiomas)
        assertEquals(emptyList<String>(), resultado.nacionalidades)
        assertEquals(emptyList<String>(), resultado.intereses)
    }

    @Test
    fun `elementos sin orden quedan al final conservando su lugar relativo`() {
        val entrada = listOf(
            CatalogoDto(tipo = CatalogoTipos.INTERES, valor = "Yoga", orden = null),
            CatalogoDto(tipo = CatalogoTipos.INTERES, valor = "Playa", orden = 0),
            CatalogoDto(tipo = CatalogoTipos.INTERES, valor = "Compras", orden = null),
            CatalogoDto(tipo = CatalogoTipos.INTERES, valor = "Buceo", orden = 1)
        )

        val resultado = entrada.agruparPorTipo()

        assertEquals(listOf("Playa", "Buceo", "Yoga", "Compras"), resultado.intereses)
    }
}