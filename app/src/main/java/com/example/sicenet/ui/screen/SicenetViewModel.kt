package com.example.sicenet.ui

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sicenet.data.ISicenetRepository
import com.example.sicenet.model.AlumnoPerfil
import com.example.sicenet.data.local.SicenetDatabase
import com.example.sicenet.data.workers.*
import androidx.work.*
import com.example.sicenet.data.SicenetLocalRepository
import kotlinx.coroutines.launch

class SicenetViewModel(
    private val repository: ISicenetRepository,
    private val localRepository: SicenetLocalRepository,
    application: Application
) : AndroidViewModel(application) {

    // --- ESTADO DE LOGIN ---
    var matricula by mutableStateOf("")
    var password by mutableStateOf("")
    var estaCargando by mutableStateOf(false)
    var mensajeError by mutableStateOf("")

    // --- ACCESO A DATOS LOCALES (ROOM) ---
    val perfilLocal = localRepository.perfil
    val materiasLocal = localRepository.cargaAcademica // Se usa para CargaScreen
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

    // --- FUNCIONES DE SESIÓN Y SINCRONIZACIÓN ---

    fun iniciarSesion(context: Context, alEntrar: () -> Unit) {
        if (estaCargando) return
        viewModelScope.launch {
            estaCargando = true
            mensajeError = ""
            try {
                val exito = repository.login(matricula, password)
                if (exito) {
                    // Sincronización inicial básica
                    sincronizarPerfil(context)
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

    // --- MÉTODOS DE SINCRONIZACIÓN (PUNTO 2B) ---

    fun sincronizarPerfil(context: Context) {
        val workManager = WorkManager.getInstance(context)
        val fetch = OneTimeWorkRequestBuilder<FetchProfileWorker>().build()
        val save = OneTimeWorkRequestBuilder<SaveProfileWorker>().build()
        workManager.beginUniqueWork("sync_perfil", ExistingWorkPolicy.REPLACE, fetch)
            .then(save).enqueue()
    }

    fun sincronizarCarga(context: Context) {
        val workManager = WorkManager.getInstance(context)
        val fetch = OneTimeWorkRequestBuilder<FetchCargaWorker>().build()
        val save = OneTimeWorkRequestBuilder<SaveCargaWorker>().build()
        workManager.beginUniqueWork("sync_carga", ExistingWorkPolicy.REPLACE, fetch)
            .then(save).enqueue()
    }

    fun sincronizarKardex(context: Context) {
        val workManager = WorkManager.getInstance(context)
        val fetch = OneTimeWorkRequestBuilder<FetchKardexWorker>().build()
        val save = OneTimeWorkRequestBuilder<SaveKardexWorker>().build()
        workManager.beginUniqueWork("sync_kardex", ExistingWorkPolicy.REPLACE, fetch)
            .then(save).enqueue()
    }

    fun sincronizarFinales(context: Context) {
        val workManager = WorkManager.getInstance(context)
        val fetch = OneTimeWorkRequestBuilder<FetchFinalesWorker>().build()
        val save = OneTimeWorkRequestBuilder<SaveFinalesWorker>().build()
        workManager.beginUniqueWork("sync_finales", ExistingWorkPolicy.REPLACE, fetch)
            .then(save).enqueue()
    }

    fun sincronizarUnidades(context: Context) {
        val workManager = WorkManager.getInstance(context)
        val fetch = OneTimeWorkRequestBuilder<FetchUnidadesWorker>().build()
        val save = OneTimeWorkRequestBuilder<SaveUnidadesWorker>().build()
        workManager.beginUniqueWork("sync_unidades", ExistingWorkPolicy.REPLACE, fetch)
            .then(save).enqueue()
    }
}