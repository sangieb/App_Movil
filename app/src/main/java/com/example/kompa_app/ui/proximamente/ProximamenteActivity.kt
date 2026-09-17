package com.example.kompa_app.ui.proximamente

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kompa_app.R

class ProximamenteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proximamente)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }
}