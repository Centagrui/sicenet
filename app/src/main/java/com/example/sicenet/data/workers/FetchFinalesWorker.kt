package com.example.sicenet.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.sicenet.data.RetrofitClient
import com.example.sicenet.data.SicenetRepository

class FetchFinalesWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        return try {
            val repository = SicenetRepository(RetrofitClient.apiService)

            // Llamamos al método específico para Calificaciones Finales
            val respuestaXml = repository.recuperarCalificacionesFinales()

            // Pasamos el XML al siguiente Worker (SaveFinalesWorker)
            val outputData = workDataOf("xml_finales" to respuestaXml)
            Result.success(outputData)
        } catch (e: Exception) {
            Result.failure()
        }
    }
}