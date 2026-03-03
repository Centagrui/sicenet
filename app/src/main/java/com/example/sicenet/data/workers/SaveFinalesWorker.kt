package com.example.sicenet.data.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.sicenet.data.RetrofitClient
import com.example.sicenet.data.SicenetRepository
import com.example.sicenet.data.local.SicenetDatabase
import com.example.sicenet.model.CalificacionFinal
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SaveFinalesWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result {

        val xml = inputData.getString("xml_finales") ?: return Result.failure()

        val database = SicenetDatabase.getDatabase(applicationContext)
        val repository = SicenetRepository(RetrofitClient.apiService)
        return try {
            val repository = SicenetRepository(RetrofitClient.apiService)
            val database = SicenetDatabase.getDatabase(applicationContext)
            val dao = database.sicenetDao()

            val materiasCarga = dao.obtenerCargaDirecta()


            val finalesServidor = repository.procesarCalificacionesFinales(xml)


            val listaParaGuardar = materiasCarga.map { materiaCarga ->

                val califEncontrada = finalesServidor.find {
                    it.materia.uppercase().trim() == materiaCarga.nombre.uppercase().trim()
                }

                CalificacionFinal(
                    materia = materiaCarga.nombre,
                    calificacion = califEncontrada?.calificacion
                        ?: "S/N",
                )
            }


            if (listaParaGuardar.isNotEmpty()) {
                dao.limpiarFinales()
                dao.insertarFinales(listaParaGuardar)
                Log.d("DEBUG_SAVE", "Finales combinadas guardadas: ${listaParaGuardar.size}")
                Result.success()
            } else {
                Log.e("DEBUG_SAVE", "No se pudo combinar: Carga Académica vacía en DB")
                Result.failure()
            }
        } catch (e: Exception) {
            Log.e("DEBUG_SAVE", "Error: ${e.message}")
            Result.failure()
        }
    }
}