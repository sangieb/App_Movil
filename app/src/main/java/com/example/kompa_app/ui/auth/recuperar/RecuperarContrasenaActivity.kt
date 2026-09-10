package com.example.kompa_app.ui.auth.recuperar

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kompa_app.R
import com.example.kompa_app.core.util.configurarToolbar
import com.example.kompa_app.core.util.mostrarError
import com.example.kompa_app.core.util.navegarAtras
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class RecuperarContrasenaActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recuperar_contrasena)

        configurarToolbar(mostrarTitulo = false)

        findViewById<MaterialButton>(R.id.btn_recuperar).setOnClickListener {
            if (validarCampo()) {
                enviarCorreoRecuperacion()
            }
        }
    }

    private fun validarCampo(): Boolean {
        val correo = findViewById<TextInputEditText>(R.id.et_recuperar_correo).text.toString().trim()

        return when {
            correo.isBlank() -> mostrarError(getString(R.string.error_correo))
            !Patterns.EMAIL_ADDRESS.matcher(correo).matches() -> mostrarError(getString(R.string.error_correo_invalido))
            else -> true
        }
    }

    private fun enviarCorreoRecuperacion() {
        // TODO: conectar con el backend para enviar el correo de recuperación de contraseña.
        Toast.makeText(
            this,
            getString(R.string.recuperar_toast_enviado),
            Toast.LENGTH_SHORT
        ).show()
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        navegarAtras()
        return true
    }
}