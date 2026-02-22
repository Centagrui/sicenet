package com.example.sicenet.data

import com.example.sicenet.model.AlumnoPerfil
import com.example.sicenet.model.Kardex
import com.example.sicenet.model.Materia

interface ISicenetRepository {
    // Cambié usuario/contrasena por matricula/contrasenia para que coincida con tu SicenetRepository
    suspend fun login(matricula: String, contrasenia: String): Boolean

    suspend fun recuperarPerfil(): String?
    fun procesarDatosPerfil(xml: String): AlumnoPerfil?

    suspend fun recuperarCargaAcademica(): String?
    fun procesarCargaAcademica(xml: String): List<Materia>

    suspend fun recuperarKardex(): String?
    fun procesarKardex(xml: String): List<Kardex>

    suspend fun recuperarCalificacionesUnidades(): String?
    suspend fun recuperarCalificacionesFinales(): String?
}