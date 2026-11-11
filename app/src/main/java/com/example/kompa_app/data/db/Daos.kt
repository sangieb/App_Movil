package com.example.kompa_app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface ActividadDao {

    /** Equivale al WHERE dinámico de consultarActividades: cada filtro en null se ignora. */
    @Query(
        """
        SELECT * FROM actividades
        WHERE (:patron IS NULL OR nombre LIKE :patron OR ubicacion LIKE :patron)
          AND (:origen IS NULL OR origen = :origen)
          AND (:duracionMinima IS NULL OR duracion_min >= :duracionMinima)
          AND (:duracionMaxima IS NULL OR duracion_min <= :duracionMaxima)
        ORDER BY fecha_creacion DESC, nombre ASC
        """
    )
    fun buscar(
        patron: String? = null,
        origen: String? = null,
        duracionMinima: Int? = null,
        duracionMaxima: Int? = null
    ): List<ActividadEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertar(actividad: ActividadEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertarTodas(actividades: List<ActividadEntity>)
}

@Dao
interface CuentaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertar(cuenta: CuentaEntity)

    @Query("SELECT * FROM cuentas WHERE correo = :correo LIMIT 1")
    fun buscar(correo: String): CuentaEntity?
}

@Dao
interface CatalogoDao {

    @Query("SELECT valor FROM catalogos WHERE tipo = :tipo ORDER BY orden ASC")
    fun valoresDe(tipo: String): List<String>

    @Query("DELETE FROM catalogos")
    fun borrarTodo()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertarTodos(catalogos: List<CatalogoEntity>)

    /** Borra y vuelve a llenar en una sola transacción, como hacía el helper. */
    @Transaction
    fun reemplazar(catalogos: List<CatalogoEntity>) {
        borrarTodo()
        insertarTodos(catalogos)
    }
}

@Dao
interface PublicacionDao {

    @Query("SELECT * FROM publicaciones ORDER BY fecha_creacion DESC, titulo ASC")
    fun todas(): List<PublicacionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun guardarTodas(publicaciones: List<PublicacionEntity>)
}