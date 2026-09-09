package com.example.kompa_app.ui.lista

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kompa_app.R
import com.example.kompa_app.data.Actividad
import com.example.kompa_app.data.ActividadRepository
import com.example.kompa_app.data.ActividadStore
import com.google.android.material.appbar.MaterialToolbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ListaActividadesActivity : AppCompatActivity() {

    private val repositorio by lazy {
        ActividadRepository(ActividadStore(applicationContext))
    }
    private val adapter = ActividadAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_actividades)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        findViewById<RecyclerView>(R.id.rv_actividades).apply {
            layoutManager = LinearLayoutManager(this@ListaActividadesActivity)
            setHasFixedSize(true)
            adapter = this@ListaActividadesActivity.adapter
        }

        cargarDatos()
    }

    private fun cargarDatos() {
        lifecycleScope.launch {
            val locales = withContext(Dispatchers.IO) { repositorio.obtenerLocales() }
            adapter.submitList(locales)
            actualizarVacio(locales)
        }
    }

    private fun actualizarVacio(lista: List<Actividad>) {
        findViewById<TextView>(R.id.tv_vacio).visibility =
            if (lista.isEmpty()) View.VISIBLE else View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}