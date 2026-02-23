package com.example.sicenet.data.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.sicenet.data.RetrofitClient
import com.example.sicenet.data.SicenetRepository
import com.example.sicenet.data.local.SicenetDatabase

class SaveKardexWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {
        // 1. Recuperamos el XML que nos pasó el FetchKardexWorker
        val xml = inputData.getString("kardex_xml") ?: return Result.failure()

        val database = SicenetDatabase.getDatabase(applicationContext)
        val repository = SicenetRepository(RetrofitClient.apiService)

        return try {
            // 2. Procesamos el XML usando el Regex que ya arreglamos
            val lista = repository.procesarKardex(xml)
            Log.d("DEBUG_WORKER", "Materias extraídas del XML: ${lista.size}")

            if (lista.isNotEmpty()) {
                // 3. Limpiamos datos viejos e insertamos los nuevos
                database.sicenetDao().limpiarKardex()
                database.sicenetDao().insertarKardex(lista)

                // 4. Verificación final en la base de datos
                val conteo = database.sicenetDao().contarKardex()
                Log.d("DEBUG_WORKER", "Materias confirmadas en DB: $conteo")

                Result.success()
            } else {
                Log.e("DEBUG_WORKER", "El procesador devolvió una lista vacía. Revisa el Regex.")
                Result.failure()
            }
        } catch (e: Exception) {
            Log.e("DEBUG_WORKER", "Error fatal al guardar el Kárdex: ${e.message}")
            Result.failure()
        }
    }
}