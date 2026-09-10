package com.example.kompa_app.ui.auth.login

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kompa_app.KompaApplication
import com.example.kompa_app.R
import com.example.kompa_app.core.util.configurarToolbar
import com.example.kompa_app.core.util.mostrarError
import com.example.kompa_app.core.util.navegarAtras
import com.example.kompa_app.data.session.ResultadoLogin
import com.example.kompa_app.ui.auth.recuperar.RecuperarContrasenaActivity
import com.example.kompa_app.ui.home.HomeActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class LoginActivity : AppCompatActivity() {

    private val graph by lazy { (application as KompaApplication).graph }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        configurarToolbar(mostrarTitulo = false)

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
        val correo = findViewById<TextInputEditText>(R.id.et_login_correo).text.toString().trim()
        val password = findViewById<TextInputEditText>(R.id.et_login_password).text.toString()
        when (graph.authRepository.login(correo, password)) {
            ResultadoLogin.EXITO -> {
                Toast.makeText(this, getString(R.string.welcome_toast), Toast.LENGTH_SHORT).show()
                startActivity(
                    Intent(this, HomeActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )
            }
            ResultadoLogin.CUENTA_INEXISTENTE ->
                mostrarError(getString(R.string.login_error_usuario_no_existe))
            ResultadoLogin.CONTRASENA_INCORRECTA ->
                mostrarError(getString(R.string.login_error_password_incorrecta))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        navegarAtras()
        return true
    }
}