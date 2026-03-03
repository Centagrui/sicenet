package com.example.sicenet.data.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.sicenet.data.RetrofitClient
import com.example.sicenet.data.SicenetRepository

class FetchCargaWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {

    // Este es el método principal que se ejecuta cuando el sistema lanza el Worker.
    override suspend fun doWork(): Result {
        // Inicializamos el repositorio pasándole el servicio de Retrofit.
        val repository = SicenetRepository(RetrofitClient.apiService)

        return try {
            // Intentamos obtener el XML de la carga académica desde el servidor (SICENET).
            val xml = repository.recuperarCargaAcademica()

            if (xml != null) {
                // depurar
                android.util.Log.d("DEBUG_XML", "XML Carga Recibido: $xml")

                Result.success(workDataOf("carga_xml" to xml))
            } else {
                android.util.Log.e("DEBUG_XML", "XML Carga llegó NULO")
                Result.failure()
            }
        } catch (e: Exception) {
            android.util.Log.e("DEBUG_XML", "Error en Fetch: ${e.message}")
            Result.retry()
        }
    }
}