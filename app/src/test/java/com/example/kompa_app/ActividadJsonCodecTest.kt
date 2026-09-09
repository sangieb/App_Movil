package com.example.kompa_app

import com.example.kompa_app.data.Actividad
import com.example.kompa_app.data.ActividadJsonCodec
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ActividadJsonCodecTest {

    private val codec = ActividadJsonCodec()

    @Test
    fun `round trip conserva todos los campos`() {
        val original = listOf(
            Actividad(
                id = "abc-123",
                nombre = "Senderismo por la montaña",
                descripcion = "Ruta guiada con grupo pequeño",
                ubicacion = "Bosque de Chapultepec",
                lat = 19.4126,
                lon = -99.1814,
                creadoPor = "Tú",
                duracionMin = 90,
                fechaCreacionLong = 1_700_000_000_000L,
                fechaActividadLong = 1_700_000_500_000L,
                fotoRuta = "/ruta/a/la/foto.jpg",
                origen = "local"
            )
        )

        val json = codec.aJson(original)
        val restaurado = codec.desdeJson(json)

        assertEquals(original, restaurado)
    }

    @Test
    fun `lista vacia se serializa y restaura`() {
        val json = codec.aJson(emptyList<Actividad>())
        assertTrue(codec.desdeJson(json).isEmpty())
    }

    @Test
    fun `json vacio devuelve lista vacia`() {
        assertEquals(emptyList<Actividad>(), codec.desdeJson("[]"))
    }

    @Test
    fun `json invalido devuelve lista vacia sin lanzar`() {
        assertEquals(emptyList<Actividad>(), codec.desdeJson("no es json"))
    }

    @Test
    fun `campos nulos sobreviven el round trip`() {
        val original = listOf(
            Actividad(
                id = "osm_n_1",
                nombre = "Mirador",
                descripcion = "",
                ubicacion = "",
                lat = 19.0,
                lon = -99.0,
                creadoPor = "Comunidad OSM",
                duracionMin = null,
                fechaCreacionLong = null,
                fotoRuta = null,
                origen = "api"
            )
        )

        assertEquals(original, codec.desdeJson(codec.aJson(original)))
    }
}