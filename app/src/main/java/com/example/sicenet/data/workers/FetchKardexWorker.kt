package com.example.sicenet.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.sicenet.data.RetrofitClient
import com.example.sicenet.data.SicenetRepository

class FetchKardexWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val repository = SicenetRepository(RetrofitClient.apiService)
        return try {
            val xml = repository.recuperarKardex()
            if (xml != null) {
                Result.success(workDataOf("kardex_xml" to xml))
            } else Result.failure()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}