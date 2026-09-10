package com.example.kompa_app

import android.app.Application
import com.example.kompa_app.data.di.AppGraph

class KompaApplication : Application() {

    val graph: AppGraph by lazy { AppGraph(this) }
}