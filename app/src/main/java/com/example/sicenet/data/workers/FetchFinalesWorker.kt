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

            // 1. Recuperamos el XML (que puede ser nulo)
            val xml: String? = repository.recuperarKardex()

            // 2. Usamos el operador ?. (safe call) y verificamos que no sea nulo
            if (xml?.contains("getResumenAcademicoResult") == true) {
                Result.success(workDataOf("xml_finales" to xml))
            } else {
                // Si es nulo o no contiene el tag, fallamos
                Result.failure()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}