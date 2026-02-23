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
        val xml = inputData.getString("kardex_xml") ?: return Result.failure()
        val database = SicenetDatabase.getDatabase(applicationContext)
        val repository = SicenetRepository(RetrofitClient.apiService)

        return try {
            val lista = repository.procesarKardex(xml)
            Log.d("DEBUG_SAVE", "Materias extraídas: ${lista.size}")

            if (lista.isNotEmpty()) {
                // 1. Guardar en Room
                database.sicenetDao().limpiarKardex()
                database.sicenetDao().insertarKardex(lista)

                // 2. Preparar la fecha
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                val fechaActual = sdf.format(Date())

                // 3. Guardar en SharedPreferences
                val sharedPref = applicationContext.getSharedPreferences("sicenet_prefs", Context.MODE_PRIVATE)
                with(sharedPref.edit()) {
                    // GUARDAMOS AMBAS PARA QUE AMBAS PANTALLAS SE ACTUALICEN
                    putString("fecha_kardex", fechaActual)
                    putString("fecha_finales", fechaActual)
                    apply()
                }

                Log.d("DEBUG_SAVE", "¡Todo guardado! Fecha: $fechaActual")
                Result.success()
            } else {
                Log.e("DEBUG_SAVE", "Lista vacía")
                Result.failure()
            }
        } catch (e: Exception) {
            Log.e("DEBUG_SAVE", "Error: ${e.message}")
            Result.failure()
        }
    }
}