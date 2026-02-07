package com.example.sicenet.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

// Usamos 'object' para aplicar el patrón Singleton.
// Esto garantiza que solo exista una instancia de la conexión en toda la app.
object RetrofitClient {

    // Dirección raíz del servidor
    private const val BASE_URL = "https://sicenet.surguanajuato.tecnm.mx/ws/"

    // ver peticiones y respuestas en el Logcat.
    // El nivel 'BODY' muestra todo: encabezados, URLs y el contenido (XML) de los mensajes.
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Creamos el cliente OkHttp que usará el interceptor.
    // OkHttp es el "motor" encargado de transportar los datos por la red.
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    // Configuramos Retrofit usando el cliente con Logs.
    // 'by lazy' significa que el objeto solo se creará la primera vez que se use
    val apiService: SicenetApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // Le decimos a dónde debe conectarse.
            .client(httpClient) // Le pasamos el motor OkHttp configurado arriba.
            // 'ScalarsConverterFactory' permite recibir respuestas en texto plano,
            // lo cual es necesario porque el servidor responde con XML y no con JSON.
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            // Finalmente, vinculamos esta configuración con la interfaz de los métodos.
            .create(SicenetApiService::class.java)
    }
}