package com.example.sicenet.data.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.sicenet.data.RetrofitClient
import com.example.sicenet.data.SicenetRepository
import com.example.sicenet.data.local.SicenetDatabase
import java.io.File

class SaveKardexWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        return try {
            // 1. Recibimos la ruta del archivo
            val filePath = inputData.getString("kardex_file_path") ?: return Result.failure()
            val file = File(filePath)

            // Verificamos que el archivo realmente exista
            if (!file.exists()) {
                Log.e("DEBUG_SAVE", "El archivo temporal no existe")
                return Result.failure()
            }

            // 2. Leemos todo el XML gigante desde el archivo
            val xml = file.readText()

            // 3. Procesamos y guardamos en Room
            val repository = SicenetRepository(RetrofitClient.apiService)
            val baseDatos = SicenetDatabase.getDatabase(applicationContext)

            val materiasProcesadas = repository.procesarKardex(xml)

            if (materiasProcesadas.isNotEmpty()) {
                baseDatos.sicenetDao().limpiarKardex()
                baseDatos.sicenetDao().insertarKardex(materiasProcesadas)
                Log.d("DEBUG_SAVE", "Kardex guardado en Room exitosamente")

                // Guardar la fecha de actualización
                val sharedPref = applicationContext.getSharedPreferences("sicenet_prefs", Context.MODE_PRIVATE)
                sharedPref.edit().putString("fecha_kardex", java.util.Date().toString()).apply()
            }

            // 4. Limpieza: Borramos el archivo para no saturar la memoria del teléfono
            file.delete()

            Result.success()
        } catch (e: Exception) {
            Log.e("DEBUG_SAVE", "Error en SaveKardexWorker: ${e.message}")
            Result.failure()
        }
    }
}