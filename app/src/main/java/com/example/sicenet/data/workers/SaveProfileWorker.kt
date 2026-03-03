package com.example.sicenet.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.sicenet.data.local.SicenetDatabase
import com.example.sicenet.data.SicenetRepository
import com.example.sicenet.data.RetrofitClient

class SaveProfileWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val xml = inputData.getString("perfil_xml") ?: return Result.failure()
        val database = SicenetDatabase.getDatabase(applicationContext)
        val repository = SicenetRepository(RetrofitClient.apiService)


        return try {
            val perfil = repository.procesarDatosPerfil(xml)
            if (perfil != null) {
                database.sicenetDao().insertarPerfil(perfil)
                Result.success()
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }
}