package com.example.kompa_app.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kompa_app.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.example.kompa_app.ui.auth.recuperar.RecuperarContrasenaActivity
import com.example.kompa_app.ui.home.HomeActivity
import android.widget.TextView

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        findViewById<MaterialButton>(R.id.btn_login).setOnClickListener {
            if (validarCampos()) {
                iniciarSesion()
            }
        }

        findViewById<TextView>(R.id.tv_olvide_contrasena).setOnClickListener {
            startActivity(Intent(this, RecuperarContrasenaActivity::class.java))
        }
    }

    private fun validarCampos(): Boolean {
        val correo = findViewById<TextInputEditText>(R.id.et_login_correo).text.toString().trim()
        val password = findViewById<TextInputEditText>(R.id.et_login_password).text.toString()

        return when {
            correo.isBlank() -> mostrarError(getString(R.string.error_correo))
            !Patterns.EMAIL_ADDRESS.matcher(correo).matches() -> mostrarError(getString(R.string.error_correo_invalido))
            password.isBlank() -> mostrarError(getString(R.string.error_login_password))
            else -> true
        }
    }

    private fun iniciarSesion() {
        // TODO: conectar con el backend para autenticar el correo y la contraseña.
        Toast.makeText(this, getString(R.string.welcome_toast), Toast.LENGTH_SHORT).show()
        startActivity(
            Intent(this, HomeActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        )
    }

    private fun mostrarError(mensaje: String): Boolean {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        return false
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}