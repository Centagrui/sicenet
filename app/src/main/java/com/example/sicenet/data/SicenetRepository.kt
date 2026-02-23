package com.example.sicenet.data

import android.util.Log
import com.example.sicenet.model.AlumnoPerfil
import com.example.sicenet.model.Materia
import com.example.sicenet.model.Kardex

class SicenetRepository(private val api: SicenetApiService) : ISicenetRepository {

    companion object { // Esto hace que la cookie sea compartida por TODOS los repositorios
        var sessionCookie: String? = null
    }

    override suspend fun login(matricula: String, contrasenia: String): Boolean {
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
            val response = api.accesoLogin("ASP.NET_SessionId=detect", soapLogin)
            if (response.isSuccessful) {
                val body = response.body() ?: ""

                // Guardar Cookie de sesión
                val rawCookie = response.headers()["Set-Cookie"]
                if (rawCookie != null) {
                    sessionCookie = rawCookie.split(";")[0]
                    Log.d("SICENET", "Cookie obtenida: $sessionCookie")
                }

                // VALIDACIÓN FLEXIBLE: Acepta con o sin espacios en el JSON del servidor
                val loginExitoso = body.contains("\"acceso\":true", ignoreCase = true) ||
                        body.contains("\"acceso\": true", ignoreCase = true)

                if (loginExitoso) {
                    Log.d("SICENET", "Login exitoso confirmado")
                    true
                } else {
                    Log.e("SICENET", "Credenciales incorrectas. Servidor respondió: $body")
                    false
                }
            } else {
                Log.e("SICENET", "Error de servidor: ${response.code()}")
                false
            }
        } catch (e: Exception) {
            Log.e("SICENET", "ERROR CRÍTICO (Posible falta de Internet o Manifest): ${e.message}")
            false
        }
    }

    override suspend fun recuperarPerfil(): String? {
        val cookieActual = sessionCookie ?: return "Error: Sin Sesión"
        val soapPerfil = """
            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <getAlumnoAcademicoWithLineamiento xmlns="http://tempuri.org/" />
              </soap:Body>
            </soap:Envelope>
        """.trimIndent()

        return try {
            val response = api.getPerfil(cookieActual, soapPerfil)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) { null }
    }

    override fun procesarDatosPerfil(xml: String): AlumnoPerfil? {
        return try {
            val jsonRaw = xml.substringAfter("<getAlumnoAcademicoWithLineamientoResult>")
                .substringBefore("</getAlumnoAcademicoWithLineamientoResult>")

            val nombre = jsonRaw.substringAfter("\"nombre\":\"", "").substringBefore("\"")
            val matricula = jsonRaw.substringAfter("\"matricula\":\"", "").substringBefore("\"")
            val carrera = jsonRaw.substringAfter("\"carrera\":\"", "").substringBefore("\"")
            val especialidad = jsonRaw.substringAfter("\"especialidad\":\"", "").substringBefore("\"")

            val semestre = jsonRaw.substringAfter("\"semActual\":").substringBefore(",").replace("\"", "").trim()
            val creditos = jsonRaw.substringAfter("\"cdtosActuales\":").substringBefore(",").replace("\"", "").trim()

            AlumnoPerfil(
                nombre = nombre,
                matricula = matricula,
                carrera = carrera,
                especialidad = especialidad,
                semestreActual = semestre,
                creditosTotales = creditos,
                fechaActualizacion = System.currentTimeMillis()
            )
        } catch (e: Exception) {
            Log.e("PARSER_ERROR", "Error: ${e.message}")
            null
        }
    }

    override suspend fun recuperarCargaAcademica(): String? {
        val soapCarga = """
        <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
          <soap:Body><getCargaAcademicaByAlumno xmlns="http://tempuri.org/" /></soap:Body>
        </soap:Envelope>""".trimIndent()

        return try {
            val response = api.getCargaAcademica(sessionCookie ?: "", soapCarga)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) { null }
    }

    override suspend fun recuperarKardex(): String? {
        val soapKardex = """
        <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
          <soap:Body>
            <getAllKardexConPromedioByAlumno xmlns="http://tempuri.org/">
              <aluLineamiento>3</aluLineamiento> 
            </getAllKardexConPromedioByAlumno>
          </soap:Body>
        </soap:Envelope>""".trimIndent()

        return try {
            val response = api.getKardex(sessionCookie ?: "", soapKardex)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) { null }
    }

    override suspend fun recuperarCalificacionesUnidades(): String? {
        val soapCalif = """
            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body><getCalifUnidadesByAlumno xmlns="http://tempuri.org/" /></soap:Body>
            </soap:Envelope>""".trimIndent()
        return try {
            val response = api.getCalificacionesUnidades(sessionCookie ?: "", soapCalif)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) { null }
    }

    override suspend fun recuperarCalificacionesFinales(): String? {
        val soapFinal = """
            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <getAllCalifFinalByAlumnos xmlns="http://tempuri.org/">
                  <bytModEducativo>1</bytModEducativo>
                </getAllCalifFinalByAlumnos>
              </soap:Body>
            </soap:Envelope>""".trimIndent()
        return try {
            val response = api.getCalificacionesFinales(sessionCookie ?: "", soapFinal)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) { null }
    }

    override fun procesarCargaAcademica(xml: String): List<Materia> {
        val lista = mutableListOf<Materia>()
        try {
            // Extraemos el bloque de texto que contiene el JSON
            val contenidoJson = xml.substringAfter("<getCargaAcademicaByAlumnoResult>")
                .substringBefore("</getCargaAcademicaByAlumnoResult>")

            // Definimos el patrón de búsqueda:
            // Buscamos "Campo": "Valor", ignorando lo que haya en medio con .*?
            val patron = """
            "Docente"\s*:\s*"(.*?)".*?
            "Materia"\s*:\s*"(.*?)".*?
            "Grupo"\s*:\s*"(.*?)"
        """.trimIndent().replace("\n", "").toRegex(RegexOption.DOT_MATCHES_ALL)

            val coincidencias = patron.findAll(contenidoJson)

            coincidencias.forEach { match ->
                val docente = match.groupValues[1]
                val materia = match.groupValues[2]
                val grupo   = match.groupValues[3]

                if (materia.isNotBlank()) {
                    lista.add(
                        Materia(
                            clave = grupo,      // Usamos el grupo como clave o ID temporal
                            nombre = materia,
                            profesor = docente,
                            // El resto de campos los mandamos vacíos como pediste
                            lunes = "", martes = "", miercoles = "", jueves = "", viernes = ""
                        )
                    )
                }
            }
            Log.d("DEBUG_SAVE", "Materias detectadas: ${lista.size}")
        } catch (e: Exception) {
            Log.e("PARSER_ERROR", "Error: ${e.message}")
        }
        return lista
    }
    override fun procesarKardex(xml: String): List<Kardex> {
        val lista = mutableListOf<Kardex>()
        try {
            val contenidoJson = xml.substringAfter("<getAllKardexConPromedioByAlumnoResult>")
                .substringBefore("</getAllKardexConPromedioByAlumnoResult>")

            // Este patrón busca "Materia":"VALOR", "Cdts":VALOR, "Calif":VALOR
            val patron = """\"Materia\"\s*:\s*\"(.*?)\".*?\"Cdts\"\s*:\s*(\d+).*?\"Calif\"\s*:\s*(\d+)""".toRegex()
            val coincidencias = patron.findAll(contenidoJson)

            coincidencias.forEach { match ->
                lista.add(Kardex(
                    materia = match.groupValues[1],
                    creditos = match.groupValues[2],
                    calificacion = match.groupValues[3],
                    periodo = ""
                ))
            }
            android.util.Log.d("DEBUG_SAVE_KARDEX", "¡ÉXITO! Se procesaron ${lista.size} materias.")
        } catch (e: Exception) {
            android.util.Log.e("PARSER_ERROR", "Error: ${e.message}")
        }
        return lista
    }
}