package com.example.kompa_app

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

    /**
     * Lee el Bundle de extras que llegó desde MainActivity (el formulario de registro)
     * y llena la pantalla de previsualización con esa información.
     */
    private fun mostrarDatosRecibidos() {
        val datosPerfil: Bundle = intent.extras ?: Bundle()

        val nombre = datosPerfil.getString(Constantes.EXTRA_NOMBRE, "")
        val correo = datosPerfil.getString(Constantes.EXTRA_CORREO, "")
        val fecha = datosPerfil.getString(Constantes.EXTRA_FECHA_NACIMIENTO, "")
        val nacionalidad = datosPerfil.getString(Constantes.EXTRA_NACIONALIDAD, "")
        val genero = datosPerfil.getString(Constantes.EXTRA_GENERO, "")
        val conectar = datosPerfil.getString(Constantes.EXTRA_CONECTAR, "")
        val idiomas = datosPerfil.getString(Constantes.EXTRA_IDIOMAS, "")
        val intereses = datosPerfil.getString(Constantes.EXTRA_INTERESES, "")
        val fotoUriTexto = datosPerfil.getString(Constantes.EXTRA_FOTO_URI)

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
        // MainActivity nunca se cerró, así que al hacer finish() aquí,
        // Android simplemente vuelve a mostrar esa misma instancia con sus datos intactos.
        findViewById<MaterialButton>(R.id.bt_editar_perfil).setOnClickListener {
            finish()
        }

        // Punto donde confirmas y guardas el perfil de forma definitiva.
        findViewById<MaterialButton>(R.id.bt_confirmar_perfil).setOnClickListener {
            // TODO: aquí conectas con tu backend/Firebase para guardar el perfil final,
            // antes de mostrar la animación de celebración.
            startActivity(android.content.Intent(this, ConfirmacionActivity::class.java))
        }
    }
}
