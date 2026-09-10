package com.example.kompa_app.core.util

import java.security.MessageDigest
import java.security.SecureRandom

object PasswordHash {

    private const val ALGORITMO = "SHA-256"
    private const val TAMANO_SAL = 16
    private val seguros = SecureRandom()

    fun hash(password: String): PasswordHashData {
        val sal = ByteArray(TAMANO_SAL).also(seguros::nextBytes)
        val hash = calcular(sal, password)
        return PasswordHashData(codificarHex(sal), codificarHex(hash))
    }

    fun verificar(password: String, salHex: String, hashHex: String): Boolean {
        return try {
            val sal = decodificarHex(salHex)
            val hashReal = calcular(sal, password)
            MessageDigest.isEqual(decodificarHex(hashHex), hashReal)
        } catch (_: IllegalArgumentException) {
            false
        }
    }

    private fun calcular(sal: ByteArray, password: String): ByteArray =
        MessageDigest.getInstance(ALGORITMO)
            .digest(sal + password.toByteArray(Charsets.UTF_8))

    fun codificarHex(bytes: ByteArray): String {
        val hex = StringBuilder(bytes.size * 2)
        bytes.forEach { byte ->
            val valor = byte.toInt() and 0xFF
            hex.append(HEX[valor ushr 4])
            hex.append(HEX[valor and 0x0F])
        }
        return hex.toString()
    }

    fun decodificarHex(hex: String): ByteArray {
        if (hex.length % 2 != 0) throw IllegalArgumentException("Hex inválido")
        return ByteArray(hex.length / 2) { indice ->
            val posicion = indice * 2
            (Character.digit(hex[posicion], 16) shl 4 or Character.digit(hex[posicion + 1], 16)).toByte()
        }
    }

    private val HEX = "0123456789abcdef".toCharArray()

    data class PasswordHashData(val sal: String, val hash: String)
}