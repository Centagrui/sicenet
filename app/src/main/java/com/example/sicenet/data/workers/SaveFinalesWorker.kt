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
        // 1. Recuperar el XML del Worker anterior
        val xml = inputData.getString("xml_finales") ?: return Result.failure()

        val database = SicenetDatabase.getDatabase(applicationContext)
        val repository = SicenetRepository(RetrofitClient.apiService)
// Dentro de SaveFinalesWorker.kt -> doWork()
        return try {
            val repository = SicenetRepository(RetrofitClient.apiService)
            val database = SicenetDatabase.getDatabase(applicationContext)
            val dao = database.sicenetDao()

            // 1. Obtenemos las materias de la Carga Académica (que ya están en Room)
            // Usamos first() para obtener la lista actual del Flow
            val materiasCarga = dao.obtenerCargaDirecta()

            // 2. Procesamos el XML de Finales que llegó del servidor
            val finalesServidor = repository.procesarCalificacionesFinales(xml)

            // 3. Creamos la lista final combinada
            val listaParaGuardar = materiasCarga.map { materiaCarga ->
                // Buscamos si esta materia de la carga existe en lo que mandó el servidor
                val califEncontrada = finalesServidor.find {
                    it.materia.uppercase().trim() == materiaCarga.nombre.uppercase().trim()
                }

                CalificacionFinal(
                    materia = materiaCarga.nombre,
                    calificacion = califEncontrada?.calificacion
                        ?: "S/N", // Si no existe, ponemos S/N
                   // creditos = materiaCarga.creditos
                )
            }

            // 4. Guardamos en la tabla de finales
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