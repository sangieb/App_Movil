package com.example.kompa_app.data

import com.example.kompa_app.Constantes

object ActividadesEjemplo {

    fun lista(): List<Actividad> = listOf(
        Actividad(
            id = "ejemplo_monserrate",
            nombre = "Caminata a Monserrate",
            descripcion = "Subida al cerro con vista panorámica de Bogotá",
            ubicacion = "Monserrate, Bogotá",
            lat = 4.6058,
            lon = -74.0562,
            creadoPor = "Ejemplo",
            duracionMin = 180,
            fechaCreacionLong = 1735689600000L,
            fotoRuta = null,
            origen = Constantes.ORIGEN_LOCAL
        ),
        Actividad(
            id = "ejemplo_museo_oro",
            nombre = "Museo del Oro",
            descripcion = "Colección de orfebrería precolombina del Banco de la República",
            ubicacion = "Centro, Bogotá",
            lat = 4.6018,
            lon = -74.0720,
            creadoPor = "Ejemplo",
            duracionMin = 120,
            fechaCreacionLong = 1735776000000L,
            fotoRuta = null,
            origen = Constantes.ORIGEN_LOCAL
        ),
        Actividad(
            id = "ejemplo_plaza_bolivar",
            nombre = "Recorrido por la Plaza de Bolívar",
            descripcion = "Centro histórico con la Catedral Primada y el Capitolio",
            ubicacion = "La Candelaria, Bogotá",
            lat = 4.5979,
            lon = -74.0761,
            creadoPor = "Ejemplo",
            duracionMin = 90,
            fechaCreacionLong = 1735862400000L,
            fotoRuta = null,
            origen = Constantes.ORIGEN_LOCAL
        ),
        Actividad(
            id = "ejemplo_simon_bolivar",
            nombre = "Picnic en el Parque Simón Bolívar",
            descripcion = "Zona verde para caminar, correr o andar en bicicleta",
            ubicacion = "Barrios Unidos, Bogotá",
            lat = 4.6540,
            lon = -74.0830,
            creadoPor = "Ejemplo",
            duracionMin = 150,
            fechaCreacionLong = 1735948800000L,
            fotoRuta = null,
            origen = Constantes.ORIGEN_LOCAL
        ),
        Actividad(
            id = "ejemplo_jardin_botanico",
            nombre = "Visita al Jardín Botánico",
            descripcion = "Invernaderos y colecciones de flora de páramo y selva",
            ubicacion = "Engativá, Bogotá",
            lat = 4.6715,
            lon = -74.0964,
            creadoPor = "Ejemplo",
            duracionMin = 120,
            fechaCreacionLong = 1736035200000L,
            fotoRuta = null,
            origen = Constantes.ORIGEN_LOCAL
        ),
        Actividad(
            id = "ejemplo_usaquen",
            nombre = "Mercado de Usaquén",
            descripcion = "Plaza colonial con artesanías, comida y música en vivo",
            ubicacion = "Usaquén, Bogotá",
            lat = 4.6978,
            lon = -74.0441,
            creadoPor = "Ejemplo",
            duracionMin = 120,
            fechaCreacionLong = 1736121600000L,
            fotoRuta = null,
            origen = Constantes.ORIGEN_LOCAL
        )
    )
}