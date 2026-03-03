package com.example.sicenet.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

/**
 * Usamos 'object' para aplicar el patrón Singleton.
 * Esto garantiza que solo exista una instancia de la conexión en toda la app,
 * ahorrando memoria y evitando múltiples conexiones abiertas al servidor.
 */
object RetrofitClient {

    // Dirección raíz (URL Base) de los Web Services del Tecnológico.
    private const val BASE_URL = "https://sicenet.surguanajuato.tecnm.mx/ws/"

    /**
     * El LoggingInterceptor es como una "cámara de seguridad".
     * Permite ver en el Logcat de Android Studio exactamente qué estás enviando
     * y qué te está respondiendo el servidor (útil para ver el XML de error o éxito).
     */
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // BODY muestra el contenido completo del mensaje.
    }

    /**
     * OkHttp es el cliente de bajo nivel que maneja la conexión física.
     * Aquí le añadimos el interceptor de logs definido arriba.
     */
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    /**
     * 'by lazy' significa que el objeto 'apiService' no se crea hasta que alguien lo pida.
     * Esto mejora el rendimiento de inicio de la aplicación.
     */
    val apiService: SicenetApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // Configura la dirección base.
            .client(httpClient) // Usa el motor OkHttp con logs.
            /**
             * IMPORTANTE: 'ScalarsConverterFactory'
             * Por defecto, Retrofit espera JSON. Como el SICENET usa SOAP/XML,
             * necesitamos este convertidor para que nos entregue la respuesta
             * como un String de texto puro (Scalar) que luego procesaremos manualmente.
             */
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            // Crea la implementación de la interfaz de servicios que definiste.
            .create(SicenetApiService::class.java)
    }
}