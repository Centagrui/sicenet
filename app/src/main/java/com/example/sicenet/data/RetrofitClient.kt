package com.example.sicenet.data

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://sicenet.surguanajuato.tecnm.mx/"

    val apiService: SicenetApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .build()
            .create(SicenetApiService::class.java)
    }
}