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
        // 1. PRIMERO declaramos e inicializamos lo que vamos a usar
        val repository = SicenetRepository(RetrofitClient.apiService)
        val database = SicenetDatabase.getDatabase(applicationContext)

        return try {
            // 2. Intentamos recuperar el Kardex
            val respuestaXml = repository.recuperarKardex()

            // 3. Manejo de sesión expirada o nula
            if (respuestaXml == null || respuestaXml.contains("error", ignoreCase = true)) {
                Log.e("DEBUG_XML", "Sesión inválida o respuesta nula")
                // Opcional: Podrías limpiar el perfil aquí si detectas error de sesión
                return Result.failure()
            }

            // 4. Si todo está bien, guardamos en archivo temporal (para evitar el límite de 10KB)
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