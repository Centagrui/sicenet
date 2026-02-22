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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

// Usamos AndroidViewModel para poder acceder a la base de datos con el context de la app
class SicenetViewModel(
    private val repository: ISicenetRepository,
    application: Application
) : AndroidViewModel(application) {

    // --- ESTADO DE LOGIN ---
    var matricula by mutableStateOf("")
    var password by mutableStateOf("")
    var estaCargando by mutableStateOf(false)
    var mensajeError by mutableStateOf("")

    // --- ACCESO A BASE DE DATOS LOCAL (ROOM) ---
    private val dao = SicenetDatabase.getDatabase(application).sicenetDao()

    // Estas variables son "Flujos" que se actualizan solos cuando la DB cambia
    val perfilLocal: Flow<AlumnoPerfil?> = dao.obtenerPerfil()
    val materiasCarga: Flow<List<Materia>> = dao.obtenerCarga()
    val kardexLocal: Flow<List<Kardex>> = dao.obtenerKardex()

    // Mantenemos estas por compatibilidad con tu código anterior si es necesario
    var perfilXml by mutableStateOf<String?>(null)
    var alumnoData by mutableStateOf<AlumnoPerfil?>(null)

    fun cargarPerfil() {
        viewModelScope.launch {
            val xml = repository.recuperarPerfil()
            perfilXml = xml
            if (xml != null && !xml.contains("Error")) {
                alumnoData = repository.procesarDatosPerfil(xml)
            }
        }
    }

    fun iniciarSesion(context: Context, alEntrar: () -> Unit) {
        // Evitamos disparar el login si ya se está cargando
        if (estaCargando) return

        viewModelScope.launch {
            estaCargando = true
            mensajeError = ""

            try {
                val exito = repository.login(matricula, password)

                if (exito) {
                    // Configuración de WorkManager...
                    val workManager = WorkManager.getInstance(context)
                    val constraints = Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()

                    val fetchPerfil = OneTimeWorkRequestBuilder<FetchProfileWorker>().setConstraints(constraints).build()
                    val savePerfil = OneTimeWorkRequestBuilder<SaveProfileWorker>().build()
                    val fetchCarga = OneTimeWorkRequestBuilder<FetchCargaWorker>().setConstraints(constraints).build()
                    val saveCarga = OneTimeWorkRequestBuilder<SaveCargaWorker>().build()
                    val fetchKardex = OneTimeWorkRequestBuilder<FetchKardexWorker>().setConstraints(constraints).build()
                    val saveKardex = OneTimeWorkRequestBuilder<SaveKardexWorker>().build()

                    workManager.beginUniqueWork("sync_total", ExistingWorkPolicy.REPLACE, fetchPerfil)
                        .then(savePerfil)
                        .then(fetchCarga)
                        .then(saveCarga)
                        .then(fetchKardex)
                        .then(saveKardex)
                        .enqueue()

                    // PASO CLAVE: Detenemos el estado de carga ANTES de navegar
                    estaCargando = false

                    // Ejecutamos la navegación
                    alEntrar()

                    // Opcional: Limpiamos contraseña para evitar re-logins accidentales
                    password = ""
                } else {
                    mensajeError = "Error de autenticación. Verifica tus datos."
                    estaCargando = false
                }
            } catch (e: Exception) {
                mensajeError = "Error de conexión: ${e.message}"
                estaCargando = false
            }
            // Eliminamos el 'finally' para tener control total de cuándo se apaga el indicador
        }
    }
}