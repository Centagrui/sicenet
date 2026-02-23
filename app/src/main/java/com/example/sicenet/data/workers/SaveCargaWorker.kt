package com.example.sicenet.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.sicenet.data.RetrofitClient
import com.example.sicenet.data.SicenetRepository
import com.example.sicenet.data.local.SicenetDatabase

class SaveCargaWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    // SaveCargaWorker.kt
    override suspend fun doWork(): Result {
        val xml = inputData.getString("carga_xml") ?: return Result.failure()
        val database = SicenetDatabase.getDatabase(applicationContext)
        val repository = SicenetRepository(RetrofitClient.apiService)

        return try {
            val listaMaterias = repository.procesarCargaAcademica(xml)

            // ESTO TE DIRÁ SI EL PARSER FUNCIONÓ
            android.util.Log.d("DEBUG_SAVE", "Materias procesadas: ${listaMaterias.size}")

            if (listaMaterias.isNotEmpty()) {
                database.sicenetDao().limpiarCarga()
                database.sicenetDao().insertarCarga(listaMaterias)
                android.util.Log.d("DEBUG_SAVE", "¡Datos guardados en Room exitosamente!")
                Result.success()
            } else {
                android.util.Log.e("DEBUG_SAVE", "La lista de materias está VACÍA tras el proceso")
                Result.failure()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }
}