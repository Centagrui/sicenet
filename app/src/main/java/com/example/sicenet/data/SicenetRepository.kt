package com.example.sicenet.data

import android.util.Log
import com.example.sicenet.model.AlumnoPerfil
import com.example.sicenet.model.CalificacionFinal
import com.example.sicenet.model.Materia
import com.example.sicenet.model.Kardex
import com.example.sicenet.model.UnidadCalificacion

/**
 * Implementación del Repositorio de Sicenet.
 * Se encarga de la comunicación remota (SOAP) y la transformación de datos (Parsing).
 */
class SicenetRepository(private val api: SicenetApiService) : ISicenetRepository {

    companion object {
        // Variable estática para almacenar la Cookie de sesión y compartirla entre hilos/workers.
        var sessionCookie: String? = null
    }

    /**
     * Realiza la petición de inicio de sesión al servidor.
     */
    override suspend fun login(matricula: String, contrasenia: String): Boolean {
        // Construcción manual del sobre SOAP para el Login
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
            // "ASP.NET_SessionId=detect" le indica al servidor que inicie una sesión nueva
            val response = api.accesoLogin("ASP.NET_SessionId=detect", soapLogin)
            if (response.isSuccessful) {
                val body = response.body() ?: ""

                // Extraemos la Cookie 'Set-Cookie' del encabezado para futuras peticiones
                val rawCookie = response.headers()["Set-Cookie"]
                if (rawCookie != null) {
                    sessionCookie = rawCookie.split(";")[0]
                    Log.d("SICENET", "Cookie obtenida: $sessionCookie")
                }

                // Verificamos si el JSON devuelto dentro del XML contiene el éxito del acceso
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

    /**
     * Descarga el XML crudo con la información del perfil del alumno.
     */
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

    /**
     * Procesa el XML de perfil usando substrings para extraer campos específicos.
     */
    override fun procesarDatosPerfil(xml: String): AlumnoPerfil? {
        return try {
            // Extraemos solo la parte del JSON que está dentro de las etiquetas XML del resultado
            val jsonRaw = xml.substringAfter("<getAlumnoAcademicoWithLineamientoResult>")
                .substringBefore("</getAlumnoAcademicoWithLineamientoResult>")

            val nombre = jsonRaw.substringAfter("\"nombre\":\"", "").substringBefore("\"")
            val matricula = jsonRaw.substringAfter("\"matricula\":\"", "").substringBefore("\"")
            val carrera = jsonRaw.substringAfter("\"carrera\":\"", "").substringBefore("\"")
            val especialidad = jsonRaw.substringAfter("\"especialidad\":\"", "").substringBefore("\"")

            // Limpiamos comillas y espacios de los valores numéricos
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

    /**
     * Descarga el XML de la carga académica actual.
     */
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

    /**
     * Descarga el XML del Kárdex (Historial Académico).
     */
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

    /**
     * Descarga el XML de calificaciones por unidad (parciales).
     */
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

    /**
     * Descarga el XML de calificaciones finales.
     */
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

    /**
     * Procesa el XML de la carga usando una expresión regular (Regex) para identificar bloques de materias.
     */
    override fun procesarCargaAcademica(xml: String): List<Materia> {
        val lista = mutableListOf<Materia>()
        try {
            val contenidoJson = xml.substringAfter("<getCargaAcademicaByAlumnoResult>")
                .substringBefore("</getCargaAcademicaByAlumnoResult>")

            // El patrón busca pares de "Docente", "Materia" y "Grupo"
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
                            clave = materia,
                            nombre = materia,
                            profesor = docente,
                            grupo = grupo,
                            creditos = "",
                            lunes = "", martes = "", miercoles = "", jueves = "", viernes = ""
                        )
                    )
                }
            }
            Log.d("DEBUG_SAVE", "Materias detectadas: ${lista.size}")
        } catch (e: Exception) {
            Log.e("PARSER_ERROR", "Error procesando carga: ${e.message}")
        }
        return lista
    }

