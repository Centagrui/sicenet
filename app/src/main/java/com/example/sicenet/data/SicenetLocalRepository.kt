package com.example.sicenet.data

import com.example.sicenet.data.local.SicenetDao
import com.example.sicenet.model.*
import kotlinx.coroutines.flow.Flow

/**
 * Esta clase actúa como un mediador entre el DAO (Room) y el resto de la app.
 * Su objetivo es centralizar todas las consultas a la base de datos local.
 */
class SicenetLocalRepository(private val dao: SicenetDao) {

    // --- FLUJOS DE DATOS (LECTURA) ---
    // Usamos Flow para que, si los datos en las tablas cambian (gracias a los Workers),
    // la interfaz de usuario se entere y se actualice solita.

    val perfil: Flow<AlumnoPerfil?> = dao.obtenerPerfil()
    val cargaAcademica: Flow<List<Materia>> = dao.obtenerCarga()
    val kardex: Flow<List<Kardex>> = dao.obtenerKardex()
    val unidades: Flow<List<UnidadCalificacion>> = dao.obtenerUnidades()
    val finales: Flow<List<CalificacionFinal>> = dao.obtenerFinales()

    // --- OPERACIONES DE ESCRITURA (GUARDADO) ---
    // Estas funciones son 'suspend' porque escribir en el disco es una tarea lenta
    // que debe hacerse fuera del hilo principal.

    // Guarda o actualiza la información del alumno.
    suspend fun guardarPerfil(perfil: AlumnoPerfil) = dao.insertarPerfil(perfil)

    // Reemplaza la carga académica actual por una nueva (primero limpia, luego inserta).
    suspend fun guardarCarga(materias: List<Materia>) {
        dao.limpiarCarga()
        dao.insertarCarga(materias)
    }

    // Agrega elementos al historial académico (Kárdex).
    suspend fun guardarKardex(items: List<Kardex>) = dao.insertarKardex(items)

    // Actualiza las calificaciones parciales/unidades.
    suspend fun guardarUnidades(lista: List<UnidadCalificacion>) {
        dao.limpiarUnidades()
        dao.insertarUnidades(lista)
    }

    // --- LIMPIEZA ---
    /**
     * Borra absolutamente toda la información de las tablas.
     * Es vital para cuando el usuario presiona "Cerrar Sesión",
     * evitando que el siguiente alumno vea datos del anterior.
     */
    suspend fun limpiarTodo() {
        dao.limpiarPerfil()
        dao.limpiarCarga()
        dao.limpiarKardex()
        dao.limpiarUnidades()
        dao.limpiarFinales()
    }
}