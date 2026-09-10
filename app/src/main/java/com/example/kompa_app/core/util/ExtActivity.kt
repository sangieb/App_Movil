package com.example.kompa_app.core.util

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kompa_app.R
import com.google.android.material.appbar.MaterialToolbar

fun AppCompatActivity.configurarToolbar(
    toolbarId: Int = R.id.toolbar,
    mostrarTitulo: Boolean = true
) {
    val toolbar = findViewById<MaterialToolbar>(toolbarId)
    setSupportActionBar(toolbar)
    supportActionBar?.setDisplayHomeAsUpEnabled(true)
    if (!mostrarTitulo) supportActionBar?.setDisplayShowTitleEnabled(false)
}

fun AppCompatActivity.mostrarError(mensaje: String): Boolean {
    Toast.makeText(this, mensaje, Toast.LENGTH_SHORT).show()
    return false
}

fun AppCompatActivity.navegarAtras() {
    onBackPressedDispatcher.onBackPressed()
}
