package com.example.sicenet.data.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.sicenet.data.RetrofitClient
import com.example.sicenet.data.SicenetRepository
import com.example.sicenet.data.local.SicenetDatabase
import java.io.File

class SaveKardexWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        return try {
            val filePath = inputData.getString("kardex_file_path") ?: return Result.failure()
            val file = File(filePath)

            if (!file.exists()) {
                Log.e("DEBUG_SAVE", "El archivo temporal no existe")
                return Result.failure()
            }

            // Pasamos el archivo a un String para poder procesarlo.
            val xml = file.readText()

//limpiar datos y permiso para guardar en db
            val repository = SicenetRepository(RetrofitClient.apiService)
            val baseDatos = SicenetDatabase.getDatabase(applicationContext)
            val materiasProcesadas = repository.procesarKardex(xml)

            // ACTUALIZAR LA BASE DE DATOS
            if (materiasProcesadas.isNotEmpty()) {

                baseDatos.sicenetDao().limpiarKardex()

                baseDatos.sicenetDao().insertarKardex(materiasProcesadas)
                Log.d("DEBUG_SAVE", "Kardex guardado en Room exitosamente")

                //Guardamos fecha actual
                val sharedPref = applicationContext.getSharedPreferences("sicenet_prefs", Context.MODE_PRIVATE)
                sharedPref.edit().putString("fecha_kardex", java.util.Date().toString()).apply()
            }

            // borramos archivo temporal de fetch
            file.delete()

            Result.success()
        } catch (e: Exception) {
            Log.e("DEBUG_SAVE", "Error en SaveKardexWorker: ${e.message}")
            Result.failure()
        }
    }
}