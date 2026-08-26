package com.example.kompa_app

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
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

    /**
     * Lee los extras que llegaron desde MainActivity (el formulario de registro)
     * y llena la pantalla de previsualización con esa información.
     */
    private fun mostrarDatosRecibidos() {
        val nombre = intent.getStringExtra(Constantes.EXTRA_NOMBRE).orEmpty()
        val correo = intent.getStringExtra(Constantes.EXTRA_CORREO).orEmpty()
        val fecha = intent.getStringExtra(Constantes.EXTRA_FECHA_NACIMIENTO).orEmpty()
        val nacionalidad = intent.getStringExtra(Constantes.EXTRA_NACIONALIDAD).orEmpty()
        val genero = intent.getStringExtra(Constantes.EXTRA_GENERO).orEmpty()
        val conectar = intent.getStringExtra(Constantes.EXTRA_CONECTAR).orEmpty()
        val idiomas = intent.getStringExtra(Constantes.EXTRA_IDIOMAS).orEmpty()
        val intereses = intent.getStringExtra(Constantes.EXTRA_INTERESES).orEmpty()
        val fotoUriTexto = intent.getStringExtra(Constantes.EXTRA_FOTO_URI)

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

    /**
     * Convierte un texto separado por comas (ej: "Playa, Yoga, Buceo") en
     * chips de solo lectura dentro del ChipGroup indicado.
     */
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
        // Regresa al formulario de registro para corregir datos.
        // Como MainActivity ya se cerró con finish(), se abre una nueva instancia.
        findViewById<MaterialButton>(R.id.bt_editar_perfil).setOnClickListener {
            finish()
        }

        // Punto donde confirmas y guardas el perfil de forma definitiva.
        findViewById<MaterialButton>(R.id.bt_confirmar_perfil).setOnClickListener {
            // TODO: aquí conectas con tu backend/Firebase para guardar el perfil final.
            Toast.makeText(this, "¡Perfil confirmado!", Toast.LENGTH_SHORT).show()
        }
    }
}
