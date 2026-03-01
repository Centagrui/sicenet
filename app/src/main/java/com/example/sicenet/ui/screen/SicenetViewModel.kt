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

class SicenetViewModel(
    private val repository: ISicenetRepository,
    private val localRepository: SicenetLocalRepository,
    application: Application
) : AndroidViewModel(application) {

    // En SicenetViewModel.kt
    val estaOnline = mutableStateOf(true) // Actualizar esto con un NetworkCallback
    // 1. Instancia global de WorkManager para esta clase
    private val workManager = WorkManager.getInstance(application)

    // --- ESTADO DE LOGIN ---
    var matricula by mutableStateOf("")
    var password by mutableStateOf("")
    var estaCargando by mutableStateOf(false)
    var mensajeError by mutableStateOf("")

    // --- ACCESO A DATOS LOCALES (ROOM) ---
    val perfilLocal = localRepository.perfil
    val materiasLocal = localRepository.cargaAcademica
    val kardexLocal = localRepository.kardex
    val unidadesLocal = localRepository.unidades
    val finalesLocal = localRepository.finales

    init {
        viewModelScope.launch {
            kardexLocal.collect { lista ->
                Log.d("DEBUG_SICENET", "Kárdex actualizado en DB: ${lista.size} materias")
            }
        }
    }

    // --- FUNCIONES DE SESIÓN ---

    fun iniciarSesion(context: Context, alEntrar: () -> Unit) {
        if (estaCargando) return
        viewModelScope.launch {
            estaCargando = true
            mensajeError = ""
            try {
                val exito = repository.login(matricula, password)
                if (exito) {
                    sincronizarDato("PERFIL") // Sincronización inicial
                    estaCargando = false
                    alEntrar()
                    password = ""
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

    // --- MÉTODO DE SINCRONIZACIÓN UNIFICADO (CORREGIDO) ---

    fun sincronizarDato(tipo: String) {
        val restricciones = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Determinamos qué Workers usar según el tipo
        val (fetchClass, saveClass, uniqueName) = when(tipo) {
            "PERFIL" -> Triple(FetchProfileWorker::class.java, SaveProfileWorker::class.java, "sync_perfil")
            "CARGA" -> Triple(FetchCargaWorker::class.java, SaveCargaWorker::class.java, "sync_carga")
            "KARDEX" -> Triple(FetchKardexWorker::class.java, SaveKardexWorker::class.java, "sync_kardex")
            "UNIDADES" -> Triple(FetchUnidadesWorker::class.java, SaveUnidadesWorker::class.java, "sync_unidades")
            "FINALES" -> Triple(FetchFinalesWorker::class.java, SaveFinalesWorker::class.java, "sync_finales")
            else -> return
        }

        // Creamos las peticiones de trabajo con las clases específicas
        val requestFetch = OneTimeWorkRequest.Builder(fetchClass)
            .setConstraints(restricciones)
            .build()

        val requestSave = OneTimeWorkRequest.Builder(saveClass)
            .build()

        // Encadenamos y encolamos
        workManager.beginUniqueWork(uniqueName, ExistingWorkPolicy.REPLACE, requestFetch)
            .then(requestSave)
            .enqueue()
    }
    // Dentro de SicenetViewModel.kt

    // Función para verificar si hay una sesión activa en Room
    // Borra cualquier otra que se llame igual y deja solo esta:
    fun verificarSesion(onResultado: (Boolean) -> Unit) {
        viewModelScope.launch {
            // Importa: kotlinx.coroutines.flow.firstOrNull
            val perfil = localRepository.perfil.firstOrNull()
            onResultado(perfil != null)
        }
    }

    fun cerrarSesion() {
        viewModelScope.launch {
            // Ejecutar todas las limpiezas del DAO
            localRepository.limpiarTodo()
        }
    }

}