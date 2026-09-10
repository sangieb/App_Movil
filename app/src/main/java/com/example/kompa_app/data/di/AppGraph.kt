package com.example.kompa_app.data.di

import android.content.Context
import com.example.kompa_app.data.ActividadRepository
import com.example.kompa_app.data.PerfilRepository
import com.example.kompa_app.data.PerfilStore
import com.example.kompa_app.data.PublicacionRepository
import com.example.kompa_app.data.cuenta.CuentaStore
import com.example.kompa_app.data.db.KompaDbHelper
import com.example.kompa_app.data.network.ApiClient
import com.example.kompa_app.data.session.AuthRepository
import com.example.kompa_app.data.session.TokenStore

class AppGraph(context: Context) {

    private val appContext = context.applicationContext

    val tokenStore: TokenStore by lazy { TokenStore(appContext) }

    val perfilStore: PerfilStore by lazy { PerfilStore(appContext) }

    val dbHelper: KompaDbHelper by lazy { KompaDbHelper(appContext) }

    val cuentaStore: CuentaStore by lazy { CuentaStore(dbHelper) }

    val authRepository: AuthRepository by lazy { AuthRepository(tokenStore, cuentaStore) }

    val perfilRepository: PerfilRepository by lazy { PerfilRepository(perfilStore) }

    val apiClient: ApiClient by lazy { ApiClient(authRepository) }

    val publicacionRepository: PublicacionRepository by lazy { PublicacionRepository(apiClient) }

    val actividadRepository: ActividadRepository by lazy { ActividadRepository(dbHelper, apiClient) }
}