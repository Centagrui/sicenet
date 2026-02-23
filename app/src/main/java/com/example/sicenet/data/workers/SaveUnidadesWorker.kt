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
        // Recuperamos el XML del Worker anterior
        val xml = inputData.getString("unidades_xml") ?: return Result.failure()

        val database = SicenetDatabase.getDatabase(applicationContext)
        val repository = SicenetRepository(RetrofitClient.apiService)

        return try {
            // 1. Procesar el XML para convertirlo en Lista de Objetos
            val listaUnidades = repository.procesarUnidades(xml)

            if (listaUnidades.isNotEmpty()) {
                // 2. Guardar en la DB Local (Repository Local indirecto vía DAO)
                database.sicenetDao().limpiarUnidades()
                database.sicenetDao().insertarUnidades(listaUnidades)

                // 3. Guardar fecha de actualización (Requisito 2b de la rúbrica)
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