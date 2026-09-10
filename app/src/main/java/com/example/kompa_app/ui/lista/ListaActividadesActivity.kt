package com.example.kompa_app.ui.lista

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kompa_app.Constantes
import com.example.kompa_app.KompaApplication
import com.example.kompa_app.R
import com.example.kompa_app.core.util.configurarToolbar
import com.example.kompa_app.core.util.navegarAtras
import com.example.kompa_app.data.Actividad
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.launch

class ListaActividadesActivity : AppCompatActivity() {

    private val graph by lazy { (application as KompaApplication).graph }
    private val viewModel: CatalogViewModel by viewModels {
        CatalogViewModelFactory(graph.actividadRepository)
    }
    private val adapter = ActividadAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_actividades)

        configurarToolbar()

        findViewById<RecyclerView>(R.id.rv_actividades).apply {
            layoutManager = LinearLayoutManager(this@ListaActividadesActivity)
            setHasFixedSize(true)
            adapter = this@ListaActividadesActivity.adapter
        }

        findViewById<SearchView>(R.id.sv_buscar).setOnQueryTextListener(
            object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    aplicarFiltros()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    aplicarFiltros()
                    return true
                }
            }
        )

        findViewById<ChipGroup>(R.id.cg_origen).setOnCheckedStateChangeListener { _, _ ->
            aplicarFiltros()
        }

        findViewById<ChipGroup>(R.id.cg_duracion).setOnCheckedStateChangeListener { _, _ ->
            aplicarFiltros()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.resultados.collect { lista ->
                    adapter.submitList(lista)
                    actualizarVacio(lista)
                }
            }
        }
    }

    private fun aplicarFiltros() {
        val texto = findViewById<SearchView>(R.id.sv_buscar).query?.toString()?.trim().orEmpty()

        val origen = when (findViewById<ChipGroup>(R.id.cg_origen).checkedChipId) {
            R.id.chip_origen_locales -> Constantes.ORIGEN_LOCAL
            R.id.chip_origen_api -> Constantes.ORIGEN_API
            else -> null
        }

        val (duracionMinima, duracionMaxima) =
            when (findViewById<ChipGroup>(R.id.cg_duracion).checkedChipId) {
                R.id.chip_duracion_corta -> 0 to 60
                R.id.chip_duracion_media -> 60 to 180
                R.id.chip_duracion_larga -> 180 to null
                else -> null to null
            }

        viewModel.aplicarFiltros(
            FiltrosCatalogo(
                texto = texto,
                origen = origen,
                duracionMinima = duracionMinima,
                duracionMaxima = duracionMaxima
            )
        )
    }

    private fun actualizarVacio(lista: List<Actividad>) {
        findViewById<TextView>(R.id.tv_vacio).visibility =
            if (lista.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        navegarAtras()
        return true
    }
}