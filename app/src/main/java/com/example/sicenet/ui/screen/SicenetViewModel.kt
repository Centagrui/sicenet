package com.example.sicenet.ui

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sicenet.data.ISicenetRepository
import com.example.sicenet.model.AlumnoPerfil
import com.example.sicenet.model.Materia
import com.example.sicenet.model.Kardex
import com.example.sicenet.data.local.SicenetDatabase
import com.example.sicenet.data.workers.*
import androidx.work.*
import com.example.sicenet.data.SicenetLocalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class SicenetViewModel(
    private val repository: ISicenetRepository,
    private val localRepository: SicenetLocalRepository, // 1. Agrega el repo local aquí

    application: Application
) : AndroidViewModel(application) {


    // 1. --- ESTADO DE LOGIN ---
    var matricula by mutableStateOf("")
    var password by mutableStateOf("")
    var estaCargando by mutableStateOf(false)
    var mensajeError by mutableStateOf("")

    // 2. --- ACCESO A BASE DE DATOS LOCAL (ROOM) ---
    private val dao = SicenetDatabase.getDatabase(application).sicenetDao()

    // Usamos Flow directamente. Compose lo consumirá con .collectAsState()
    val perfilLocal = localRepository.perfil
    val materiasCarga = localRepository.cargaAcademica
    val kardexLocal = localRepository.kardex

    // 3. --- BLOQUE INIT ---
    init {
        viewModelScope.launch {
            // Observamos el flujo del Kardex para depuración
            kardexLocal.collect { lista ->
                Log.d("DEBUG_SICENET", "Kárdex actualizado en DB: ${lista.size} materias")
            }
        }
    }

    // 4. --- VARIABLES DE ESTADO TEMPORAL ---
    var alumnoData by mutableStateOf<AlumnoPerfil?>(null)

    // 5. --- FUNCIONES ---

    fun iniciarSesion(context: Context, alEntrar: () -> Unit) {
        if (estaCargando) return

        viewModelScope.launch {
            estaCargando = true
            mensajeError = ""

            try {
                val exito = repository.login(matricula, password)

                if (exito) {
                    val workManager = WorkManager.getInstance(context)

                    val constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()

                    // Definición de Workers
                    val fetchPerfil = OneTimeWorkRequestBuilder<FetchProfileWorker>().setConstraints(constraints).build()
                    val savePerfil = OneTimeWorkRequestBuilder<SaveProfileWorker>().build()

                    val fetchCarga = OneTimeWorkRequestBuilder<FetchCargaWorker>().setConstraints(constraints).build()
                    val saveCarga = OneTimeWorkRequestBuilder<SaveCargaWorker>().build()

                    val fetchKardex = OneTimeWorkRequestBuilder<FetchKardexWorker>().setConstraints(constraints).build()
                   // val saveKardex = OneTimeWorkRequestBuilder<SaveKardexWorker>().build()

                    // Encadenamiento de tareas
                    workManager.beginUniqueWork("sync_total", ExistingWorkPolicy.REPLACE, fetchPerfil)
                        .then(savePerfil)
                        .then(fetchCarga)
                        .then(saveCarga)
                        .then(fetchKardex)
                        //.then(saveKardex)
                        .enqueue()

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
    // En SicenetViewModel
    val unidadesLocal = localRepository.unidades // Este viene del DAO

    fun sincronizarUnidades(context: Context) {
        val workManager = WorkManager.getInstance(context)

        val syncRequest = OneTimeWorkRequestBuilder<FetchUnidadesWorker>().build()
        val saveRequest = OneTimeWorkRequestBuilder<SaveUnidadesWorker>().build()

        workManager.beginUniqueWork(
            "sync_unidades_unique",
            ExistingWorkPolicy.REPLACE,
            syncRequest
        ).then(saveRequest).enqueue()
    }
}