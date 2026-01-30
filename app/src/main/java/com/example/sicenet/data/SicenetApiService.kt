package com.example.sicenet.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface SicenetApiService {
    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: http://tempuri.org/accesoLogin"
    )
    @POST("ws/wsalumnos.asmx")
    suspend fun accesoLogin(@Body body: String): Response<String>

    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: http://tempuri.org/getAlumnoAcademicoWithLineamiento"
    )
    @POST("ws/wsalumnos.asmx")
    suspend fun getPerfil(@Header("Cookie") cookie: String, @Body body: String): Response<String>
}