package com.example.sicenet.data.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.sicenet.data.RetrofitClient
import com.example.sicenet.data.SicenetRepository
import com.example.sicenet.data.local.SicenetDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SaveKardexWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        // 1. Recuperamos el XML del FetchKardexWorker
        val xml = inputData.getString("kardex_xml") ?: return Result.failure()

        val database = SicenetDatabase.getDatabase(applicationContext)
        // Usamos el parser del repositorio de red
        val repository = SicenetRepository(RetrofitClient.apiService)

        return try {
            // 2. Procesamos el XML
            val lista = repository.procesarKardex(xml)
            Log.d("DEBUG_WORKER", "Materias extraídas del XML: ${lista.size}")

            if (lista.isNotEmpty()) {
                // 3. Persistencia en Room
                database.sicenetDao().limpiarKardex()
                database.sicenetDao().insertarKardex(lista)

                // 4. GUARDAR FECHA (Punto 2 de la rúbrica)
                // Guardamos el momento exacto de la sincronización para las pantallas
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val fechaActual = sdf.format(Date())

                val sharedPref = applicationContext.getSharedPreferences("sicenet_prefs", Context.MODE_PRIVATE)
                sharedPref.edit().putString("fecha_finales", fechaActual).apply()
                sharedPref.edit().putString("fecha_kardex", fechaActual).apply()

                Log.d("DEBUG_WORKER", "Kárdex guardado exitosamente el: $fechaActual")
                Result.success()
            } else {
                Log.e("DEBUG_WORKER", "Lista vacía: El XML no contenía registros válidos.")
                Result.failure()
            }
        } catch (e: Exception) {
            Log.e("DEBUG_WORKER", "Error al procesar/guardar Kárdex: ${e.message}")
            Result.failure()
        }
    }
}