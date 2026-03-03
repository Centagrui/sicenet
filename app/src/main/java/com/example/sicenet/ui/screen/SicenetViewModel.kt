package com.example.sicenet.ui

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.sicenet.data.ISicenetRepository
import com.example.sicenet.data.SicenetLocalRepository
import com.example.sicenet.data.workers.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

/**
 * ViewModel principal de la aplicación.
 * Gestiona el estado de la UI, la sesión del usuario y la orquestación de Workers.
 */
class SicenetViewModel(
    private val repository: ISicenetRepository,
    private val localRepository: SicenetLocalRepository,
    application: Application
) : AndroidViewModel(application) {

    // Estado para monitorear si el dispositivo tiene acceso a Internet
    val estaOnline = mutableStateOf(true)

    // Instancia única de WorkManager para gestionar tareas en segundo plano
    private val workManager = WorkManager.getInstance(application)

    // --- ESTADOS DE LOGIN (Reactivos a Compose) ---
    var matricula by mutableStateOf("")
    var password by mutableStateOf("")
    var estaCargando by mutableStateOf(false)
    var mensajeError by mutableStateOf("")

    // --- FLUJOS DE DATOS DESDE ROOM (Repositorio Local) ---
    // Estos 'Flows' notifican a la UI automáticamente cuando la base de datos cambia
    val perfilLocal = localRepository.perfil
    val materiasLocal = localRepository.cargaAcademica
    val kardexLocal = localRepository.kardex
    val unidadesLocal = localRepository.unidades
    val finalesLocal = localRepository.finales

    init {
        // Observador de depuración para verificar en consola cambios en el Kárdex
        viewModelScope.launch {
            kardexLocal.collect { lista ->
                Log.d("DEBUG_SICENET", "Kárdex actualizado en DB: ${lista.size} materias")
            }
        }
    }

    // --- FUNCIONES DE SESIÓN ---

    /**
     * Intenta autenticar al usuario con el servidor SICENET.
     */
    fun iniciarSesion(context: Context, alEntrar: () -> Unit) {
        if (estaCargando) return // Evita múltiples clicks
        viewModelScope.launch {
            estaCargando = true
            mensajeError = ""
            try {
                // Llamada suspendida al repositorio (petición de red)
                val exito = repository.login(matricula, password)
                if (exito) {
                    // Si el login es correcto, disparamos la primera sincronización de perfil
                    sincronizarDato("PERFIL")
                    estaCargando = false
                    alEntrar() // Navega a la pantalla principal
                    password = "" // Seguridad: limpia la contraseña de memoria
                } else {
                    mensajeError = "Matrícula o contraseña incorrecta."
                    estaCargando = false
                }
            } catch (e: Exception) {
                mensajeError = "Sin conexión al servidor"
                estaCargando = false
            }
        }
    }

    // --- MÉTODO DE SINCRONIZACIÓN UNIFICADO ---

    /**
     * Orquesta el encadenamiento de Workers:
     * Primero descarga (Fetch) y luego guarda (Save) en la base de datos local.
     */
    fun sincronizarDato(tipo: String) {
        // Solo inicia si hay conexión a Internet
        val restricciones = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Seleccionamos dinámicamente el par de Workers y el nombre único de la tarea
        val (fetchClass, saveClass, uniqueName) = when(tipo) {
            "PERFIL" -> Triple(FetchProfileWorker::class.java, SaveProfileWorker::class.java, "sync_perfil")
            "CARGA" -> Triple(FetchCargaWorker::class.java, SaveCargaWorker::class.java, "sync_carga")
            "KARDEX" -> Triple(FetchKardexWorker::class.java, SaveKardexWorker::class.java, "sync_kardex")
            "UNIDADES" -> Triple(FetchUnidadesWorker::class.java, SaveUnidadesWorker::class.java, "sync_unidades")
            "FINALES" -> Triple(FetchFinalesWorker::class.java, SaveFinalesWorker::class.java, "sync_finales")
            else -> return
        }

        // Petición para descargar datos del SICENET
        val requestFetch = OneTimeWorkRequest.Builder(fetchClass)
            .setConstraints(restricciones)
            .build()

        // Petición para procesar y persistir en Room
        val requestSave = OneTimeWorkRequest.Builder(saveClass)
            .build()

        // ENCADENAMIENTO: Fetch -> Save
        // ExistingWorkPolicy.REPLACE cancela una descarga anterior si se inicia una nueva del mismo tipo
        workManager.beginUniqueWork(uniqueName, ExistingWorkPolicy.REPLACE, requestFetch)
            .then(requestSave)
            .enqueue()
    }

    /**
     * Verifica si existen datos de perfil en Room.
     * Útil para el Splash Screen para decidir si mandar al usuario a Login o a Home.
     */
    fun verificarSesion(onResultado: (Boolean) -> Unit) {
        viewModelScope.launch {
            // Tomamos el primer valor que emita el flujo de perfil
            val perfil = localRepository.perfil.firstOrNull()
            onResultado(perfil != null)
        }
    }

    /**
     * Borra todos los datos de las tablas de Room (Cierra la sesión localmente).
     */
    fun cerrarSesion() {
        viewModelScope.launch {
            localRepository.limpiarTodo()
        }
    }
}