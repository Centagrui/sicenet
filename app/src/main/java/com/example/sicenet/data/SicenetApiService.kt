package com.example.sicenet.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

// Definimos los puntos de entrada del API del Sicenet
interface SicenetApiService {

    // @Headers  encabezados necesarios para que el servidor entienda la petición.
    // Definimos que es un archivo XML y se define la acción SOAP.
    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPAction: \"http://tempuri.org/accesoLogin\""
    )
    // @POST nos indica que enviaremos datos al servidor.
    // 'suspend' nos indica que la función se ejecutará en una corrutina para no trabar la app.
    @POST("wsalumnos.asmx")
    suspend fun accesoLogin(@Body body: String): Response<String>

    // sobrecarga del método Login para cuando ya tenemos una sesión iniciada.
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

    // Endpoint para obtener los datos académicos y de perfil del alumno.
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