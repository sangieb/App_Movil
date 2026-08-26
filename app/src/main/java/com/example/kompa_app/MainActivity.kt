package com.example.kompa_app

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    // Máximo de intereses que el usuario puede elegir
    private val MAX_INTERESES = 5

    // Lista de intereses disponibles (agrega o quita los que necesites)
    private val INTERESES_DISPONIBLES = listOf(
        "Senderismo", "Playa", "Mochilero", "Gastronomía", "Fotografía",
        "Vida nocturna", "Museos y cultura", "Deportes extremos", "Yoga",
        "Café y trabajo remoto", "Compras", "Naturaleza", "Fiestas locales",
        "Buceo", "Road trips"
    )

    private val NACIONALIDADES = listOf(
        "Mexicana", "Colombiana", "Argentina", "Chilena", "Peruana",
        "Española", "Estadounidense", "Brasileña", "Ecuatoriana", "Otra"
    )

    private val IDIOMAS = listOf(
        "Español", "Inglés", "Portugués", "Francés", "Alemán", "Italiano", "Otro"
    )

    private var fotoUri: Uri? = null
    private val interesesSeleccionados = mutableSetOf<String>()

    // Selector de imagen desde galería
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            fotoUri = it
            findViewById<android.widget.ImageView>(R.id.iv_foto_perfil).apply {
                setImageURI(it)
                setPadding(0, 0, 0, 0)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupFoto()
        setupFechaNacimiento()
        setupNacionalidad()
        setupIdiomas()
        setupIntereses()
        setupBotonRegistrar()
    }

    private fun setupFoto() {
        val ivFotoPerfil = findViewById<android.widget.ImageView>(R.id.iv_foto_perfil)
        val btAgregarFoto = findViewById<android.widget.ImageView>(R.id.bt_agregar_foto)
        val abrirGaleria = { pickImageLauncher.launch("image/*") }
        ivFotoPerfil.setOnClickListener { abrirGaleria() }
        btAgregarFoto.setOnClickListener { abrirGaleria() }
    }

    private fun setupFechaNacimiento() {
        val etFechaNacimiento = findViewById<TextInputEditText>(R.id.et_fecha_nacimiento)
        etFechaNacimiento.setOnClickListener {
            val calendario = Calendar.getInstance()
            // Sugerimos año actual - 18 para fomentar mayoría de edad por defecto
            calendario.add(Calendar.YEAR, -18)

            DatePickerDialog(
                this,
                { _, year, month, day ->
                    val fechaSeleccionada = Calendar.getInstance()
                    fechaSeleccionada.set(year, month, day)
                    val formato = SimpleDateFormat("dd/MM/yyyy", Locale("es"))
                    etFechaNacimiento.setText(formato.format(fechaSeleccionada.time))
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupNacionalidad() {
        val actvNacionalidad = findViewById<android.widget.AutoCompleteTextView>(R.id.actv_nacionalidad)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, NACIONALIDADES)
        actvNacionalidad.setAdapter(adapter)
    }

    private fun setupIdiomas() {
        // Para selección múltiple real de idiomas, considera reemplazar este
        // AutoCompleteTextView por un ChipGroup similar al de intereses.
        val actvIdiomas = findViewById<android.widget.AutoCompleteTextView>(R.id.actv_idiomas)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, IDIOMAS)
        actvIdiomas.setAdapter(adapter)
    }

    private fun setupIntereses() {
        val cgIntereses = findViewById<com.google.android.material.chip.ChipGroup>(R.id.cg_intereses)
        val tvContadorIntereses = findViewById<android.widget.TextView>(R.id.tv_contador_intereses)

        INTERESES_DISPONIBLES.forEach { interes ->
            val chipInteres = Chip(this).apply {
                text = interes
                isCheckable = true
                setChipBackgroundColorResource(R.color.purple_ultra_light)
                setChipStrokeColorResource(R.color.purple_medium)
                chipStrokeWidth = 1f
            }

            chipInteres.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    if (interesesSeleccionados.size >= MAX_INTERESES) {
                        buttonView.isChecked = false
                        Toast.makeText(
                            this,
                            "Solo puedes elegir hasta $MAX_INTERESES intereses",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnCheckedChangeListener
                    }
                    interesesSeleccionados.add(interes)
                } else {
                    interesesSeleccionados.remove(interes)
                }
                tvContadorIntereses.text = "Elige hasta $MAX_INTERESES (${interesesSeleccionados.size}/$MAX_INTERESES)"
            }

            cgIntereses.addView(chipInteres)
        }
    }

    private fun setupBotonRegistrar() {
        findViewById<com.google.android.material.button.MaterialButton>(R.id.bt_registrar)
            .setOnClickListener {
                if (validarFormulario()) {
                    // TODO: aquí conectas con tu backend/Firebase para crear la cuenta
                    Toast.makeText(this, "Cuenta creada correctamente", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun validarFormulario(): Boolean {
        val nombre = findViewById<TextInputEditText>(R.id.et_nombre).text.toString()
        val correo = findViewById<TextInputEditText>(R.id.et_correo).text.toString()
        val password = findViewById<TextInputEditText>(R.id.et_password).text.toString()
        val fecha = findViewById<TextInputEditText>(R.id.et_fecha_nacimiento).text.toString()
        val genero = findViewById<android.widget.RadioGroup>(R.id.rg_genero).checkedRadioButtonId
        val conectarChips = findViewById<com.google.android.material.chip.ChipGroup>(R.id.cg_conectar).checkedChipIds

        return when {
            nombre.isBlank() -> mostrarError("Ingresa tu nombre")
            correo.isBlank() -> mostrarError("Ingresa tu correo")
            password.length < 6 -> mostrarError("La contraseña debe tener al menos 6 caracteres")
            fecha.isBlank() -> mostrarError("Selecciona tu fecha de nacimiento")
            genero == -1 -> mostrarError("Selecciona tu género")
            conectarChips.isEmpty() -> mostrarError("Selecciona con quién quieres conectar")
            interesesSeleccionados.isEmpty() -> mostrarError("Elige al menos un interés")
            fotoUri == null -> mostrarError("Sube una foto de perfil")
            else -> true
        }
    }

    private fun mostrarError(mensaje: String): Boolean {
        Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
        return false
    }
}
