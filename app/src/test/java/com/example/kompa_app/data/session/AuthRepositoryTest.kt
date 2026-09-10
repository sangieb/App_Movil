package com.example.kompa_app.data.session

import com.example.kompa_app.data.cuenta.FakeCuentaStorage
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class AuthRepositoryTest {

    private lateinit var storage: FakeTokenStorage
    private lateinit var cuentas: FakeCuentaStorage
    private var ahora: Long = 1_000_000L
    private lateinit var repositorio: AuthRepository

    private val duracionAccesoMs = TimeUnit.MINUTES.toMillis(15)

    @Before
    fun setUp() {
        storage = FakeTokenStorage()
        cuentas = FakeCuentaStorage()
        repositorio = AuthRepository(storage, cuentas, reloj = { ahora })
    }

    @Test
    fun `login con usuario registrado crea sesion`() {
        cuentas.registrar("maria@correo.com", "secreto123")

        val resultado = repositorio.login("maria@correo.com", "secreto123")

        assertEquals(ResultadoLogin.EXITO, resultado)
        assertTrue(repositorio.sesionActiva())
        assertNotNull(repositorio.usuarioId())
        assertNotNull(repositorio.tokenAcceso())
        assertEquals(ahora + duracionAccesoMs, storage.expiraEn())
    }

    @Test
    fun `login de usuario no registrado no crea sesion`() {
        val resultado = repositorio.login("nadie@correo.com", "secreto123")

        assertEquals(ResultadoLogin.CUENTA_INEXISTENTE, resultado)
        assertFalse(repositorio.sesionActiva())
    }

    @Test
    fun `login con contrasena incorrecta no crea sesion`() {
        cuentas.registrar("maria@correo.com", "secreto123")

        val resultado = repositorio.login("maria@correo.com", "claveEquivocada")

        assertEquals(ResultadoLogin.CONTRASENA_INCORRECTA, resultado)
        assertFalse(repositorio.sesionActiva())
    }

    @Test
    fun `login con contrasena corta no crea sesion`() {
        cuentas.registrar("maria@correo.com", "secreto123")

        val resultado = repositorio.login("maria@correo.com", "12345")

        assertEquals(ResultadoLogin.CONTRASENA_INCORRECTA, resultado)
        assertFalse(repositorio.sesionActiva())
    }

    @Test
    fun `login con campos vacios no crea sesion`() {
        assertEquals(ResultadoLogin.CONTRASENA_INCORRECTA, repositorio.login("", "secreto123"))
        assertEquals(ResultadoLogin.CONTRASENA_INCORRECTA, repositorio.login("maria@correo.com", "  "))
        assertFalse(repositorio.sesionActiva())
    }

    @Test
    fun `renovar sin expiry devuelve true sin tocar el token`() {
        repositorio.crearSesion("maria@correo.com")
        val tokenOriginal = repositorio.tokenAcceso()

        assertTrue(repositorio.renovarSiEsNecesario())
        assertEquals(tokenOriginal, repositorio.tokenAcceso())
    }

    @Test
    fun `renovar con token expirado refresca la sesion`() {
        repositorio.crearSesion("maria@correo.com")
        val tokenOriginal = repositorio.tokenAcceso()

        ahora = ahora + duracionAccesoMs + 1

        assertTrue(repositorio.renovarSiEsNecesario())
        assertNotEquals(tokenOriginal, repositorio.tokenAcceso())
        assertEquals(ahora + duracionAccesoMs, storage.expiraEn())
    }

    @Test
    fun `refrescar sin refresh token devuelve false`() {
        assertFalse(repositorio.refrescar())
    }

    @Test
    fun `refrescar renueva tokens y expiracion`() {
        repositorio.crearSesion("maria@correo.com")
        val refreshOriginal = storage.refreshToken()
        val expiracionOriginal = storage.expiraEn()

        ahora += 1_000L
        assertTrue(repositorio.refrescar())

        assertNotEquals(refreshOriginal, storage.refreshToken())
        assertNotEquals(expiracionOriginal, storage.expiraEn())
        assertEquals(ahora + duracionAccesoMs, storage.expiraEn())
    }

    @Test
    fun `logout limpia toda la sesion`() {
        repositorio.crearSesion("maria@correo.com")
        repositorio.logout()

        assertFalse(repositorio.sesionActiva())
        assertFalse(repositorio.usuarioId() != null)
        assertFalse(repositorio.tokenAcceso() != null)
    }

    @Test
    fun `renovar sin sesion no crea tokens`() {
        assertFalse(repositorio.renovarSiEsNecesario())
        assertFalse(repositorio.sesionActiva())
    }
}