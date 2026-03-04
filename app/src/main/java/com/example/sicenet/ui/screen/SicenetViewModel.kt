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

// desde aqui manejgamos los workers
class SicenetViewModel(
    private val repository: ISicenetRepository,
    private val localRepository: SicenetLocalRepository,
    application: Application
) : AndroidViewModel(application) {

// nos fijamos si tiene intenet
    val estaOnline = mutableStateOf(true)

    // Instancia única de WorkManager para gestionar tareas en segundo plano
    private val workManager = WorkManager.getInstance(application)

 //esta esperando lo que se le va a llevar
    var matricula by mutableStateOf("")
    var password by mutableStateOf("")
    var estaCargando by mutableStateOf(false)
    var mensajeError by mutableStateOf("")

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



    fun iniciarSesion(context: Context, alEntrar: () -> Unit) {
        if (estaCargando) return
        viewModelScope.launch {
            estaCargando = true
            mensajeError = ""
            try {
                // verifica
                val exito = repository.login(matricula, password)
                if (exito) {
                    sincronizarDato("PERFIL")
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

    // primera va por los fetch y luego los guarda en room
    fun sincronizarDato(tipo: String) {
        // solo si hay intenet
        val restricciones = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // para mandar llamar los workers
        val (fetchClass, saveClass, uniqueName) = when(tipo) {
            "PERFIL" -> Triple(FetchProfileWorker::class.java, SaveProfileWorker::class.java, "sync_perfil")
            "CARGA" -> Triple(FetchCargaWorker::class.java, SaveCargaWorker::class.java, "sync_carga")
            "KARDEX" -> Triple(FetchKardexWorker::class.java, SaveKardexWorker::class.java, "sync_kardex")
            "UNIDADES" -> Triple(FetchUnidadesWorker::class.java, SaveUnidadesWorker::class.java, "sync_unidades")
            "FINALES" -> Triple(FetchFinalesWorker::class.java, SaveFinalesWorker::class.java, "sync_finales")
            else -> return
        }

        //  descargar datos del SICENET
        val requestFetch = OneTimeWorkRequest.Builder(fetchClass)
            .setConstraints(restricciones)
            .build()

        // procesar en Room
        val requestSave = OneTimeWorkRequest.Builder(saveClass)
            .build()


        // es para que solo se haga una cosa por cosa, y solo hasta que tegna los datos de fetch
        // va a poder guardar los datos en el save
        workManager.beginUniqueWork(uniqueName, ExistingWorkPolicy.REPLACE, requestFetch)
            .then(requestSave)// es cuando lo guarda en la room despues de haber acabo bien lo del fetch
            .enqueue()
    }


    // es para ver si hay datos ya en la room
    fun verificarSesion(onResultado: (Boolean) -> Unit) {
        viewModelScope.launch {
            // Tomamos el primer valor que emita el flujo de perfil
            val perfil = localRepository.perfil.firstOrNull()
            onResultado(perfil != null)
        }
    }

   // cerrar sesion
    fun cerrarSesion() {
        viewModelScope.launch {
            localRepository.limpiarTodo()
        }
    }
}