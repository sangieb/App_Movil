package com.example.kompa_app.util

import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
import org.osmdroid.util.MapTileIndex

object FuenteDeMapa {

    private const val URL_BASE =
        "https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/"

    val PRINCIPAL: OnlineTileSourceBase = object : OnlineTileSourceBase(
        "EsriWorldStreetMap",
        1,
        19,
        256,
        "",
        arrayOf(URL_BASE)
    ) {
        override fun getTileURLString(indice: Long): String {
            val zoom = MapTileIndex.getZoom(indice)
            val x = MapTileIndex.getX(indice)
            val y = MapTileIndex.getY(indice)
            return "${URL_BASE}${zoom}/${y}/${x}"
        }
    }
}