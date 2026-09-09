package com.example.kompa_app

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class ConfirmacionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirmacion)

        val airplaneView = findViewById<AirplaneAnimationView>(R.id.airplaneAnimationView)
        val btnContinuar = findViewById<MaterialButton>(R.id.bt_continuar)

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
            // TODO: cuando exista una pantalla principal/home de la app,
            // navega hacia allá con startActivity(...) antes del finishAffinity().
            Toast.makeText(this, getString(R.string.welcome_toast), Toast.LENGTH_SHORT).show()
            finishAffinity() // Cierra todo el flujo de registro; no se puede volver atrás
        }
    }
}