package com.example.kompa_app.ui.nueva

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.example.kompa_app.Constantes
import com.example.kompa_app.KompaApplication
import com.example.kompa_app.R
import com.example.kompa_app.data.Actividad
import com.example.kompa_app.core.util.FotoUtil
import com.example.kompa_app.core.util.FechaFormatos
import com.example.kompa_app.core.util.FuenteDeMapa
import com.example.kompa_app.core.util.PaisPorNacionalidad
import com.example.kompa_app.core.util.UbicacionHelper
import com.example.kompa_app.core.util.configurarToolbar
import com.example.kompa_app.core.util.mostrarError
import com.example.kompa_app.core.util.navegarAtras
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.Calendar
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker

class NuevaActividadActivity : AppCompatActivity() {

    private val graph by lazy { (application as KompaApplication).graph }

    private lateinit var mapa: MapView
    private var fotoUri: Uri? = null
    private var latSeleccionada: Double? = null
    private var lonSeleccionada: Double? = null
    private var marcadorUbicacion: Marker? = null
    private var guardando = false
    private var fechaActividadSeleccionada: Long? = null

    private lateinit var etUbicacion: TextInputEditText
    private lateinit var imgFotoGrupo: android.widget.ImageView
    private lateinit var etFechaActividad: TextInputEditText

    private val selectorFoto = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            fotoUri = it
            imgFotoGrupo.apply {
                setImageURI(it)
                setPadding(0, 0, 0, 0)
                scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(this, getSharedPreferences(Constantes.PREF_OSMDROID, MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = Constantes.USER_AGENT
        setContentView(R.layout.activity_nueva_actividad)

        configurarToolbar()

etUbicacion = findViewById(R.id.et_ubicacion)
        imgFotoGrupo = findViewById(R.id.img_foto_grupo)
        etFechaActividad = findViewById(R.id.et_fecha_actividad)

        configurarMapa()
        configurarSeleccionFoto()
        configurarBusquedaDireccion()
        configurarSeleccionFecha()

        findViewById<MaterialButton>(R.id.btn_guardar).setOnClickListener {
            guardarActividad()
        }

        restaurarEstado(savedInstanceState)
        if (savedInstanceState == null) {
            centrarEnDispositivoSiHayPermiso()
        }
    }

    override fun onPause() {
        super.onPause()
        mapa.onPause()
    }

    override fun onResume() {
        super.onResume()
        mapa.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mapa.isInitialized) {
            mapa.onDetach()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EXTRA_FOTO, fotoUri?.toString())
        outState.putDouble(EXTRA_LAT, latSeleccionada ?: -1.0)
        outState.putDouble(EXTRA_LON, lonSeleccionada ?: -1.0)
        outState.putString(EXTRA_UBICACION, etUbicacion.text.toString())
        outState.putLong(EXTRA_FECHA, fechaActividadSeleccionada ?: -1L)
    }

    private fun restaurarEstado(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            return
        }
        savedInstanceState.getString(EXTRA_FOTO)?.let { texto ->
            val uri = texto.toUri()
            fotoUri = uri
            imgFotoGrupo.apply {
                setImageURI(uri)
                setPadding(0, 0, 0, 0)
                scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
            }
        }
        val lat = savedInstanceState.getDouble(EXTRA_LAT, -1.0)
        val lon = savedInstanceState.getDouble(EXTRA_LON, -1.0)
        if (lat != -1.0 && lon != -1.0) {
            latSeleccionada = lat
            lonSeleccionada = lon
            colocarMarcador(lat, lon)
        }
        etUbicacion.setText(
            savedInstanceState.getString(EXTRA_UBICACION).orEmpty()
        )
        val fecha = savedInstanceState.getLong(EXTRA_FECHA, -1L)
        if (fecha != -1L) {
            fechaActividadSeleccionada = fecha
            etFechaActividad.setText(FechaFormatos.fechaHora(fecha))
        }
    }

