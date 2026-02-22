package com.example.sicenet.data

import com.example.sicenet.data.local.SicenetDao
import com.example.sicenet.model.*
import kotlinx.coroutines.flow.Flow

class SicenetLocalRepository(private val dao: SicenetDao) {
    val perfil: Flow<AlumnoPerfil?> = dao.obtenerPerfil()
    val cargaAcademica: Flow<List<Materia>> = dao.obtenerCarga()
    val kardex: Flow<List<Kardex>> = dao.obtenerKardex()

    suspend fun guardarPerfil(perfil: AlumnoPerfil) = dao.insertarPerfil(perfil)
    suspend fun guardarCarga(materias: List<Materia>) {
        dao.limpiarCarga()
        dao.insertarCarga(materias)
    }
    suspend fun guardarKardex(items: List<Kardex>) = dao.insertarKardex(items)
}