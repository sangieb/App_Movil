package com.example.kompa_app.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.kompa_app.Constantes
import com.example.kompa_app.KompaApplication
import com.example.kompa_app.R
import com.example.kompa_app.data.Actividad
import com.example.kompa_app.ui.inicio.InicioActivity
import com.example.kompa_app.ui.lista.ListaActividadesActivity
import com.example.kompa_app.ui.nueva.NuevaActividadActivity
import com.example.kompa_app.ui.feed.FeedActivity
import com.example.kompa_app.core.util.FuenteDeMapa
import com.example.kompa_app.core.util.UbicacionHelper
import kotlinx.coroutines.launch
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay

class HomeActivity : AppCompatActivity() {

    private val graph by lazy { (application as KompaApplication).graph }
    private val viewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(graph.actividadRepository)
    }

    private lateinit var mapa: MapView
    private var latActual = Constantes.LAT_DEFAULT
    private var lonActual = Constantes.LON_DEFAULT

    private val permisoUbicacion = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) {
            centrarEnMiUbicacion()
        } else {
            Toast.makeText(this, getString(R.string.home_permiso_negado), Toast.LENGTH_SHORT).show()
            viewModel.cargar()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(this, getSharedPreferences(Constantes.PREF_OSMDROID, MODE_PRIVATE))
        Configuration.getInstance().userAgentValue = Constantes.USER_AGENT
        setContentView(R.layout.activity_home)

        mapa = MapView(this)
        findViewById<FrameLayout>(R.id.contenedor_mapa).apply {
            addView(mapa, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            ))
        }
        mapa.setTileSource(FuenteDeMapa.PRINCIPAL)
        mapa.controller.setZoom(14.0)

        findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_show_list)
            .setOnClickListener {
                startActivity(Intent(this, ListaActividadesActivity::class.java))
            }

        findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_show_feed)
            .setOnClickListener {
                startActivity(Intent(this, FeedActivity::class.java))
            }

        findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_logout)
            .setOnClickListener {
                graph.authRepository.logout()
                startActivity(
                    Intent(this, InicioActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                )
            }

        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab_agregar)
            .setOnClickListener {
                startActivity(Intent(this, NuevaActividadActivity::class.java))
            }

        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab_centrar)
            .setOnClickListener { solicitarUbicacion() }

        lifecycleScope.launch {
            viewModel.actividades.collect { lista ->
                pintarMarcadores(lista)
            }
        }

        solicitarUbicacion()
    }

    override fun onResume() {
        super.onResume()
        mapa.onResume()
        viewModel.cargar()
    }

    override fun onPause() {
        super.onPause()
        mapa.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::mapa.isInitialized) {
            mapa.onDetach()
        }
    }

    private fun solicitarUbicacion() {
        val permisoOk = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (permisoOk) {
            centrarEnMiUbicacion()
        } else {
            permisoUbicacion.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    private fun centrarEnMiUbicacion() {
        UbicacionHelper.ultimaUbicacion(this)?.let { ubicacion ->
            latActual = ubicacion.latitude
            lonActual = ubicacion.longitude
        }
        mapa.controller.animateTo(GeoPoint(latActual, lonActual))
        viewModel.cargar()
    }

    private fun pintarMarcadores(lista: List<Actividad>) {
        val aEliminar = ArrayList<Overlay>()
        mapa.overlays.forEach { if (it is Marker) aEliminar.add(it) }
        mapa.overlays.removeAll(aEliminar)

        lista
            .filter { it.lat != 0.0 || it.lon != 0.0 }
            .forEach { actividad ->
                val marcador = Marker(mapa).apply {
                    position = GeoPoint(actividad.lat, actividad.lon)
                    title = actividad.nombre
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    icon = ContextCompat.getDrawable(
                        this@HomeActivity,
                        if (actividad.origen == "local") R.drawable.ic_marker_local else R.drawable.ic_marker_api
                    )
                }
                mapa.overlays.add(marcador)
            }
        mapa.invalidate()
    }
}