    private fun configurarMapa() {
        mapa = MapView(this)
        findViewById<FrameLayout>(R.id.contenedor_mapa_direccion).apply {
            addView(mapa, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            ))
        }
        mapa.setTileSource(FuenteDeMapa.PRINCIPAL)
        mapa.controller.setZoom(15.0)
        mapa.controller.setCenter(GeoPoint(Constantes.LAT_DEFAULT, Constantes.LON_DEFAULT))

        val receptor = object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                p?.let {
                    latSeleccionada = it.latitude
                    lonSeleccionada = it.longitude
                    colocarMarcador(it.latitude, it.longitude)
                    buscarNombreUbicacion(it.latitude, it.longitude)
                }
                return true
            }

            override fun longPressHelper(p: GeoPoint?): Boolean = false
        }
        mapa.overlays.add(MapEventsOverlay(receptor))
    }

    private fun configurarBusquedaDireccion() {
        etUbicacion.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                buscarDireccion()
                true
            } else {
                false
            }
        }
        findViewById<TextInputLayout>(R.id.til_ubicacion).setEndIconOnClickListener {
            buscarDireccion()
        }
    }

    private fun centrarEnDispositivoSiHayPermiso() {
        val permisoOk = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (permisoOk) {
            UbicacionHelper.ultimaUbicacion(this)?.let { ubicacion ->
                mapa.controller.animateTo(GeoPoint(ubicacion.latitude, ubicacion.longitude))
            }
        }
    }

    private fun buscarDireccion() {
        val texto = etUbicacion.text.toString().trim()
        if (texto.isEmpty()) {
            mostrarError(getString(R.string.nueva_error_direccion_vacia))
            return
        }
        ocultarTeclado()

        val viewbox = UbicacionHelper.ultimaUbicacion(this)?.let { ubicacion ->
            val margen = 0.5
            "${ubicacion.longitude - margen}," +
                "${ubicacion.latitude - margen}," +
                "${ubicacion.longitude + margen}," +
                "${ubicacion.latitude + margen}"
        }
        val paises = if (viewbox == null) {
            PaisPorNacionalidad.codigoPais(graph.perfilRepository.nacionalidad())
        } else {
            null
        }

        lifecycleScope.launch {
            val resultado = withContext(Dispatchers.IO) {
                graph.actividadRepository.buscarDireccion(texto, viewbox = viewbox, paises = paises)
            }
            val lat = resultado?.lat?.toDoubleOrNull()
            val lon = resultado?.lon?.toDoubleOrNull()
            if (lat != null && lon != null) {
                latSeleccionada = lat
                lonSeleccionada = lon
                mapa.controller.animateTo(GeoPoint(lat, lon))
                colocarMarcador(lat, lon)
                etUbicacion.setText(
                    resultado.display_name.orEmpty()
                )
            } else {
                mostrarError(getString(R.string.nueva_error_direccion))
            }
        }
    }

    private fun ocultarTeclado() {
        getSystemService(InputMethodManager::class.java)?.apply {
            hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }

    private fun colocarMarcador(lat: Double, lon: Double) {
        marcadorUbicacion?.let { mapa.overlays.remove(it) }
        val nuevo = Marker(mapa).apply {
            position = GeoPoint(lat, lon)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            icon = androidx.core.content.ContextCompat.getDrawable(
                this@NuevaActividadActivity,
                R.drawable.ic_marker_local
            )
        }
        marcadorUbicacion = nuevo
        mapa.overlays.add(nuevo)
        mapa.invalidate()
    }

    private fun buscarNombreUbicacion(lat: Double, lon: Double) {
        lifecycleScope.launch {
            val nombre = withContext(Dispatchers.IO) {
                graph.actividadRepository.nombreUbicacion(lat, lon)
            }
            etUbicacion.setText(
                nombre.ifBlank { getString(R.string.nueva_ubicacion_sin_nombre) }
            )
        }
    }

    private fun configurarSeleccionFoto() {
        val abrirGaleria = { selectorFoto.launch("image/*") }
        imgFotoGrupo.setOnClickListener { abrirGaleria() }
        findViewById<MaterialButton>(R.id.btn_agregar_foto).setOnClickListener { abrirGaleria() }
    }

    private fun configurarSeleccionFecha() {
        etFechaActividad.setOnClickListener {
            val calendario = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    TimePickerDialog(
                        this,
                        { _, hora, minuto ->
                            val fecha = Calendar.getInstance().apply {
                                set(year, month, day, hora, minuto, 0)
                                set(Calendar.MILLISECOND, 0)
                            }
                            fechaActividadSeleccionada = fecha.timeInMillis
                            etFechaActividad.setText(FechaFormatos.fechaHora(fecha.timeInMillis))
                        },
                        calendario.get(Calendar.HOUR_OF_DAY),
                        calendario.get(Calendar.MINUTE),
                        true
                    ).show()
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun guardarActividad() {
        if (guardando) {
            return
        }
        val nombre = findViewById<TextInputEditText>(R.id.et_nombre).text.toString().trim()
        val descripcion = findViewById<TextInputEditText>(R.id.et_descripcion).text.toString().trim()
        val duracionTexto = findViewById<TextInputEditText>(R.id.et_duracion).text.toString().trim()
        val duracionMin = duracionTexto.toIntOrNull() ?: -1
        val lat = latSeleccionada
        val lon = lonSeleccionada
        val foto = fotoUri
        val fechaActividad = fechaActividadSeleccionada

        when {
            nombre.isBlank() -> mostrarError(getString(R.string.nueva_error_nombre))
            nombre.length < 2 -> mostrarError(getString(R.string.nueva_error_nombre_corto))
            lat == null || lon == null -> mostrarError(getString(R.string.nueva_error_ubicacion))
            descripcion.length < 10 -> mostrarError(getString(R.string.nueva_error_descripcion))
            duracionMin <= 0 -> mostrarError(getString(R.string.nueva_error_duracion))
            fechaActividad == null -> mostrarError(getString(R.string.nueva_error_fecha))
            fechaActividad <= System.currentTimeMillis() ->
                mostrarError(getString(R.string.nueva_error_fecha_pasada))
            else -> ejecutarGuardado(nombre, descripcion, duracionMin, lat, lon, foto, fechaActividad)
        }
    }

    private fun ejecutarGuardado(
        nombre: String,
        descripcion: String,
        duracionMin: Int,
        lat: Double,
        lon: Double,
        foto: Uri?,
        fechaActividad: Long
    ) {
        guardando = true
        lifecycleScope.launch {
            val rutaFoto = foto?.let { ruta ->
                withContext(Dispatchers.IO) {
                    FotoUtil.guardarFoto(this@NuevaActividadActivity, ruta)
                }
            }
            val ubicacion = etUbicacion.text.toString()
            val actividad = Actividad(
                id = UUID.randomUUID().toString(),
                nombre = nombre,
                descripcion = descripcion,
                ubicacion = ubicacion,
                lat = lat,
                lon = lon,
                creadoPor = graph.perfilRepository.perfilActual()
                    ?.nombre?.takeIf { it.isNotBlank() }
                    ?: getString(R.string.nueva_creador_tu),
                duracionMin = duracionMin,
                fechaCreacionLong = System.currentTimeMillis(),
                fechaActividadLong = fechaActividad,
                fotoRuta = rutaFoto,
                origen = Constantes.ORIGEN_LOCAL
            )
            withContext(Dispatchers.IO) {
                graph.actividadRepository.guardarNueva(actividad)
            }
            guardando = false
            Toast.makeText(this@NuevaActividadActivity, getString(R.string.nueva_guardada), Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        navegarAtras()
        return true
    }

    companion object {
        private const val EXTRA_FOTO = "extra_foto"
        private const val EXTRA_LAT = "extra_lat"
        private const val EXTRA_LON = "extra_lon"
        private const val EXTRA_UBICACION = "extra_ubicacion"
        private const val EXTRA_FECHA = "extra_fecha"
    }
}