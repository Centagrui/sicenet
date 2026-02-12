package com.example.sicenet.data

import android.util.Log
import com.example.sicenet.model.AlumnoPerfil

class SicenetRepository(private val api: SicenetApiService) {

    // Variable para almacenar el ID de sesión (Cookie) y mantener al usuario conectado
    private var sessionCookie: String? = null

    suspend fun login(matricula: String, contrasenia: String): Boolean {
        // Estructura XML
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
            // Se envía una cookie  para que el servidor genere una sesión real
            val response = api.accesoLogin("ASP.NET_SessionId=detect", soapLogin)

            if (response.isSuccessful) {
                val body = response.body() ?: ""
                Log.d("SICENET_LOGIN", "Body: $body")
                 // Obtenemos la cookie del header para las peticiones del perfil
                val rawCookie = response.headers()["Set-Cookie"]
                if (rawCookie != null) {
                    //primera
                    sessionCookie = rawCookie.split(";")[0]
                    Log.d("COOKIE_DEBUG", "Sesión capturada: $sessionCookie")
                }

                // servidor devuelve un String que contiene un JSON
                // Si d viene true, el login es válido
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
        } catch (e: Exception) {
            Log.e("SICENET", "Error en login: ${e.message}")
            false
        }
    }

    suspend fun recuperarPerfil(): String? {
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
            // Mandamos la petición usando la cookie que guardamos en el login
            val response = api.getPerfil(cookieActual, soapPerfil)

            if (response.isSuccessful) {
                val responseBody = response.body()
                Log.d("PERFIL_SUCCESS", "XML del perfil obtenido correctamente")
                responseBody // Devolvemos el XML completo como String
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

    // limpiamos el XML  y extrae los datos reales
    fun procesarDatosPerfil(xml: String): AlumnoPerfil? {
        return try {
            // Quitamos las etiquetas XML para quedarnos con el texto interior
            val jsonRaw = xml.substringAfter("<getAlumnoAcademicoWithLineamientoResult>")
                .substringBefore("</getAlumnoAcademicoWithLineamientoResult>")

            // para recortar el texto entre comillas
            val nombre = jsonRaw.substringAfter("\"nombre\":\"", "").substringBefore("\"")
            val matricula = jsonRaw.substringAfter("\"matricula\":\"", "").substringBefore("\"")
            val estatus = jsonRaw.substringAfter("\"estatus\":\"", "").substringBefore("\"")
            val inscrito = jsonRaw.substringAfter("\"inscrito\":\"", "").substringBefore("\"")
            val carrera = jsonRaw.substringAfter("\"carrera\":\"", "").substringBefore("\"")
            val especialidad = jsonRaw.substringAfter("\"especialidad\":\"", "").substringBefore("\"")

            // Limpieza  para números
            val semestre = jsonRaw.substringAfter("\"semActual\":")
                .substringBefore(",")
                .substringBefore("}")
                .replace("\"", "")
                .trim()

            val creditos = jsonRaw.substringAfter("\"cdtosActuales\":")
                .substringBefore(",")
                .substringBefore("}")
                .replace("\"", "")
                .trim()

            Log.d("PARSER_CHECK", "Semestre extraído: $semestre, Créditos: $creditos")

            // Devolvemos el objeto AlumnoPerfil ya con los datos limpios y listos para la UI
            AlumnoPerfil(
                nombre = nombre,
                matricula = matricula,
                estatus = estatus,
                inscrito = inscrito,
                carrera = carrera,
                semestreActual = semestre,
                especialidad = especialidad,
                creditosTotales = creditos
            )
        } catch (e: Exception) {
            Log.e("PARSER_ERROR", "Error procesando JSON: ${e.message}")
            null
        }
    }
}