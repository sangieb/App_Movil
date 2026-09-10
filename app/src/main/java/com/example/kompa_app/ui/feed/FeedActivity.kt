package com.example.kompa_app.ui.feed

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kompa_app.KompaApplication
import com.example.kompa_app.R
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class FeedActivity : AppCompatActivity() {

    private val graph by lazy { (application as KompaApplication).graph }
    private val viewModel: FeedViewModel by viewModels {
        FeedViewModelFactory(graph.publicacionRepository)
    }
    private val adapter = FeedAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        findViewById<RecyclerView>(R.id.rv_feed).apply {
            layoutManager = LinearLayoutManager(this@FeedActivity)
            adapter = this@FeedActivity.adapter
        }

        findViewById<MaterialButton>(R.id.btn_reintentar).setOnClickListener {
            viewModel.cargar()
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.estado.collect { estado -> renderizar(estado) }
            }
        }

        viewModel.cargar()
    }

    private fun renderizar(estado: FeedUiState) {
        val rv = findViewById<RecyclerView>(R.id.rv_feed)
        val vacio = findViewById<TextView>(R.id.tv_vacio)
        val error = findViewById<TextView>(R.id.tv_error)
        val btnReintentar = findViewById<MaterialButton>(R.id.btn_reintentar)
        val cargando = findViewById<ProgressBar>(R.id.pb_cargando)

        when (estado) {
            FeedUiState.Cargando -> {
                cargando.visibility = View.VISIBLE
                rv.visibility = View.GONE
                vacio.visibility = View.GONE
                error.visibility = View.GONE
                btnReintentar.visibility = View.GONE
            }
            is FeedUiState.Exito -> {
                cargando.visibility = View.GONE
                error.visibility = View.GONE
                btnReintentar.visibility = View.GONE
                adapter.submitList(estado.publicaciones)
                vacio.visibility = if (estado.publicaciones.isEmpty()) View.VISIBLE else View.GONE
                rv.visibility = if (estado.publicaciones.isEmpty()) View.GONE else View.VISIBLE
            }
            FeedUiState.Error -> {
                cargando.visibility = View.GONE
                rv.visibility = View.GONE
                vacio.visibility = View.GONE
                error.visibility = View.VISIBLE
                btnReintentar.visibility = View.VISIBLE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}