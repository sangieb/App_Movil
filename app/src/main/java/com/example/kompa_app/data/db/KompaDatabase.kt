package com.example.kompa_app.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.kompa_app.data.actividad.ActividadesEjemplo
import com.example.kompa_app.data.catalogo.CatalogosSemilla

@Database(
    entities = [
        ActividadEntity::class,
        CuentaEntity::class,
        CatalogoEntity::class,
        PublicacionEntity::class
    ],
    version = KompaDatabase.VERSION,
    exportSchema = false
)
abstract class KompaDatabase : RoomDatabase() {

    abstract fun actividadDao(): ActividadDao
    abstract fun cuentaDao(): CuentaDao
    abstract fun catalogoDao(): CatalogoDao
    abstract fun publicacionDao(): PublicacionDao

    companion object {
        /** Mismo archivo que usaba KompaDbHelper: los datos existentes se conservan. */
        const val NOMBRE = "kompa.db"

        /** KompaDbHelper llegó a la versión 4; Room arranca en la 5. */
        const val VERSION = 5

        fun crear(context: Context): KompaDatabase =
            Room.databaseBuilder(context.applicationContext, KompaDatabase::class.java, NOMBRE)
                .addMigrations(*MIGRACIONES_DESDE_HELPER)
                .addCallback(SembrarAlCrear)
                .build()
    }
}

/* ---------- Datos iniciales (antes en KompaDbHelper.onCreate) ---------- */

private object SembrarAlCrear : RoomDatabase.Callback() {
    override fun onCreate(db: SupportSQLiteDatabase) {
        sembrarActividades(db)
        sembrarCatalogos(db)
    }
}

private fun sembrarActividades(db: SupportSQLiteDatabase) {
    ActividadesEjemplo.lista().map { it.aEntidad() }.forEach { a ->
        db.insert("actividades", SQLiteDatabase.CONFLICT_REPLACE, ContentValues().apply {
            put("id", a.id)
            put("nombre", a.nombre)
            put("descripcion", a.descripcion)
            put("ubicacion", a.ubicacion)
            put("lat", a.lat)
            put("lon", a.lon)
            put("creado_por", a.creadoPor)
            put("duracion_min", a.duracionMin)
            put("fecha_creacion", a.fechaCreacion)
            put("fecha_actividad", a.fechaActividad)
            put("foto_ruta", a.fotoRuta)
            put("origen", a.origen)
        })
    }
}

private fun sembrarCatalogos(db: SupportSQLiteDatabase) {
    CatalogosSemilla.lista().mapNotNull { it.aEntidadOrNull() }.forEach { c ->
        db.insert("catalogos", SQLiteDatabase.CONFLICT_REPLACE, ContentValues().apply {
            put("tipo", c.tipo)
            put("valor", c.valor)
            put("orden", c.orden)
        })
    }
}

/* ---------- Migración desde las versiones 1–4 de KompaDbHelper ---------- */

private const val DDL_ACTIVIDADES =
    "CREATE TABLE IF NOT EXISTS `actividades` (" +
            "`id` TEXT NOT NULL, `nombre` TEXT NOT NULL, `descripcion` TEXT NOT NULL, " +
            "`ubicacion` TEXT NOT NULL, `lat` REAL NOT NULL, `lon` REAL NOT NULL, " +
            "`creado_por` TEXT NOT NULL, `duracion_min` INTEGER, `fecha_creacion` INTEGER, " +
            "`fecha_actividad` INTEGER, `foto_ruta` TEXT, `origen` TEXT NOT NULL, " +
            "PRIMARY KEY(`id`))"

private const val DDL_CUENTAS =
    "CREATE TABLE IF NOT EXISTS `cuentas` (" +
            "`correo` TEXT NOT NULL, `sal` TEXT NOT NULL, `hash` TEXT NOT NULL, " +
            "PRIMARY KEY(`correo`))"

private const val DDL_CATALOGOS =
    "CREATE TABLE IF NOT EXISTS `catalogos` (" +
            "`tipo` TEXT NOT NULL, `valor` TEXT NOT NULL, `orden` INTEGER NOT NULL DEFAULT 0, " +
            "PRIMARY KEY(`tipo`, `valor`))"

private const val DDL_PUBLICACIONES =
    "CREATE TABLE IF NOT EXISTS `publicaciones` (" +
            "`id` TEXT NOT NULL, `titulo` TEXT NOT NULL, `cuerpo` TEXT NOT NULL, " +
            "`autor` TEXT NOT NULL, `fecha_creacion` INTEGER, `lat` REAL, `lon` REAL, " +
            "PRIMARY KEY(`id`))"

private class TablaRoom(val nombre: String, val ddl: String, val columnas: String)

private val TABLAS = listOf(
    TablaRoom(
        "actividades", DDL_ACTIVIDADES,
        "id, nombre, descripcion, ubicacion, lat, lon, creado_por, duracion_min, " +
                "fecha_creacion, fecha_actividad, foto_ruta, origen"
    ),
    TablaRoom("cuentas", DDL_CUENTAS, "correo, sal, hash"),
    TablaRoom("catalogos", DDL_CATALOGOS, "tipo, valor, orden"),
    TablaRoom(
        "publicaciones", DDL_PUBLICACIONES,
        "id, titulo, cuerpo, autor, fecha_creacion, lat, lon"
    )
)

private fun SupportSQLiteDatabase.existeTabla(nombre: String): Boolean =
    query("SELECT 1 FROM sqlite_master WHERE type = 'table' AND name = ?", arrayOf(nombre))
        .use { it.moveToFirst() }

private fun migrarAlEsquemaRoom(db: SupportSQLiteDatabase) {
    var catalogosNuevos = false
    TABLAS.forEach { t ->
        if (db.existeTabla(t.nombre)) {
            val vieja = "${t.nombre}_old"
            db.execSQL("ALTER TABLE `${t.nombre}` RENAME TO `$vieja`")
            db.execSQL(t.ddl)
            // Las filas con clave NULL (posibles en el esquema viejo) se descartan.
            val primeraCol = t.columnas.substringBefore(",").trim()
            db.execSQL(
                "INSERT OR REPLACE INTO `${t.nombre}` (${t.columnas}) " +
                        "SELECT ${t.columnas} FROM `$vieja` WHERE $primeraCol IS NOT NULL"
            )
            db.execSQL("DROP TABLE `$vieja`")
        } else {
            db.execSQL(t.ddl)
            if (t.nombre == "catalogos") catalogosNuevos = true
        }
    }
    if (catalogosNuevos) sembrarCatalogos(db)
}

private class MigracionDesdeHelper(desde: Int) : Migration(desde, KompaDatabase.VERSION) {
    override fun migrate(db: SupportSQLiteDatabase) = migrarAlEsquemaRoom(db)
}

internal val MIGRACIONES_DESDE_HELPER: Array<Migration> =
    (1 until KompaDatabase.VERSION).map { MigracionDesdeHelper(it) }.toTypedArray()