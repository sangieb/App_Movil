package com.example.kompa_app.core.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.content.ContextCompat

object UbicacionHelper {

    fun ultimaUbicacion(context: Context): Location? {
        val permisoOk = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (!permisoOk) {
            return null
        }
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val proveedores = listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
        return proveedores
            .mapNotNull { manager.getLastKnownLocation(it) }
            .maxByOrNull { it.time }
    }
}