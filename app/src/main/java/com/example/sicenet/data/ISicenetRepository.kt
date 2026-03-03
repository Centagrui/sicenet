package com.example.sicenet.data

import com.example.sicenet.model.*

/**
 * Contrato que define todas las operaciones de datos de la app.
 * Aquí se declaran las funciones para traer datos de internet (Fetch)
 * y para traducirlos (Procesamiento).
 */
interface ISicenetRepository {

    // --- ACCESO ---
    // Verifica si el usuario y contraseña son válidos.
    suspend fun login(matricula: String, contrasenia: String): Boolean

    // --- MÉTODOS DE RECUPERACIÓN (FETCH) ---
    // Estas funciones son 'suspend' porque van a internet y deben correr en hilos secundarios.
    // Se usan principalmente en los "FetchWorkers".

    suspend fun recuperarPerfil(): String?
    suspend fun recuperarCargaAcademica(): String?
    suspend fun recuperarKardex(): String?

    // Métodos vitales para obtener los detalles de calificaciones.
    suspend fun recuperarCalificacionesUnidades(): String?
    suspend fun recuperarCalificacionesFinales(): String?

    // --- MÉTODOS DE PROCESAMIENTO (PARSING) ---
    // Estas funciones NO necesitan 'suspend' porque solo procesan texto en memoria.
    // Toman el XML (String) y lo convierten en Objetos de Kotlin (Listas).
    // Se usan principalmente en los "SaveWorkers".

    // Convierte el XML de perfil en un objeto AlumnoPerfil.
    fun procesarDatosPerfil(xml: String): AlumnoPerfil?

    // Convierte el XML de carga en una lista de objetos Materia.
    fun procesarCargaAcademica(xml: String): List<Materia>

    // Convierte el XML de kárdex en una lista de objetos Kardex.
    fun procesarKardex(xml: String): List<Kardex>

    // Convierte el XML de unidades en una lista de UnidadCalificacion.
    fun procesarUnidades(xml: String): List<UnidadCalificacion>

    // Convierte el XML de finales en una lista de CalificacionFinal.
    fun procesarCalificacionesFinales(xml: String): List<CalificacionFinal>
}