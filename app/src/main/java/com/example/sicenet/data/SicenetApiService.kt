package com.example.sicenet.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface SicenetApiService {

    // @Headers  encabezados necesarios para que el servidor entienda la petición.
    // Definimos que es un archivo XML y se define la acción SOAP.
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
        // enviamos el identificador de sesión que el servidor nos dio.
        @Header("Cookie") cookie: String,
        //  cuerpo de la petición, SOAP en formato String.
        @Body body: String
    ): Response<String>

    // Endpont para obtener los datos académicos y de perfil del alumno.
    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/getAlumnoAcademicoWithLineamiento\""
    )
    @POST("wsalumnos.asmx")
    suspend fun getPerfil(
        // la Cookie para demostrar que el alumno ya está logueado.
        @Header("Cookie") cookie: String,
        @Body body: String
    ): Response<String>
}