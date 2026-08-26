package com.example.kompa_app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup

class PerfilPreviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_preview)

        mostrarDatosRecibidos()
        setupBotones()
    }

    private fun mostrarDatosRecibidos() {
        val bundle = intent.getBundleExtra(Constantes.BUNDLE_DATOS) ?: return

        val nombre = bundle.getString(Constantes.EXTRA_NOMBRE).orEmpty()
        val correo = bundle.getString(Constantes.EXTRA_CORREO).orEmpty()
        val fecha = bundle.getString(Constantes.EXTRA_FECHA_NACIMIENTO).orEmpty()
        val nacionalidad = bundle.getString(Constantes.EXTRA_NACIONALIDAD).orEmpty()
        val genero = bundle.getString(Constantes.EXTRA_GENERO).orEmpty()
        val conectar = bundle.getString(Constantes.EXTRA_CONECTAR).orEmpty()
        val idiomas = bundle.getString(Constantes.EXTRA_IDIOMAS).orEmpty()
        val intereses = bundle.getString(Constantes.EXTRA_INTERESES).orEmpty()
        val fotoUriTexto = bundle.getString(Constantes.EXTRA_FOTO_URI)

        findViewById<TextView>(R.id.tv_nombre_preview).text = nombre
        findViewById<TextView>(R.id.tv_correo_preview).text = correo
        findViewById<TextView>(R.id.tv_fecha_nacimiento_preview).text = "Nacimiento: $fecha"
        findViewById<TextView>(R.id.tv_nacionalidad_preview).text = "Nacionalidad: $nacionalidad"
        findViewById<TextView>(R.id.tv_genero_preview).text = "Género: $genero"
        findViewById<TextView>(R.id.tv_conectar_preview).text = "Conectar con: $conectar"

        agregarChipsDeTexto(idiomas, R.id.cg_idiomas_preview)
        agregarChipsDeTexto(intereses, R.id.cg_intereses_preview)

        fotoUriTexto?.let { uriTexto ->
            findViewById<ImageView>(R.id.iv_foto_perfil_preview).apply {
                setImageURI(Uri.parse(uriTexto))
                setPadding(0, 0, 0, 0)
            }
        }
    }

    private fun agregarChipsDeTexto(textoSeparadoPorComas: String, chipGroupId: Int) {
        val chipGroup = findViewById<ChipGroup>(chipGroupId)
        chipGroup.removeAllViews()
        textoSeparadoPorComas
            .split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .forEach { valor ->
                val chip = Chip(this).apply {
                    text = valor
                    isCheckable = false
                    isClickable = false
                    setChipBackgroundColorResource(R.color.purple_ultra_light)
                    setChipStrokeColorResource(R.color.purple_medium)
                    chipStrokeWidth = 1f
                }
                chipGroup.addView(chip)
            }
    }

    private fun setupBotones() {
        findViewById<MaterialButton>(R.id.bt_editar_perfil).setOnClickListener {
            finish()
        }

        findViewById<MaterialButton>(R.id.bt_confirmar_perfil).setOnClickListener {
            // TODO: aquí conectas con tu backend/Firebase para guardar el perfil final,
            // antes de mostrar la animación de celebración.
            startActivity(Intent(this, ConfirmacionActivity::class.java))
        }
    }
}