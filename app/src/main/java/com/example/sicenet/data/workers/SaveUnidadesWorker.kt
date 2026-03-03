package com.example.sicenet.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.sicenet.data.RetrofitClient
import com.example.sicenet.data.SicenetRepository
import com.example.sicenet.data.local.SicenetDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SaveUnidadesWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val xml = inputData.getString("unidades_xml") ?: return Result.failure()

        val database = SicenetDatabase.getDatabase(applicationContext)
        val repository = SicenetRepository(RetrofitClient.apiService)

        return try {

            val listaUnidades = repository.procesarUnidades(xml)

            if (listaUnidades.isNotEmpty()) {

                database.sicenetDao().limpiarUnidades()
                database.sicenetDao().insertarUnidades(listaUnidades)

                // 3. Guardar fecha de actualización
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                val fechaActual = sdf.format(Date())

                val sharedPref = applicationContext.getSharedPreferences("sicenet_prefs", Context.MODE_PRIVATE)
                sharedPref.edit().putString("fecha_unidades", fechaActual).apply()

                Result.success()
            } else {
                Result.failure()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }
}