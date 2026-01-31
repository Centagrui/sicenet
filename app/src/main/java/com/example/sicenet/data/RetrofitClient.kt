package com.example.sicenet.data

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://sicenet.surguanajuato.tecnm.mx/ws/"
    // 1. Configuramos el Interceptor para ver peticiones y respuestas en el Logcat
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // 2. Creamos el cliente OkHttp que usará el interceptor
    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    // 3. Configuramos Retrofit usando el cliente con Logs
    val apiService: SicenetApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(SicenetApiService::class.java)
    }
}