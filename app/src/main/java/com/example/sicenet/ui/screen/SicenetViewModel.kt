package com.example.sicenet.ui

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sicenet.data.ISicenetRepository
import com.example.sicenet.data.SicenetRepository
import com.example.sicenet.model.AlumnoPerfil
import kotlinx.coroutines.launch

class SicenetViewModel(private val repository: ISicenetRepository) : ViewModel() {
    var matricula by mutableStateOf("")
    var password by mutableStateOf("")

    // Variables de estado para controlar qué mostrar en la pantalla
    var estaCargando by mutableStateOf(false)
    var mensajeError by mutableStateOf("")

    //  guardamos el resultado del servidorr
    var perfilXml by mutableStateOf<String?>(null)
    var alumnoData by mutableStateOf<AlumnoPerfil?>(null)


    fun cargarPerfil() {

        viewModelScope.launch {
            val xml = repository.recuperarPerfil()
            perfilXml = xml
            if (xml != null && !xml.contains("Error")) {
                alumnoData = repository.procesarDatosPerfil(xml)
            }
        }
    }


    fun iniciarSesion(onSuccess: () -> Unit) {
        viewModelScope.launch {
            estaCargando = true
            mensajeError = ""

            val exito = repository.login(matricula, password)

            if (exito) {
                //  Intentamos traer los datos del perfil
                val resultado = repository.recuperarPerfil()
                if (resultado != null && !resultado.contains("Error")) {
                    perfilXml = resultado
                    // Parseamos el XML a un objeto AlumnoPerfil
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