package com.example.kompa_app

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RegistroActivity : AppCompatActivity() {

    private val MAX_INTERESES = 5

    private val INTERESES_DISPONIBLES = listOf(
        "Senderismo", "Playa", "Mochilero", "Gastronomía", "Fotografía",
        "Vida nocturna", "Museos y cultura", "Deportes extremos", "Yoga",
        "Café y trabajo remoto", "Compras", "Naturaleza", "Fiestas locales",
        "Buceo", "Road trips"
    )

    private val NACIONALIDADES = listOf(
        "Mexicana/o", "Colombiana/o", "Argentina/o", "Chilena/o", "Peruana/o",
        "Española/o", "Estadounidense", "Brasileña/o", "Ecuatoriana/o", "Otro/Otra"
    )

    private val IDIOMAS = listOf(
        "Español", "Inglés", "Portugués", "Francés", "Alemán", "Italiano", "Otro"
    )

    private var fotoUri: Uri? = null
    private val interesesSeleccionados = mutableSetOf<String>()

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            fotoUri = it
            findViewById<android.widget.ImageView>(R.id.img_foto_perfil).apply {
                setImageURI(it)
                setPadding(0, 0, 0, 0)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        setupToolbar()
        setupFoto()
        setupFechaNacimiento()
        setupNacionalidad()
        setupIdiomas()
        setupConectarChips()
        setupIntereses()
        setupBotonRegistrar()
    }

    private fun setupToolbar() {
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupFoto() {
        val imgFotoPerfil = findViewById<android.widget.ImageView>(R.id.img_foto_perfil)
        val imgAgregarFoto = findViewById<android.widget.ImageView>(R.id.img_agregar_foto)
        val abrirGaleria = { pickImageLauncher.launch("image/*") }
        imgFotoPerfil.setOnClickListener { abrirGaleria() }
        imgAgregarFoto.setOnClickListener { abrirGaleria() }
    }

    private fun setupFechaNacimiento() {
        val etFechaNacimiento = findViewById<TextInputEditText>(R.id.et_fecha_nacimiento)
        etFechaNacimiento.setOnClickListener {
            val calendario = Calendar.getInstance()
            calendario.add(Calendar.YEAR, -18)

            DatePickerDialog(
                this,
                { _, year, month, day ->
                    val fechaSeleccionada = Calendar.getInstance()
                    fechaSeleccionada.set(year, month, day)
                    val formato = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("es"))
                    etFechaNacimiento.setText(formato.format(fechaSeleccionada.time))
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupNacionalidad() {
        val actvNacionalidad = findViewById<AutoCompleteTextView>(R.id.actv_nacionalidad)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, NACIONALIDADES)
        actvNacionalidad.setAdapter(adapter)
    }

    private fun setupIdiomas() {
        val actvIdiomas = findViewById<AutoCompleteTextView>(R.id.actv_idiomas)
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, IDIOMAS)
        actvIdiomas.setAdapter(adapter)
    }

    private fun setupConectarChips() {
        val cgConectar = findViewById<ChipGroup>(R.id.cg_conectar)
        val ids = listOf(R.id.chip_mujeres, R.id.chip_hombres, R.id.chip_todes)

        ids.forEach { chipId ->
            val chip = cgConectar.findViewById<Chip>(chipId)
            chip.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    buttonView.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_primary))
                    buttonView.setTextColor(ContextCompat.getColor(this, R.color.white))
                } else {
                    buttonView.setBackgroundColor(ContextCompat.getColor(this, R.color.purple_ultra_light))
                    buttonView.setTextColor(ContextCompat.getColor(this, R.color.text_dark))
                }
            }
        }
    }

    private fun setupIntereses() {
        val cgIntereses = findViewById<ChipGroup>(R.id.cg_intereses)
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
                val chip = buttonView as Chip
                if (isChecked) {
                    if (interesesSeleccionados.size >= MAX_INTERESES) {
                        chip.isChecked = false
                        Toast.makeText(
                            this,
                            resources.getQuantityString(R.plurals.error_max_intereses, MAX_INTERESES, MAX_INTERESES),
                            Toast.LENGTH_SHORT
                        ).show()
                        return@setOnCheckedChangeListener
                    }
                    interesesSeleccionados.add(interes)
                    chip.setChipBackgroundColorResource(R.color.purple_primary)
                    chip.setTextColor(ContextCompat.getColor(this, R.color.white))
                } else {
                    interesesSeleccionados.remove(interes)
                    chip.setChipBackgroundColorResource(R.color.purple_ultra_light)
                    chip.setTextColor(ContextCompat.getColor(this, R.color.text_dark))
                }
                tvContadorIntereses.text = getString(
                    R.string.main_contador_intereses_formato,
                    MAX_INTERESES,
                    interesesSeleccionados.size,
                    MAX_INTERESES
                )
            }

            cgIntereses.addView(chipInteres)
        }
    }

    private fun setupBotonRegistrar() {
        findViewById<MaterialButton>(R.id.btn_registrar)
            .setOnClickListener {
                if (validarFormulario()) {
                    irAPerfilPreview()
                }
            }
    }

    private fun irAPerfilPreview() {
        val nombre = findViewById<TextInputEditText>(R.id.et_nombre).text.toString().trim()
        val correo = findViewById<TextInputEditText>(R.id.et_correo).text.toString().trim()
        val fecha = findViewById<TextInputEditText>(R.id.et_fecha_nacimiento).text.toString().trim()
        val nacionalidad = findViewById<AutoCompleteTextView>(R.id.actv_nacionalidad).text.toString().trim()
        val idiomas = findViewById<AutoCompleteTextView>(R.id.actv_idiomas).text.toString().trim()

        val rgGenero = findViewById<RadioGroup>(R.id.rg_genero)
        val generoSeleccionado = if (rgGenero.checkedRadioButtonId == -1) "" else
            findViewById<RadioButton>(rgGenero.checkedRadioButtonId).text.toString()

        val cgConectar = findViewById<ChipGroup>(R.id.cg_conectar)
        val conectarSeleccionado = cgConectar.checkedChipIds.joinToString(", ") { id ->
            cgConectar.findViewById<Chip>(id).text.toString()
        }

        val intereses = interesesSeleccionados.joinToString(", ")

        val bundle = Bundle().apply {
            putString(Constantes.EXTRA_NOMBRE, nombre)
            putString(Constantes.EXTRA_CORREO, correo)
            putString(Constantes.EXTRA_FECHA_NACIMIENTO, fecha)
            putString(Constantes.EXTRA_NACIONALIDAD, nacionalidad)
            putString(Constantes.EXTRA_GENERO, generoSeleccionado)
            putString(Constantes.EXTRA_CONECTAR, conectarSeleccionado)
            putString(Constantes.EXTRA_IDIOMAS, idiomas)
            putString(Constantes.EXTRA_INTERESES, intereses)
            putString(Constantes.EXTRA_FOTO_URI, fotoUri?.toString())
        }

        val intent = Intent(this, PerfilPreviewActivity::class.java).apply {
            putExtra(Constantes.BUNDLE_DATOS, bundle)
        }
        startActivity(intent)
    }

    private fun validarFormulario(): Boolean {
        val nombre = findViewById<TextInputEditText>(R.id.et_nombre).text.toString().trim()
        val correo = findViewById<TextInputEditText>(R.id.et_correo).text.toString().trim()
        val password = findViewById<TextInputEditText>(R.id.et_password).text.toString()
        val fecha = findViewById<TextInputEditText>(R.id.et_fecha_nacimiento).text.toString().trim()
        val nacionalidad = findViewById<AutoCompleteTextView>(R.id.actv_nacionalidad).text.toString().trim()
        val idiomas = findViewById<AutoCompleteTextView>(R.id.actv_idiomas).text.toString().trim()
        val genero = findViewById<RadioGroup>(R.id.rg_genero).checkedRadioButtonId
        val conectarChips = findViewById<ChipGroup>(R.id.cg_conectar).checkedChipIds
        val cbDatosPersonales = findViewById<CheckBox>(R.id.cb_datos_personales)

        return when {
            nombre.isBlank() -> mostrarError(getString(R.string.error_nombre))
            nombre.length < 2 -> mostrarError(getString(R.string.error_nombre_corto))
            correo.isBlank() -> mostrarError(getString(R.string.error_correo))
            !Patterns.EMAIL_ADDRESS.matcher(correo).matches() -> mostrarError(getString(R.string.error_correo_invalido))
            password.length < 6 -> mostrarError(getString(R.string.error_password))
            fecha.isBlank() -> mostrarError(getString(R.string.error_fecha))
            !esMayorDeEdad(fecha) -> mostrarError(getString(R.string.error_mayor_edad))
            nacionalidad.isBlank() -> mostrarError(getString(R.string.error_nacionalidad))
            idiomas.isBlank() -> mostrarError(getString(R.string.error_idioma))
            genero == -1 -> mostrarError(getString(R.string.error_genero))
            conectarChips.isEmpty() -> mostrarError(getString(R.string.error_conectar))
            interesesSeleccionados.isEmpty() -> mostrarError(getString(R.string.error_intereses))
            !cbDatosPersonales.isChecked -> mostrarError(getString(R.string.error_datos_personales))
            else -> true
        }
    }

    private fun esMayorDeEdad(fecha: String): Boolean {
        return try {
            val formato = SimpleDateFormat("dd/MM/yyyy", Locale.forLanguageTag("es"))
            val fechaNacimiento = formato.parse(fecha) ?: return false
            val calendarioNacimiento = Calendar.getInstance().apply { time = fechaNacimiento }
            val hoy = Calendar.getInstance()

            var edad = hoy.get(Calendar.YEAR) - calendarioNacimiento.get(Calendar.YEAR)
            if (hoy.get(Calendar.MONTH) < calendarioNacimiento.get(Calendar.MONTH) ||
                (hoy.get(Calendar.MONTH) == calendarioNacimiento.get(Calendar.MONTH) &&
                    hoy.get(Calendar.DAY_OF_MONTH) < calendarioNacimiento.get(Calendar.DAY_OF_MONTH))
            ) {
                edad--
            }
            edad >= 18
        } catch (e: Exception) {
            false
        }
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