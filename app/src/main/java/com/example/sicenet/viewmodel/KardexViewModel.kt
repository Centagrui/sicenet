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

class KardexViewModel(
    private val localRepository: SicenetLocalRepository,
    private val context: Context
) : ViewModel() {

    // Observamos el flujo de datos de Room (se actualiza solo)
    val listaKardex: Flow<List<Kardex>> = localRepository.kardex

    // Función para refrescar los datos usando los Workers que ya tienes
    fun refrescarKardex() {
        val workManager = WorkManager.getInstance(context)

        // Encadenamos: Primero traer del servidor, luego guardar en Room
        val fetchRequest = OneTimeWorkRequestBuilder<FetchKardexWorker>().build()
        val saveRequest = OneTimeWorkRequestBuilder<SaveKardexWorker>().build()

        workManager.beginWith(fetchRequest)
            .then(saveRequest)
            .enqueue()
    }
}