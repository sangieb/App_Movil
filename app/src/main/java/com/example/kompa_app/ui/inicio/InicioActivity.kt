package com.example.kompa_app.ui.inicio

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.kompa_app.R
import com.example.kompa_app.ui.auth.login.LoginActivity
import com.example.kompa_app.ui.auth.registro.RegistroActivity

class InicioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_inicio)

        val imgLogo = findViewById<ImageView>(R.id.img_logo)
        val llBotones = findViewById<LinearLayout>(R.id.ll_botones)

        prepararAnimacion(imgLogo, llBotones)

        findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_registrarse)
            .setOnClickListener { startActivity(Intent(this, RegistroActivity::class.java)) }

        findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_iniciar_sesion)
            .setOnClickListener { startActivity(Intent(this, LoginActivity::class.java)) }
    }

    private fun prepararAnimacion(imgLogo: ImageView, llBotones: LinearLayout) {
        imgLogo.alpha = 0f
        imgLogo.scaleX = 0.8f
        imgLogo.scaleY = 0.8f

        imgLogo.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(600L)
            .setInterpolator(DecelerateInterpolator())
            .start()

        llBotones.alpha = 0f
        llBotones.translationY = 24f
        llBotones.visibility = View.VISIBLE
        llBotones.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(400L)
            .setStartDelay(350L)
            .setInterpolator(DecelerateInterpolator())
            .start()
    }
}