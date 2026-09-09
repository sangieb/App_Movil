package com.example.kompa_app.ui.perfil.preview

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.example.kompa_app.Constantes
import com.example.kompa_app.R
import com.example.kompa_app.ui.perfil.confirmacion.ConfirmacionActivity

class PerfilPreviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_preview)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

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
        findViewById<TextView>(R.id.tv_fecha_nacimiento_preview).text =
            getString(R.string.preview_nacimiento_formato, fecha)
        findViewById<TextView>(R.id.tv_nacionalidad_preview).text =
            getString(R.string.preview_nacionalidad_formato, nacionalidad)
        findViewById<TextView>(R.id.tv_genero_preview).text =
            getString(R.string.preview_genero_formato, genero)
        findViewById<TextView>(R.id.tv_conectar_preview).text =
            getString(R.string.preview_conectar_formato, conectar)

        agregarChipsDeTexto(idiomas, R.id.cg_idiomas_preview)
        agregarChipsDeTexto(intereses, R.id.cg_intereses_preview)

        fotoUriTexto?.let { uriTexto ->
            findViewById<ImageView>(R.id.img_foto_perfil_preview).apply {
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
        findViewById<MaterialButton>(R.id.btn_editar_perfil).setOnClickListener {
            finish()
        }

        findViewById<MaterialButton>(R.id.btn_confirmar_perfil).setOnClickListener {
            val datos = intent.getBundleExtra(Constantes.BUNDLE_DATOS) ?: Bundle()
            startActivity(
                Intent(this, ConfirmacionActivity::class.java)
                    .putExtra(Constantes.BUNDLE_DATOS, datos)
            )
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}