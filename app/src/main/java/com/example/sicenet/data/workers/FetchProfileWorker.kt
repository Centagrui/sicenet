package com.example.sicenet.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.sicenet.data.RetrofitClient
import com.example.sicenet.data.SicenetRepository

class FetchProfileWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val repository = SicenetRepository(RetrofitClient.apiService)

        return try {
            val xml = repository.recuperarPerfil()
            if (xml != null) {
                // Pasamos el XML como salida para el siguiente workeeer
                val outputData = workDataOf("perfil_xml" to xml)
                Result.success(outputData)
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            Result.retry()
        }
    }
}