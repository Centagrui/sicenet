package com.example.sicenet.data.local

import androidx.room.*
import com.example.sicenet.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SicenetDao {
    // Operaciones para Perfil
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarPerfil(perfil: AlumnoPerfil)

    @Query("SELECT * FROM perfil_alumno LIMIT 1")
    fun obtenerPerfil(): Flow<AlumnoPerfil?>

    // Operaciones para Carga Académica
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCarga(materias: List<Materia>)

    @Query("DELETE FROM carga_academica")
    suspend fun limpiarCarga()

    @Query("SELECT * FROM carga_academica")
    fun obtenerCarga(): Flow<List<Materia>>

    // Operaciones para Kardex
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarKardex(items: List<Kardex>)

    @Query("SELECT * FROM kardex")
    fun obtenerKardex(): Flow<List<Kardex>>
}