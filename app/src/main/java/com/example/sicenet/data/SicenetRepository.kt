package com.example.sicenet.data

import android.util.Log

class SicenetRepository(private val api: SicenetApiService) {
    // Usamos una variable volátil para asegurar que se lea correctamente entre hilos
    private var sessionCookie: String? = null

    suspend fun login(matricula: String, contrasenia: String): Boolean {
        val soapLogin = """
            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <accesoLogin xmlns="http://tempuri.org/">
                  <strMatricula>$matricula</strMatricula>
                  <strContrasenia>$contrasenia</strContrasenia>
                  <tipoUsuario>ALUMNO</tipoUsuario>
                </accesoLogin>
              </soap:Body>
            </soap:Envelope>
        """.trimIndent()

        return try {
            // Enviamos la cookie "detect" para saltar el filtro de ASP.NET
            val response = api.accesoLogin("ASP.NET_SessionId=detect", soapLogin)

            if (response.isSuccessful) {
                val body = response.body() ?: ""
                Log.d("SICENET_LOGIN", "Body: $body")

                // 1. CAPTURAR LA COOKIE PRIMERO
                // El servidor siempre nos da una sesión, hay que guardarla para usarla en getPerfil
                val rawCookie = response.headers()["Set-Cookie"]
                if (rawCookie != null) {
                    sessionCookie = rawCookie.split(";")[0]
                    Log.d("COOKIE_DEBUG", "Sesión capturada: $sessionCookie")
                }

                // 2. VALIDAR EL ACCESO
                // Como el servidor responde con {"acceso":true,...}, buscamos esa cadena
                if (body.contains("\"acceso\":true")) {
                    Log.d("SICENET", "¡Login exitoso detectado!")
                    return true
                } else {
                    Log.e("SICENET", "Credenciales incorrectas o acceso denegado")
                    return false
                }
            } else {
                Log.e("SICENET", "Error en la petición: ${response.code()}")
                return false
            }
            false
        } catch (e: Exception) {
            Log.e("SICENET", "Error en login: ${e.message}")
            false
        }
    }

    suspend fun recuperarPerfil(): String? {
        // Verificamos que tengamos la cookie antes de disparar la petición
        val cookieActual = sessionCookie ?: run {
            Log.e("SICENET", "Intento de recuperar perfil sin cookie")
            return "Error: Sin Sesión"
        }

        val soapPerfil = """
            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <getAlumnoAcademicoWithLineamiento xmlns="http://tempuri.org/" />
              </soap:Body>
            </soap:Envelope>
        """.trimIndent()

        return try {
            val response = api.getPerfil(cookieActual, soapPerfil)

            if (response.isSuccessful) {
                val responseBody = response.body()
                Log.d("PERFIL_SUCCESS", "XML del perfil obtenido correctamente")
                responseBody
            } else {
                val errorMsg = "Error ${response.code()}: ${response.errorBody()?.string()}"
                Log.e("PERFIL_ERROR", errorMsg)
                "Error en el servidor"
            }
        } catch (e: Exception) {
            Log.e("PERFIL_EXCEPTION", "Fallo de red: ${e.message}")
            "Error de conexión"
        }
    }
}