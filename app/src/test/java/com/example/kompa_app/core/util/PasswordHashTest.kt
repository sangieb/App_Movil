package com.example.kompa_app.core.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PasswordHashTest {

    @Test
    fun `genera sal y hash distintos para la misma contrasena`() {
        val primero = PasswordHash.hash("secreto123")
        val segundo = PasswordHash.hash("secreto123")

        assertNotEquals(primero.sal, segundo.sal)
        assertNotEquals(primero.hash, segundo.hash)
    }

    @Test
    fun `verifica correctamente con la sal original`() {
        val datos = PasswordHash.hash("secreto123")

        assertTrue(PasswordHash.verificar("secreto123", datos.sal, datos.hash))
    }

    @Test
    fun `no almacena la contrasena en texto plano`() {
        val datos = PasswordHash.hash("secreto123")

        assertFalse(datos.hash.contains("secreto123"))
        assertFalse(datos.sal.contains("secreto123"))
    }

    @Test
    fun `rechaza una contrasena incorrecta`() {
        val datos = PasswordHash.hash("secreto123")

        assertFalse(PasswordHash.verificar("otraClave", datos.sal, datos.hash))
    }

    @Test
    fun `rechaza hex invalido`() {
        assertFalse(PasswordHash.verificar("clave", "xyz", "abc"))
    }

    @Test
    fun `codificar y decodificar hex son inversos`() {
        val original = byteArrayOf(0x00, 0x0f, 0x10, 0xff.toByte(), 0x7a)

        val codificado = PasswordHash.codificarHex(original)
        assertEquals("000f10ff7a", codificado)
        assertTrue(original.contentEquals(PasswordHash.decodificarHex(codificado)))
    }
}