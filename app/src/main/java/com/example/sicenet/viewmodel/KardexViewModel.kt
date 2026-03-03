package com.example.sicenet.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.sicenet.data.SicenetLocalRepository
import com.example.sicenet.data.workers.FetchKardexWorker
import com.example.sicenet.data.workers.SaveKardexWorker
import com.example.sicenet.model.Kardex
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * ViewModel que gestiona exclusivamente la lógica del historial académico (Kárdex).
 * @param localRepository Repositorio que conecta con los DAOs de Room.
 * @param context Contexto necesario para inicializar el WorkManager.
 */
class KardexViewModel(
    private val localRepository: SicenetLocalRepository,
    private val context: Context
) : ViewModel() {

    /**
     * Fuente de verdad única: Exponemos el Flow de Room.
     * Cualquier cambio realizado por los Workers en la base de datos se reflejará
     * automáticamente en la UI que observe este flujo.
     */
    val listaKardex: Flow<List<Kardex>> = localRepository.kardex

    /**
     * Orquestación de procesos en segundo plano.
     * Utiliza WorkManager para asegurar que la descarga y el guardado se completen
     * incluso si el usuario sale de la aplicación.
     */
    fun refrescarKardex() {
        val workManager = WorkManager.getInstance(context)

        // 1. Petición para descargar el XML del servidor SOAP
        val fetchRequest = OneTimeWorkRequestBuilder<FetchKardexWorker>().build()

        // 2. Petición para parsear el XML y persistirlo en las tablas de Room
        val saveRequest = OneTimeWorkRequestBuilder<SaveKardexWorker>().build()

        /**
         * ENCADENAMIENTO ESTRATÉGICO:
         * El operador .then() garantiza que SaveKardexWorker no se ejecute
         * hasta que FetchKardexWorker termine con éxito y entregue los datos.
         */
        workManager.beginWith(fetchRequest)
            .then(saveRequest)
            .enqueue()
    }
}