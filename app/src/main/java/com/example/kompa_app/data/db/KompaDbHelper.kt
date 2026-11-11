package com.example.kompa_app.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.kompa_app.data.actividad.Actividad
import com.example.kompa_app.data.auth.cuenta.Cuenta
import com.example.kompa_app.data.network.CatalogoDto
import com.example.kompa_app.data.publicacion.Publicacion

@Entity(tableName = "actividades")
data class ActividadEntity(
    @PrimaryKey val id: String,
    val nombre: String,
    val descripcion: String,
    val ubicacion: String,
    val lat: Double,
    val lon: Double,
    @ColumnInfo(name = "creado_por") val creadoPor: String,
    @ColumnInfo(name = "duracion_min") val duracionMin: Int?,
    @ColumnInfo(name = "fecha_creacion") val fechaCreacion: Long?,
    @ColumnInfo(name = "fecha_actividad") val fechaActividad: Long?,
    @ColumnInfo(name = "foto_ruta") val fotoRuta: String?,
    val origen: String
)

@Entity(tableName = "cuentas")
data class CuentaEntity(
    @PrimaryKey val correo: String,
    val sal: String,
    val hash: String
)

@Entity(tableName = "catalogos", primaryKeys = ["tipo", "valor"])
data class CatalogoEntity(
    val tipo: String,
    val valor: String,
    @ColumnInfo(defaultValue = "0") val orden: Int = 0
)

@Entity(tableName = "publicaciones")
data class PublicacionEntity(
    @PrimaryKey val id: String,
    val titulo: String,
    val cuerpo: String,
    val autor: String,
    @ColumnInfo(name = "fecha_creacion") val fechaCreacion: Long?,
    val lat: Double?,
    val lon: Double?
)

// ---- Conversión dominio <-> entidad ----

fun Actividad.aEntidad() = ActividadEntity(
    id = id, nombre = nombre, descripcion = descripcion, ubicacion = ubicacion,
    lat = lat, lon = lon, creadoPor = creadoPor, duracionMin = duracionMin,
    fechaCreacion = fechaCreacionLong, fechaActividad = fechaActividadLong,
    fotoRuta = fotoRuta, origen = origen
)

fun ActividadEntity.aDominio() = Actividad(
    id = id, nombre = nombre, descripcion = descripcion, ubicacion = ubicacion,
    lat = lat, lon = lon, creadoPor = creadoPor, duracionMin = duracionMin,
    fechaCreacionLong = fechaCreacion, fechaActividadLong = fechaActividad,
    fotoRuta = fotoRuta, origen = origen
)

fun Cuenta.aEntidad() = CuentaEntity(correo = correo, sal = sal, hash = hash)

fun CuentaEntity.aDominio() = Cuenta(correo = correo, sal = sal, hash = hash)

fun Publicacion.aEntidad() = PublicacionEntity(
    id = id, titulo = titulo, cuerpo = cuerpo, autor = autor,
    fechaCreacion = fechaCreacionLong, lat = lat, lon = lon
)

fun PublicacionEntity.aDominio() = Publicacion(
    id = id, titulo = titulo, cuerpo = cuerpo, autor = autor,
    fechaCreacionLong = fechaCreacion, lat = lat, lon = lon
)

/** Descarta filas sin tipo o valor (antes SQLite las rechazaba en silencio por NOT NULL). */
fun CatalogoDto.aEntidadOrNull(): CatalogoEntity? {
    val t = tipo ?: return null
    val v = valor ?: return null
    return CatalogoEntity(tipo = t, valor = v, orden = orden ?: 0)
}