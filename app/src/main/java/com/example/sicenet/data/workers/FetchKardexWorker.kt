package com.example.sicenet.data.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.sicenet.data.RetrofitClient
import com.example.sicenet.data.SicenetRepository
import java.io.File

class FetchKardexWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        return try {
            val repository = SicenetRepository(RetrofitClient.apiService)
            val respuestaXml = repository.recuperarKardex()

            if (respuestaXml != null) {
                // 1. Crear un archivo temporal en la memoria caché
                val file = File(applicationContext.cacheDir, "kardex_temp.xml")
                file.writeText(respuestaXml)

                // 2. Pasamos la RUTA del archivo, que pesa solo unos cuantos bytes
                val outputData = workDataOf("kardex_file_path" to file.absolutePath)

                Log.d("DEBUG_XML", "Kardex XML guardado en caché y ruta enviada")
                Result.success(outputData)
            } else {
                Log.e("DEBUG_XML", "El XML del Kardex llegó nulo")
                Result.failure()
            }
        } catch (e: Exception) {
            Log.e("DEBUG_XML", "Error en FetchKardexWorker: ${e.message}")
            Result.failure()
        }
    }
}