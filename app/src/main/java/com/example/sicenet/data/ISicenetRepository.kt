package com.example.sicenet.data

import com.example.sicenet.model.AlumnoPerfil
import com.example.sicenet.model.Kardex
import com.example.sicenet.model.Materia
import com.example.sicenet.model.UnidadCalificacion
import com.example.sicenet.model.CalificacionFinal
interface ISicenetRepository {
    suspend fun login(matricula: String, contrasenia: String): Boolean
    suspend fun recuperarPerfil(): String?
    suspend fun recuperarCargaAcademica(): String?
    suspend fun recuperarKardex(): String?

    // ESTAS DOS SON VITALES PARA LOS WORKERS:
    suspend fun recuperarCalificacionesUnidades(): String?
    suspend fun recuperarCalificacionesFinales(): String?

    // Métodos de procesamiento
    fun procesarDatosPerfil(xml: String): AlumnoPerfil?
    fun procesarCargaAcademica(xml: String): List<Materia>
    fun procesarKardex(xml: String): List<Kardex>
    fun procesarUnidades(xml: String): List<UnidadCalificacion>

    //  fun procesarCalificacionesFinales(xml: String): List<Kardex>
    fun procesarCalificacionesFinales(xml: String): List<CalificacionFinal>
}