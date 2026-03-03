package com.example.sicenet.data.local

import androidx.room.*
import com.example.sicenet.model.*
import kotlinx.coroutines.flow.Flow

/**
 *  las operaciones que puedes realizar en la base de datos
 */
@Dao
interface SicenetDao {

    // Inserta los datos y se remplaza si ya existe el alumno
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPerfil(perfil: AlumnoPerfil)

    // flow cargar datos automaticos
    @Query("SELECT * FROM perfil_alumno LIMIT 1")
    fun obtenerPerfil(): Flow<AlumnoPerfil?>

    @Query("DELETE FROM perfil_alumno")
    suspend fun limpiarPerfil()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCarga(materias: List<Materia>)

    @Query("DELETE FROM carga_academica")
    suspend fun limpiarCarga()

    @Query("SELECT * FROM carga_academica")
    fun obtenerCarga(): Flow<List<Materia>>

    // Versión "One-shot": obtiene la lista una sola vez sin observar cambios (útil para lógica interna).
    @Query("SELECT * FROM carga_academica")
    suspend fun obtenerCargaDirecta(): List<Materia>

    @Query("SELECT * FROM kardex")
    fun obtenerKardex(): Flow<List<Kardex>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarKardex(items: List<Kardex>)

    @Query("DELETE FROM kardex")
    suspend fun limpiarKardex()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUnidades(unidades: List<UnidadCalificacion>)

    @Query("DELETE FROM calificaciones_unidades")
    suspend fun limpiarUnidades()
    @Query("SELECT * FROM calificaciones_unidades")
    fun obtenerUnidades(): Flow<List<UnidadCalificacion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarFinales(finales: List<CalificacionFinal>)

    @Query("DELETE FROM calificaciones_finales")
    suspend fun limpiarFinales()

    @Query("SELECT * FROM calificaciones_finales")
    fun obtenerFinales(): Flow<List<CalificacionFinal>>
}