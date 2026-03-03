package com.example.sicenet.data.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.sicenet.data.RetrofitClient
import com.example.sicenet.data.SicenetRepository
import com.example.sicenet.data.local.SicenetDatabase // IMPORTANTE: Importar tu DB
import java.io.File

class FetchKardexWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        val repository = SicenetRepository(RetrofitClient.apiService)
        val database = SicenetDatabase.getDatabase(applicationContext)

        return try {

            val respuestaXml = repository.recuperarKardex()
            if (respuestaXml == null || respuestaXml.contains("error", ignoreCase = true)) {
                Log.e("DEBUG_XML", "Sesión inválida o respuesta nula")
                return Result.failure()
            }
            val file = File(applicationContext.cacheDir, "kardex_temp.xml")
            file.writeText(respuestaXml)

            // 5. Pasamos la RUTA al siguiente Worker
            val outputData = workDataOf("kardex_file_path" to file.absolutePath)

            Log.d("DEBUG_XML", "Kardex XML obtenido exitosamente")
            Result.success(outputData)

        } catch (e: Exception) {
            Log.e("DEBUG_XML", "Error crítico en FetchKardexWorker: ${e.message}")
            Result.failure()
        }
    }
}