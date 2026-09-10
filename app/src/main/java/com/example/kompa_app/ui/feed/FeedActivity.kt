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
import com.example.kompa_app.core.util.configurarToolbar
import com.example.kompa_app.core.util.navegarAtras
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class FeedActivity : AppCompatActivity() {

    private val graph by lazy { (application as KompaApplication).graph }
    private val viewModel: FeedViewModel by viewModels {
        FeedViewModelFactory(graph.publicacionRepository)
    }
    private val adapter = FeedAdapter()

    private lateinit var rvFeed: RecyclerView
    private lateinit var tvVacio: TextView
    private lateinit var tvError: TextView
    private lateinit var btnReintentar: MaterialButton
    private lateinit var pbCargando: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        configurarToolbar()

        rvFeed = findViewById<RecyclerView>(R.id.rv_feed).apply {
            layoutManager = LinearLayoutManager(this@FeedActivity)
            adapter = this@FeedActivity.adapter
        }
        tvVacio = findViewById(R.id.tv_vacio)
        tvError = findViewById(R.id.tv_error)
        btnReintentar = findViewById(R.id.btn_reintentar)
        pbCargando = findViewById(R.id.pb_cargando)

        btnReintentar.setOnClickListener {
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
        when (estado) {
            FeedUiState.Cargando -> {
                pbCargando.visibility = View.VISIBLE
                rvFeed.visibility = View.GONE
                tvVacio.visibility = View.GONE
                tvError.visibility = View.GONE
                btnReintentar.visibility = View.GONE
            }
            is FeedUiState.Exito -> {
                pbCargando.visibility = View.GONE
                tvError.visibility = View.GONE
                btnReintentar.visibility = View.GONE
                adapter.submitList(estado.publicaciones)
                tvVacio.visibility = if (estado.publicaciones.isEmpty()) View.VISIBLE else View.GONE
                rvFeed.visibility = if (estado.publicaciones.isEmpty()) View.GONE else View.VISIBLE
            }
            FeedUiState.Error -> {
                pbCargando.visibility = View.GONE
                rvFeed.visibility = View.GONE
                tvVacio.visibility = View.GONE
                tvError.visibility = View.VISIBLE
                btnReintentar.visibility = View.VISIBLE
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        navegarAtras()
        return true
    }
}