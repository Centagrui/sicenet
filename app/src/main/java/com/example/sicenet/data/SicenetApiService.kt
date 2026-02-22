package com.example.sicenet.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface SicenetApiService {

    // Login (Ya lo tenías)
    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/accesoLogin\""
    )
    @POST("wsalumnos.asmx")
    suspend fun accesoLogin(@Body body: String): Response<String>

    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/accesoLogin\""
    )
    @POST("wsalumnos.asmx")
    suspend fun accesoLogin(
        @Header("Cookie") cookie: String,
        @Body body: String
    ): Response<String>

    // Perfil (Ya lo tenías)
    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/getAlumnoAcademicoWithLineamiento\""
    )
    @POST("wsalumnos.asmx")
    suspend fun getPerfil(
        @Header("Cookie") cookie: String,
        @Body body: String
    ): Response<String>

    // --- NUEVOS MÉTODOS PARA LA ENTREGA 2 ---

    // 1. Carga Académica
    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/getCargaAcademicaByAlumno\""
    )
    @POST("wsalumnos.asmx")
    suspend fun getCargaAcademica(
        @Header("Cookie") cookie: String,
        @Body body: String
    ): Response<String>

    // 2. Kardex
    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/getAllKardexConPromedioByAlumno\""
    )
    @POST("wsalumnos.asmx")
    suspend fun getKardex(
        @Header("Cookie") cookie: String,
        @Body body: String
    ): Response<String>

    // 3. Calificaciones por Unidad
    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/getCalifUnidadesByAlumno\""
    )
    @POST("wsalumnos.asmx")
    suspend fun getCalificacionesUnidades(
        @Header("Cookie") cookie: String,
        @Body body: String
    ): Response<String>

    // 4. Calificación Final
    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/getAllCalifFinalByAlumnos\""
    )
    @POST("wsalumnos.asmx")
    suspend fun getCalificacionesFinales(
        @Header("Cookie") cookie: String,
        @Body body: String
    ): Response<String>
}