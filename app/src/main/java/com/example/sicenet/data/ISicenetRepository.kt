package com.example.sicenet.data

import com.example.sicenet.model.AlumnoPerfil
import com.example.sicenet.model.Materia

//repository
interface ISicenetRepository {
    suspend fun login(usuario: String, contrasena: String): Boolean

    suspend fun recuperarPerfil(): String?
    fun procesarDatosPerfil(xml: String): AlumnoPerfil?

    suspend fun recuperarCargaAcademica(): String?
    suspend fun recuperarKardex(): String?

    suspend fun recuperarCalificacionesUnidades(): String?
    suspend fun recuperarCalificacionesFinales(): String?

    fun procesarCargaAcademica(xml: String): List<Materia> // <-- Esta es la que falta


}