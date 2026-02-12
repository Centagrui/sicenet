package com.example.sicenet.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

// Usamos 'object' para aplicar el patrón Singleton.
// Esto garantiza que solo exista una instancia de la conexión en toda la app.
object RetrofitClient {
    private const val BASE_URL = "https://sicenet.surguanajuato.tecnm.mx/ws/"

    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // OkHttp nos ayuda a traer los datos de la red
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    // Configuramos Retrofit usando el cliente con Logs.
    // by lazy para crear el objeto la primera vez
    val apiService: SicenetApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) //
            .client(httpClient)
            //  permite recibir respuestas en texto plano,
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            // Finalmente, vinculamos esta configuración con la interfaz de los métodos.
            .create(SicenetApiService::class.java)
    }
}