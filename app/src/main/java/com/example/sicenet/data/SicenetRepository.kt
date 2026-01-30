package com.example.sicenet.data

class SicenetRepository(private val api: SicenetApiService) {
    private var sessionCookie: String? = null

    suspend fun login(matricula: String, contrasenia: String): Boolean {
        // Usamos triple comilla para el XML.
        // Si la contraseña tiene '$', Kotlin lo trata normal dentro de las llaves ${contrasenia}
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

        val response = api.accesoLogin(soapLogin)
        if (response.isSuccessful) {
            // Recuperamos la cookie de la cabecera "Set-Cookie"
            val rawCookie = response.headers()["Set-Cookie"]
            if (rawCookie != null) {
                sessionCookie = rawCookie.split(";")[0]
                return true
            }
        }
        return false
    }

    suspend fun recuperarPerfil(): String? {
        val cookie = sessionCookie ?: return "No hay sesión activa"
        val soapPerfil = """
            <?xml version="1.0" encoding="utf-8"?>
            <soap:Envelope xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:xsd="http://www.w3.org/2001/XMLSchema" xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
              <soap:Body>
                <getAlumnoAcademicoWithLineamiento xmlns="http://tempuri.org/" />
              </soap:Body>
            </soap:Envelope>
        """.trimIndent()

        val response = api.getPerfil(cookie, soapPerfil)
        return if (response.isSuccessful) response.body() else "Error en la consulta"
    }
}