package com.example.sicenet.data.local

import androidx.room.*
import com.example.sicenet.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SicenetDao {
    // --- PERFIL ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPerfil(perfil: AlumnoPerfil)

    @Query("SELECT * FROM perfil_alumno LIMIT 1")
    fun obtenerPerfil(): Flow<AlumnoPerfil?>

    // --- CARGA ACADÉMICA ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCarga(materias: List<Materia>)

    @Query("DELETE FROM carga_academica")
    suspend fun limpiarCarga()

    @Query("SELECT * FROM carga_academica")
    fun obtenerCarga(): Flow<List<Materia>>

    // --- KÁRDEX ---
    @Query("SELECT * FROM kardex")
    fun obtenerKardex(): Flow<List<Kardex>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarKardex(items: List<Kardex>)

    @Query("DELETE FROM kardex")
    suspend fun limpiarKardex()

    // --- UNIDADES (PARCIALES) ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUnidades(unidades: List<UnidadCalificacion>)

    @Query("DELETE FROM calificaciones_unidades")
    suspend fun limpiarUnidades() // Aquí estaba el duplicado, ya solo queda una vez.

    @Query("SELECT * FROM calificaciones_unidades")
    fun obtenerUnidades(): Flow<List<UnidadCalificacion>>

    // --- CALIFICACIONES FINALES ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarFinales(finales: List<CalificacionFinal>)

    @Query("DELETE FROM calificaciones_finales")
    suspend fun limpiarFinales()

    @Query("SELECT * FROM calificaciones_finales")
    fun obtenerFinales(): Flow<List<CalificacionFinal>>
    // Agrega esto a SicenetDao.kt
    @Query("SELECT * FROM carga_academica")
    suspend fun obtenerCargaDirecta(): List<Materia>
    @Query("DELETE FROM perfil_alumno")
    suspend fun limpiarPerfil()
}
