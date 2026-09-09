package com.example.kompa_app

import com.example.kompa_app.core.util.PaisPorNacionalidad
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PaisPorNacionalidadTest {

    @Test
    fun `mapea cada nacionalidad a su codigo de pais`() {
        assertEquals("mx", PaisPorNacionalidad.codigoPais("Mexicana/o"))
        assertEquals("co", PaisPorNacionalidad.codigoPais("Colombiana/o"))
        assertEquals("ar", PaisPorNacionalidad.codigoPais("Argentina/o"))
        assertEquals("cl", PaisPorNacionalidad.codigoPais("Chilena/o"))
        assertEquals("pe", PaisPorNacionalidad.codigoPais("Peruana/o"))
        assertEquals("es", PaisPorNacionalidad.codigoPais("Española/o"))
        assertEquals("us", PaisPorNacionalidad.codigoPais("Estadounidense"))
        assertEquals("br", PaisPorNacionalidad.codigoPais("Brasileña/o"))
        assertEquals("ec", PaisPorNacionalidad.codigoPais("Ecuatoriana/o"))
    }

    @Test
    fun `otra o vacio no tienen restriccion de pais`() {
        assertNull(PaisPorNacionalidad.codigoPais("Otro/Otra"))
        assertNull(PaisPorNacionalidad.codigoPais(null))
        assertNull(PaisPorNacionalidad.codigoPais(""))
        assertNull(PaisPorNacionalidad.codigoPais("   "))
    }

    @Test
    fun `ignora variaciones de mayusculas y espacios`() {
        assertEquals("co", PaisPorNacionalidad.codigoPais("  colombiana/O "))
        assertEquals("es", PaisPorNacionalidad.codigoPais("ESPAÑOLA/O"))
    }
}