    /**
     * Procesa el XML del Kárdex buscando materia, créditos (cdts) y calificación (calif).
     */
    override fun procesarKardex(xml: String): List<Kardex> {
        val lista = mutableListOf<Kardex>()
        try {
            val contenidoJson = xml.substringAfter("<getAllKardexConPromedioByAlumnoResult>")
                .substringBefore("</getAllKardexConPromedioByAlumnoResult>")

            // Regex diseñada para capturar campos opcionalmente rodeados por comillas
            val patron = """\"materia\"\s*:\s*\"(.*?)\".*?\"cdts\"\s*:\s*\"?(\d+)\"?.*?\"calif\"\s*:\s*\"?(\d+)\"?""".toRegex(RegexOption.IGNORE_CASE)
            val coincidencias = patron.findAll(contenidoJson)

            coincidencias.forEach { match ->
                lista.add(Kardex(
                    materia = match.groupValues[1].trim(),
                    creditos = match.groupValues[2].trim(),
                    calificacion = match.groupValues[3].trim(),
                    periodo = ""
                ))
            }
            Log.d("DEBUG_SAVE_KARDEX", "¡ÉXITO! Se procesaron ${lista.size} materias en Kárdex.")
        } catch (e: Exception) {
            Log.e("PARSER_ERROR", "Error en Kardex: ${e.message}")
        }
        return lista
    }

    /**
     * Procesa el XML de unidades. Itera del C1 al C13 por cada materia encontrada.
     */
    override fun procesarUnidades(xml: String): List<UnidadCalificacion> {
        val lista = mutableListOf<UnidadCalificacion>()
        try {
            val contenidoJson = xml.substringAfter("<getCalifUnidadesByAlumnoResult>")
                .substringBefore("</getCalifUnidadesByAlumnoResult>")

            // 1. Buscamos cada bloque de materia entre llaves { ... }
            val bloqueMateriaRegex = """\{(.*?)\}""".toRegex(RegexOption.DOT_MATCHES_ALL)
            val bloques = bloqueMateriaRegex.findAll(contenidoJson)

            bloques.forEach { bloque ->
                val textoBloque = bloque.value

                // 2. Extraer el nombre de la materia en el bloque actual
                val nombreMateria = """\"Materia\"\s*:\s*\"(.*?)\"""".toRegex()
                    .find(textoBloque)?.groupValues?.get(1) ?: "Materia Desconocida"

                // 3. Extraer C1, C2, C3... hasta C13 si existen
                for (i in 1..13) {
                    val califRegex = """\"C$i\"\s*:\s*\"(.*?)\"""".toRegex()
                    val coincidencia = califRegex.find(textoBloque)

                    val valor = coincidencia?.groupValues?.get(1)

                    // Solo agregamos si el valor no es nulo o literal "null"
                    if (valor != null && valor != "null") {
                        lista.add(UnidadCalificacion(
                            materia = nombreMateria,
                            unidad = i.toString(),
                            calificacion = valor
                        ))
                    }
                }
            }
            Log.d("DEBUG_UNIDADES", "Total de registros procesados: ${lista.size}")
        } catch (e: Exception) {
            Log.e("PARSER_ERROR", "Error procesando unidades: ${e.message}")
        }
        return lista
    }

    /**
     * Procesa el XML de promedios finales para la pantalla de Finales.
     */
    override fun procesarCalificacionesFinales(xml: String): List<CalificacionFinal> {
        val lista = mutableListOf<CalificacionFinal>()
        try {
            val contenidoJson = xml.substringAfter("<getAllCalifFinalByAlumnosResult>")
                .substringBefore("</getAllCalifFinalByAlumnosResult>")

            // Buscamos el nombre de la materia y su calificación final
            val patron = """\"materia\"\s*:\s*\"(.*?)\".*?\"calif\"\s*:\s*\"?(.*?)\"""".toRegex(RegexOption.IGNORE_CASE)
            val coincidencias = patron.findAll(contenidoJson)

            coincidencias.forEach { match ->
                val nombreMateria = match.groupValues[1].trim()
                var calif = match.groupValues[2].trim()

                // Si la calificación está vacía (materia en curso), ponemos S/N (Sin Nota)
                if (calif.isEmpty() || calif == "null") calif = "S/N"

                lista.add(CalificacionFinal(
                    materia = nombreMateria,
                    calificacion = calif
                ))
            }
        } catch (e: Exception) {
            Log.e("PARSER_ERROR", "Error en finales: ${e.message}")
        }
        return lista
    }
}