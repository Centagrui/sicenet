package com.example.sicenet.data.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.sicenet.data.RetrofitClient
import com.example.sicenet.data.SicenetRepository
import com.example.sicenet.data.local.SicenetDatabase

class FetchKardexWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val repository = SicenetRepository(RetrofitClient.apiService)
        val database = SicenetDatabase.getDatabase(applicationContext)

        return try {
            val xml = repository.recuperarKardex()

            if (xml != null) {
                // Procesamos el XML aquí mismo para no tener que pasarlo
                val lista = repository.procesarKardex(xml)

                if (lista.isNotEmpty()) {
                    database.sicenetDao().limpiarKardex()
                    database.sicenetDao().insertarKardex(lista)
                    Log.d("DEBUG_KARDEX", "¡ÉXITO! ${lista.size} materias guardadas directamente.")
                    Result.success()
                } else {
                    Log.e("DEBUG_KARDEX", "No se encontraron materias en el XML")
                    Result.failure()
                }
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            Log.e("DEBUG_KARDEX", "Error: ${e.message}")
            Result.retry()
        }
        // Dentro de doWork
        val xmlFull = repository.recuperarKardex() ?: return Result.failure()
// Limpiamos el XML para que quepa en los 10KB de salida
        val jsonLimpio = xmlFull.substringAfter("<getAllKardexConPromedioByAlumnoResult>")
            .substringBefore("</getAllKardexConPromedioByAlumnoResult>")

        return Result.success(workDataOf("kardex_json" to jsonLimpio)) // Datos de SALIDA
    }
}