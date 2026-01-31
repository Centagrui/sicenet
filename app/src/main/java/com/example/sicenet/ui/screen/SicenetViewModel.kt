package com.example.sicenet.ui

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sicenet.data.SicenetRepository
import com.example.sicenet.model.AlumnoPerfil // Importante: Importa tu modelo
import kotlinx.coroutines.launch

class SicenetViewModel(private val repository: SicenetRepository) : ViewModel() {

    // Datos de entrada del Login
    var matricula by mutableStateOf("")
    var password by mutableStateOf("")

    // Estados de la UI
    var estaCargando by mutableStateOf(false)
    var mensajeError by mutableStateOf("")

    // Datos del Alumno (Unificamos perfilXml a un solo tipo)
    var perfilXml by mutableStateOf<String?>(null)
    var alumnoData by mutableStateOf<AlumnoPerfil?>(null)

    /**
     * Función para procesar el XML y llenar el objeto alumnoData
     */
    fun cargarPerfil() {
        viewModelScope.launch {
            val xml = repository.recuperarPerfil()
            perfilXml = xml
            if (xml != null && !xml.contains("Error")) {
                // Usamos la función de procesamiento que creamos en el Repository
                alumnoData = repository.procesarDatosPerfil(xml)
            }
        }
    }

    /**
     * Función de Inicio de Sesión
     */
    fun iniciarSesion(onSuccess: () -> Unit) {
        viewModelScope.launch {
            estaCargando = true
            mensajeError = ""

            val exito = repository.login(matricula, password)

            if (exito) {
                // Si el login es exitoso, intentamos traer el perfil de una vez
                val resultado = repository.recuperarPerfil()
                if (resultado != null && !resultado.contains("Error")) {
                    perfilXml = resultado
                    // Procesamos los datos inmediatamente para tenerlos listos
                    alumnoData = repository.procesarDatosPerfil(resultado)
                    onSuccess()
                } else {
                    mensajeError = "Sesión iniciada, pero no se pudo obtener el perfil."
                }
            } else {
                mensajeError = "Error de autenticación. Verifica tus datos."
            }
            estaCargando = false
        }
    }
}