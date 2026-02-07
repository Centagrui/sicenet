package com.example.sicenet.ui

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sicenet.data.SicenetRepository
import com.example.sicenet.model.AlumnoPerfil
import kotlinx.coroutines.launch

class SicenetViewModel(private val repository: SicenetRepository) : ViewModel() {

    // 'by mutableStateOf' permite que Compose detecte cambios automáticamente.
    // Si estas variables cambian, la UI se redibuja sola (Reactividad).
    var matricula by mutableStateOf("")
    var password by mutableStateOf("")

    // Variables de estado para controlar qué mostrar en la pantalla (Loading o Errores)
    var estaCargando by mutableStateOf(false)
    var mensajeError by mutableStateOf("")

    //  guardamos el resultado del servidor: primero el texto crudo y luego el objeto limpio
    var perfilXml by mutableStateOf<String?>(null)
    var alumnoData by mutableStateOf<AlumnoPerfil?>(null)

    /**
     * para obtener el XML del perfil y convertirlo en un objeto AlumnoPerfil
     */
    fun cargarPerfil() {
        // viewModelScope.launch inicia una corrutina que se cancela si el ViewModel se destruye
        viewModelScope.launch {
            val xml = repository.recuperarPerfil()
            perfilXml = xml
            if (xml != null && !xml.contains("Error")) {
                // Mandamos el XML al repositorio para que lo limpie y nos devuelva el objeto
                alumnoData = repository.procesarDatosPerfil(xml)
            }
        }
    }

    /**
     * Función de Inicio de Sesión
     */
    fun iniciarSesion(onSuccess: () -> Unit) {
        viewModelScope.launch {
            estaCargando = true // Mostramos el circulito de carga
            mensajeError = ""   // Limpiamos errores previos

            // Llamamos a la función de login del repositorio
            val exito = repository.login(matricula, password)

            if (exito) {
                //  Intentamos traer los datos del perfil
                val resultado = repository.recuperarPerfil()
                if (resultado != null && !resultado.contains("Error")) {
                    perfilXml = resultado
                    // Parseamos el XML a un objeto AlumnoPerfil
                    alumnoData = repository.procesarDatosPerfil(resultado)
                    // Ejecutamos el callback para avisar a la UI que ya puede navegar
                    onSuccess()
                } else {
                    mensajeError = "Sesión iniciada, pero no se pudo obtener el perfil."
                }
            } else {
                // LOGIN FALLIDO: Mostramos mensaje al usuario
                mensajeError = "Error de autenticación. Verifica tus datos."
            }
            estaCargando = false // Quitamos el circulito de carga
        }
    }
}