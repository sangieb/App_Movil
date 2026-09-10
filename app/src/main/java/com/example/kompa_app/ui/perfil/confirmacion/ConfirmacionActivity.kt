package com.example.kompa_app.ui.perfil.confirmacion

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kompa_app.Constantes
import com.example.kompa_app.KompaApplication
import com.example.kompa_app.R
import com.example.kompa_app.data.Perfil
import com.example.kompa_app.ui.home.HomeActivity
import com.example.kompa_app.ui.widget.animacion.AirplaneAnimationView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton

class ConfirmacionActivity : AppCompatActivity() {

    private val graph by lazy { (application as KompaApplication).graph }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmacion)

        persistirPerfil()

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar_confirmacion)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val airplaneView = findViewById<AirplaneAnimationView>(R.id.airplaneAnimationView)
        val btnContinuar = findViewById<MaterialButton>(R.id.btn_continuar)

        // Cuando la animación del avión (el "brindis") termina su vuelta,
        // mostramos el botón para continuar.
        airplaneView.onFlightCompleted = {
            btnContinuar.visibility = View.VISIBLE
            btnContinuar.animate()
                .alpha(1f)
                .setDuration(400)
                .start()
        }

        btnContinuar.setOnClickListener {
            val bundle = intent.getBundleExtra(Constantes.BUNDLE_DATOS)
            val correo = bundle?.getString(Constantes.EXTRA_CORREO).orEmpty()
            val password = bundle?.getString(Constantes.EXTRA_PASSWORD)
            if (correo.isNotBlank() && password != null) {
                graph.cuentaStore.registrar(correo, password)
                graph.authRepository.crearSesion(correo)
            }
            Toast.makeText(this, getString(R.string.welcome_toast), Toast.LENGTH_SHORT).show()
            startActivity(
                Intent(this, HomeActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun persistirPerfil(): Perfil? {
        val bundle = intent.getBundleExtra(Constantes.BUNDLE_DATOS) ?: return null
        val perfil = Perfil(
            nombre = bundle.getString(Constantes.EXTRA_NOMBRE).orEmpty(),
            correo = bundle.getString(Constantes.EXTRA_CORREO).orEmpty(),
            fechaNacimiento = bundle.getString(Constantes.EXTRA_FECHA_NACIMIENTO).orEmpty(),
            nacionalidad = bundle.getString(Constantes.EXTRA_NACIONALIDAD).orEmpty(),
            genero = bundle.getString(Constantes.EXTRA_GENERO).orEmpty(),
            conectar = bundle.getString(Constantes.EXTRA_CONECTAR).orEmpty(),
            idiomas = bundle.getString(Constantes.EXTRA_IDIOMAS).orEmpty(),
            intereses = bundle.getString(Constantes.EXTRA_INTERESES).orEmpty(),
            fotoUri = bundle.getString(Constantes.EXTRA_FOTO_URI)
        )
        graph.perfilRepository.guardar(perfil)
        return perfil
    }
}