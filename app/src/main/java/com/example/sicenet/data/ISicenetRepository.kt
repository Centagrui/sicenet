package com.example.sicenet.data

import com.example.sicenet.model.AlumnoPerfil
//repository
interface ISicenetRepository {
    suspend fun login(usuario: String, contrasena: String): Boolean

    suspend fun recuperarPerfil(): String?
    fun procesarDatosPerfil(xml: String): AlumnoPerfil?
}