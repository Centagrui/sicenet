package com.example.sicenet.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.sicenet.data.RetrofitClient
import com.example.sicenet.data.SicenetRepository

class FetchUnidadesWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        return try {
            val repository = SicenetRepository(RetrofitClient.apiService)

            // Llamamos al método que consulta el SOAP (asegúrate de tenerlo en tu Repo)
            val respuestaXml = repository.recuperarCalificacionesUnidades()
            // Pasamos el XML al siguiente Worker como indica la rúbrica (2b)
            val outputData = workDataOf("unidades_xml" to respuestaXml)
            Result.success(outputData)
        } catch (e: Exception) {
            Result.failure()
        }
    }
}