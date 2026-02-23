package com.example.sicenet.data.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.sicenet.data.RetrofitClient
import com.example.sicenet.data.SicenetRepository
import com.example.sicenet.data.local.SicenetDatabase
import java.text.SimpleDateFormat
import java.util.*

class SaveFinalesWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        // 1. Obtener el XML del paso anterior
        val xml = inputData.getString("xml_finales")

        if (xml == null) {
            Log.e("DEBUG_LOGCAT", "ERROR: No se recibió el XML (xml_finales es null)")
            return Result.failure()
        }

        return try {
            val db = SicenetDatabase.getDatabase(applicationContext)
            val repository = SicenetRepository(RetrofitClient.apiService)

            // 2. Procesar el XML
            // NOTA: Asegúrate que procesarKardex funcione con el XML de finales
            val listaFinales = repository.procesarKardex(xml)

            Log.d("DEBUG_LOGCAT", "XML Recibido (primeros 100 caracteres): ${xml.take(100)}")
            Log.d("DEBUG_LOGCAT", "Cantidad de materias detectadas: ${listaFinales.size}")

            if (listaFinales.isNotEmpty()) {
                // 3. Guardar en Base de Datos (Esto activa el Flow en la UI)
                // Usamos limpiarKardex e insertarKardex porque usas el modelo Kardex para las finales
                db.sicenetDao().limpiarKardex()
                db.sicenetDao().insertarKardex(listaFinales)

                // 4. Guardar la fecha de éxito
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                val fechaActual = sdf.format(Date())

                val sharedPref = applicationContext.getSharedPreferences("sicenet_prefs", Context.MODE_PRIVATE)
                sharedPref.edit().putString("fecha_finales", fechaActual).apply()

                Log.d("DEBUG_LOGCAT", "¡ÉXITO! Fecha guardada: $fechaActual")
                Result.success()
            } else {
                Log.e("DEBUG_LOGCAT", "FALLO: El parser devolvió una lista vacía. Revisa el Regex en el Repositorio.")
                Result.failure()
            }
        } catch (e: Exception) {
            Log.e("DEBUG_LOGCAT", "ERROR CRÍTICO en doWork: ${e.message}")
            Result.failure()
        }
    }
}