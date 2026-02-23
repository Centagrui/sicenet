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
    fun obtenerCarga(): Flow<List<Materia>> // MANTENER SOLO ESTA

    // --- KÁRDEX ---
    @Query("SELECT * FROM kardex")
    fun obtenerKardex(): Flow<List<Kardex>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarKardex(items: List<Kardex>)

    @Query("DELETE FROM kardex")
    suspend fun limpiarKardex()

    // Dentro de SicenetDao.kt
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarUnidades(unidades: List<UnidadCalificacion>)

    @Query("DELETE FROM calificaciones_unidades")
    suspend fun limpiarUnidades()

    @Query("SELECT * FROM calificaciones_unidades")
    fun obtenerUnidades(): Flow<List<UnidadCalificacion>>

    @Query("SELECT * FROM kardex WHERE periodo = 'Actual' OR periodo = ''")
    fun obtenerFinales(): Flow<List<Kardex>>

    @Query("DELETE FROM kardex")
    suspend fun borrarKardex()
}
