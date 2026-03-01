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

class SaveFinalesWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        // 1. Recuperar el XML del Worker anterior (FetchFinalesWorker)
        val xml = inputData.getString("xml_finales") ?: return Result.failure()

        val database = SicenetDatabase.getDatabase(applicationContext)
        val repository = SicenetRepository(RetrofitClient.apiService)

        return try {
            // 2. Procesar el XML
            val listaFinales = repository.procesarCalificacionesFinales(xml)

            if (listaFinales.isNotEmpty()) {
                // 3. Persistencia en Room
                // Nota: Asegúrate de tener una tabla específica para Finales o usa la de Kardex si así lo diseñaste
                database.sicenetDao().limpiarKardex()
                database.sicenetDao().insertarKardex(listaFinales)

                // 4. Guardar fecha de actualización (Mismo formato que Unidades)
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val fechaActual = sdf.format(Date())

                val sharedPref = applicationContext.getSharedPreferences("sicenet_prefs", Context.MODE_PRIVATE)
                sharedPref.edit().putString("fecha_finales", fechaActual).apply()

                Log.d("DEBUG_SAVE", "Finales guardadas exitosamente: $fechaActual")
                Result.success()
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            Log.e("DEBUG_SAVE", "Error en SaveFinalesWorker: ${e.message}")
            Result.failure()
        }
    }
}