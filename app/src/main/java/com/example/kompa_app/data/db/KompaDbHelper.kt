package com.example.kompa_app.data.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.kompa_app.data.Actividad
import com.example.kompa_app.data.ActividadesEjemplo
import com.example.kompa_app.data.cuenta.Cuenta

class KompaDbHelper(context: Context) : SQLiteOpenHelper(
    context,
    NOMBRE_DB,
    null,
    VERSION_DB
) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(DDL_ACTIVIDADES)
        db.execSQL(DDL_CUENTAS)
        insertarActividades(db, ActividadesEjemplo.lista())
    }

    override fun onUpgrade(db: SQLiteDatabase, versionAntigua: Int, versionNueva: Int) {
        if (versionAntigua < 2) {
            db.execSQL(DDL_CUENTAS)
        }
    }

    fun insertarCuenta(cuenta: Cuenta) {
        synchronized(this) {
            writableDatabase.insertWithOnConflict(TABLA_CUENTAS, null, ContentValues().apply {
                put(COL_CORREO, cuenta.correo)
                put(COL_SAL, cuenta.sal)
                put(COL_HASH, cuenta.hash)
            }, SQLiteDatabase.CONFLICT_REPLACE)
        }
    }

    fun consultarCuenta(correo: String): Cuenta? =
        readableDatabase.query(
            TABLA_CUENTAS,
            null,
            "$COL_CORREO = ?",
            arrayOf(correo),
            null,
            null,
            null
        ).use { cursor ->
            if (cursor.moveToFirst()) {
                Cuenta(
                    correo = cursor.getString(cursor.getColumnIndexOrThrow(COL_CORREO)),
                    sal = cursor.getString(cursor.getColumnIndexOrThrow(COL_SAL)),
                    hash = cursor.getString(cursor.getColumnIndexOrThrow(COL_HASH))
                )
            } else {
                null
            }
        }

    fun insertarActividad(actividad: Actividad) {
        synchronized(this) {
            writableDatabase.insertWithOnConflict(TABLA_ACTIVIDADES, null, actividad.aContentValues(),
                SQLiteDatabase.CONFLICT_REPLACE)
        }
    }

    fun consultarActividades(
        texto: String = "",
        origen: String? = null,
        duracionMinima: Int? = null,
        duracionMaxima: Int? = null
    ): List<Actividad> {
        val seleccion = StringBuilder()
        val argumentos = ArrayList<String>()
        if (texto.isNotBlank()) {
            seleccion.append("(${COL_NOMBRE} LIKE ? OR ${COL_UBICACION} LIKE ?)")
            val patron = "%${texto.trim()}%"
            argumentos.add(patron)
            argumentos.add(patron)
        }
        origen?.let {
            if (seleccion.isNotEmpty()) seleccion.append(" AND ")
            seleccion.append("$COL_ORIGEN = ?")
            argumentos.add(it)
        }
        duracionMinima?.let {
            if (seleccion.isNotEmpty()) seleccion.append(" AND ")
            seleccion.append("$COL_DURACION >= ?")
            argumentos.add(it.toString())
        }
        duracionMaxima?.let {
            if (seleccion.isNotEmpty()) seleccion.append(" AND ")
            seleccion.append("$COL_DURACION <= ?")
            argumentos.add(it.toString())
        }
        return readableDatabase.query(
            TABLA_ACTIVIDADES,
            null,
            seleccion.toString().takeIf { it.isNotEmpty() },
            argumentos.toTypedArray().takeIf { it.isNotEmpty() },
            null,
            null,
            "$COL_FECHA_CREACION DESC, $COL_NOMBRE ASC"
        ).use { cursor -> cursor.aListaActividades() }
    }

    private fun Actividad.aContentValues(): ContentValues = ContentValues().apply {
        put(COL_ID, id)
        put(COL_NOMBRE, nombre)
        put(COL_DESCRIPCION, descripcion)
        put(COL_UBICACION, ubicacion)
        put(COL_LAT, lat)
        put(COL_LON, lon)
        put(COL_CREADO_POR, creadoPor)
        duracionMin?.let { put(COL_DURACION, it) }
        fechaCreacionLong?.let { put(COL_FECHA_CREACION, it) }
        fechaActividadLong?.let { put(COL_FECHA_ACTIVIDAD, it) }
        fotoRuta?.let { put(COL_FOTO, it) }
        put(COL_ORIGEN, origen)
    }

    private fun Cursor.aListaActividades(): List<Actividad> {
        val resultado = ArrayList<Actividad>(count)
        while (moveToNext()) {
            resultado.add(
                Actividad(
                    id = getString(getColumnIndexOrThrow(COL_ID)),
                    nombre = getString(getColumnIndexOrThrow(COL_NOMBRE)),
                    descripcion = getString(getColumnIndexOrThrow(COL_DESCRIPCION)),
                    ubicacion = getString(getColumnIndexOrThrow(COL_UBICACION)),
                    lat = getDouble(getColumnIndexOrThrow(COL_LAT)),
                    lon = getDouble(getColumnIndexOrThrow(COL_LON)),
                    creadoPor = getString(getColumnIndexOrThrow(COL_CREADO_POR)),
                    duracionMin = leerEntero(COL_DURACION),
                    fechaCreacionLong = leerLong(COL_FECHA_CREACION),
                    fechaActividadLong = leerLong(COL_FECHA_ACTIVIDAD),
                    fotoRuta = leerTexto(COL_FOTO),
                    origen = getString(getColumnIndexOrThrow(COL_ORIGEN))
                )
            )
        }
        return resultado
    }

    private fun Cursor.leerTexto(columna: String): String? =
        if (isNull(getColumnIndexOrThrow(columna))) null else getString(getColumnIndexOrThrow(columna))

    private fun Cursor.leerEntero(columna: String): Int? =
        if (isNull(getColumnIndexOrThrow(columna))) null else getInt(getColumnIndexOrThrow(columna))

    private fun Cursor.leerLong(columna: String): Long? =
        if (isNull(getColumnIndexOrThrow(columna))) null else getLong(getColumnIndexOrThrow(columna))

    private fun insertarActividades(db: SQLiteDatabase, actividades: List<Actividad>) {
        db.beginTransaction()
        try {
            actividades.forEach { actividad ->
                db.insertWithOnConflict(TABLA_ACTIVIDADES, null, actividad.aContentValues(),
                    SQLiteDatabase.CONFLICT_REPLACE)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    private companion object {
        const val NOMBRE_DB = "kompa.db"
        const val VERSION_DB = 2

        const val TABLA_ACTIVIDADES = "actividades"
        const val TABLA_CUENTAS = "cuentas"

        const val COL_ID = "id"
        const val COL_NOMBRE = "nombre"
        const val COL_DESCRIPCION = "descripcion"
        const val COL_UBICACION = "ubicacion"
        const val COL_LAT = "lat"
        const val COL_LON = "lon"
        const val COL_CREADO_POR = "creado_por"
        const val COL_DURACION = "duracion_min"
        const val COL_FECHA_CREACION = "fecha_creacion"
        const val COL_FECHA_ACTIVIDAD = "fecha_actividad"
        const val COL_FOTO = "foto_ruta"
        const val COL_ORIGEN = "origen"
        const val COL_CORREO = "correo"
        const val COL_SAL = "sal"
        const val COL_HASH = "hash"

        val DDL_ACTIVIDADES = """
            CREATE TABLE $TABLA_ACTIVIDADES (
                $COL_ID TEXT PRIMARY KEY,
                $COL_NOMBRE TEXT NOT NULL,
                $COL_DESCRIPCION TEXT NOT NULL,
                $COL_UBICACION TEXT NOT NULL,
                $COL_LAT REAL NOT NULL,
                $COL_LON REAL NOT NULL,
                $COL_CREADO_POR TEXT NOT NULL,
                $COL_DURACION INTEGER,
                $COL_FECHA_CREACION INTEGER,
                $COL_FECHA_ACTIVIDAD INTEGER,
                $COL_FOTO TEXT,
                $COL_ORIGEN TEXT NOT NULL
            )
        """.trimIndent()

        val DDL_CUENTAS = """
            CREATE TABLE IF NOT EXISTS $TABLA_CUENTAS (
                $COL_CORREO TEXT PRIMARY KEY,
                $COL_SAL TEXT NOT NULL,
                $COL_HASH TEXT NOT NULL
            )
        """.trimIndent()
    }
}