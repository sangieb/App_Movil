package com.example.kompa_app.data.catalogo

import com.example.kompa_app.data.network.CatalogoDto

object CatalogosSemilla {

    fun lista(): List<CatalogoDto> =
        listOf(
            "Senderismo", "Playa", "Mochilero", "Gastronomía", "Fotografía",
            "Vida nocturna", "Museos y cultura", "Deportes extremos", "Yoga",
            "Café y trabajo remoto", "Compras", "Naturaleza", "Fiestas locales",
            "Buceo", "Road trips"
        ).mapIndexed { indice, valor ->
            CatalogoDto(tipo = CatalogoTipos.INTERES, valor = valor, orden = indice)
        } + listOf(
            "Mexicana/o", "Colombiana/o", "Argentina/o", "Chilena/o", "Peruana/o",
            "Española/o", "Estadounidense", "Brasileña/o", "Ecuatoriana/o", "Otro/Otra"
        ).mapIndexed { indice, valor ->
            CatalogoDto(tipo = CatalogoTipos.NACIONALIDAD, valor = valor, orden = indice)
        } + listOf(
            "Español", "Inglés", "Portugués", "Francés", "Alemán", "Italiano", "Otro"
        ).mapIndexed { indice, valor ->
            CatalogoDto(tipo = CatalogoTipos.IDIOMA, valor = valor, orden = indice)
        }
}