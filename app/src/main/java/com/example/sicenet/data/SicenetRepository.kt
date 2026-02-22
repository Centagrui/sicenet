package com.example.sicenet.data

import android.util.Log
import com.example.sicenet.model.AlumnoPerfil

// Implementamos la interfaz para cumplir con la arquitecturMVVM
class SicenetRepository(private val api: SicenetApiService) : ISicenetRepository {

    private var sessionCookie: String? = null

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
                val rawCookie = response.headers()["Set-Cookie"]
                if (rawCookie != null) {
                    sessionCookie = rawCookie.split(";")[0]
                }

                if (body.contains("\"acceso\":true")) {
                    true
                } else {
                    Log.e("SICENET", "Credenciales incorrectas")
                    false
                }
            } else false
        } catch (e: Exception) {
            Log.e("SICENET", "Error: ${e.message}")
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
        } catch (e: Exception) {
            null
        }
    }

    override fun procesarDatosPerfil(xml: String): AlumnoPerfil? {
        return try {
            val jsonRaw = xml.substringAfter("<getAlumnoAcademicoWithLineamientoResult>")
                .substringBefore("</getAlumnoAcademicoWithLineamientoResult>")

            // Extracción robusta de datos
            val nombre = jsonRaw.substringAfter("\"nombre\":\"", "").substringBefore("\"")
            val matricula = jsonRaw.substringAfter("\"matricula\":\"", "").substringBefore("\"")
            val carrera = jsonRaw.substringAfter("\"carrera\":\"", "").substringBefore("\"")
            val especialidad = jsonRaw.substringAfter("\"especialidad\":\"", "").substringBefore("\"")

            // Ajuste para semestre y créditos (buscando las claves correctas)
            val semestre = jsonRaw.substringAfter("\"semActual\":")
                .substringBefore(",")
                .substringBefore("}")
                .replace("\"", "").trim()

            val creditos = jsonRaw.substringAfter("\"cdtosActuales\":")
                .substringBefore(",")
                .substringBefore("}")
                .replace("\"", "").trim()

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

    // Agrega estos métodos dentro de la clase SicenetRepository

    override suspend fun recuperarCargaAcademica(): String? {
        val soapCarga = """
        <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
          <soap:Body>
            <getCargaAcademicaByAlumno xmlns="http://tempuri.org/" />
          </soap:Body>
        </soap:Envelope>
    """.trimIndent()

        return try {
            val response = api.getCargaAcademica(sessionCookie ?: "", soapCarga)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun recuperarKardex(): String? {
        val soapKardex = """
        <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
          <soap:Body>
            <getAllKardexConPromedioByAlumno xmlns="http://tempuri.org/">
              <aluLineamiento>3</aluLineamiento> 
            </getAllKardexConPromedioByAlumno>
          </soap:Body>
        </soap:Envelope>
    """.trimIndent()

        return try {
            val response = api.getKardex(sessionCookie ?: "", soapKardex)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun recuperarCalificacionesUnidades(): String? {
        val soapCalif = """
            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <getCalifUnidadesByAlumno xmlns="http://tempuri.org/" />
              </soap:Body>
            </soap:Envelope>
        """.trimIndent()

        return try {
            val response = api.getCalificacionesUnidades(sessionCookie ?: "", soapCalif)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun recuperarCalificacionesFinales(): String? {
        val soapFinal = """
            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <getAllCalifFinalByAlumnos xmlns="http://tempuri.org/">
                  <bytModEducativo>1</bytModEducativo>
                </getAllCalifFinalByAlumnos>
              </soap:Body>
            </soap:Envelope>
        """.trimIndent()

        return try {
            val response = api.getCalificacionesFinales(sessionCookie ?: "", soapFinal)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            null
        }
    }

    override fun procesarCargaAcademica(xml: String): List<com.example.sicenet.model.Materia> {
        val listaMaterias = mutableListOf<com.example.sicenet.model.Materia>()
        try {
            val jsonRaw = xml.substringAfter("<getCargaAcademicaByAlumnoResult>")
                .substringBefore("</getCargaAcademicaByAlumnoResult>")

            // Separamos por cada objeto en el JSON
            val materiasRaw = jsonRaw.split("},{")

            materiasRaw.forEach { materiaStr ->
                val nombre = materiaStr.substringAfter("\"materia\":\"", "").substringBefore("\"")
                val clave = materiaStr.substringAfter("\"clave\":\"", "").substringBefore("\"")
                val docente = materiaStr.substringAfter("\"docente\":\"", "").substringBefore("\"")

                if (nombre.isNotEmpty()) {
                    listaMaterias.add(
                        com.example.sicenet.model.Materia(
                            clave = clave,
                            nombre = nombre,
                            profesor = docente,
                            lunes = "", martes = "", miercoles = "", jueves = "", viernes = "" // Ajustar según XML
                        )
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("PARSER_CARGA", "Error: ${e.message}")
        }
        return listaMaterias
    }
}