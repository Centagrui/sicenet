package com.example.sicenet.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Define las llamadas directas al Servidor (Web Service).
 * Cada función representa una consulta específica que el servidor sabe responder.
 */
interface SicenetApiService {

    // --- ACCESO LOGIN ---
    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/accesoLogin\"" // Indica al servidor qué función ejecutar
    )
    @POST("wsalumnos.asmx") // El archivo en el servidor que recibe las peticiones
    suspend fun accesoLogin(@Body body: String): Response<String>

    // Sobrecarga de login para cuando ya tenemos una sesión activa (Cookie)
    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/accesoLogin\""
    )
    @POST("wsalumnos.asmx")
    suspend fun accesoLogin(
        @Header("Cookie") cookie: String, // La "llave" de sesión
        @Body body: String               // El XML con las credenciales
    ): Response<String>

    // --- PERFIL ---
    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/getAlumnoAcademicoWithLineamiento\""
    )
    @POST("wsalumnos.asmx")
    suspend fun getPerfil(
        @Header("Cookie") cookie: String,
        @Body body: String
    ): Response<String>

    // --- 1. CARGA ACADÉMICA (Materias actuales) ---
    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/getCargaAcademicaByAlumno\""
    )
    @POST("wsalumnos.asmx")
    suspend fun getCargaAcademica(
        @Header("Cookie") cookie: String,
        @Body body: String
    ): Response<String>

    // --- 2. KARDEX (Historial completo) ---
    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/getAllKardexConPromedioByAlumno\""
    )
    @POST("wsalumnos.asmx")
    suspend fun getKardex(
        @Header("Cookie") cookie: String,
        @Body body: String
    ): Response<String>

    // --- 3. CALIFICACIONES POR UNIDAD (Parciales) ---
    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/getCalifUnidadesByAlumno\""
    )
    @POST("wsalumnos.asmx")
    suspend fun getCalificacionesUnidades(
        @Header("Cookie") cookie: String,
        @Body body: String
    ): Response<String>

    // --- 4. CALIFICACIÓN FINAL (Promedios) ---
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