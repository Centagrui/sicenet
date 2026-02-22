package com.example.sicenet.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.sicenet.data.RetrofitClient
import com.example.sicenet.data.SicenetRepository
import com.example.sicenet.data.local.SicenetDatabase

class SaveCargaWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        // 1. Recibimos el XML del worker anterior
        val xml = inputData.getString("carga_xml") ?: return Result.failure()

        val database = SicenetDatabase.getDatabase(applicationContext)
        val repository = SicenetRepository(RetrofitClient.apiService)

        return try {
            // 2. Procesamos el XML para convertirlo en Lista de Materias
            val listaMaterias = repository.procesarCargaAcademica(xml)

            if (listaMaterias.isNotEmpty()) {
                // 3. Guardamos en la base de datos local
                database.sicenetDao().limpiarCarga() // Borramos lo viejo
                database.sicenetDao().insertarCarga(listaMaterias) // Insertamos lo nuevo
                Result.success()
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }
}