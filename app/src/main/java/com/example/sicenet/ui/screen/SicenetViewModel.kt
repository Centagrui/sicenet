package com.example.sicenet.ui

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sicenet.data.SicenetRepository
import kotlinx.coroutines.launch

class SicenetViewModel(private val repository: SicenetRepository) : ViewModel() {
    var matricula by mutableStateOf("")
    var password by mutableStateOf("")
    var perfilXml by mutableStateOf("")
    var estaCargando by mutableStateOf(false)
    var mensajeError by mutableStateOf("")
    fun iniciarSesion(onSuccess: () -> Unit) {
        viewModelScope.launch {
            estaCargando = true
            mensajeError = ""
            // Intentamos la autenticación
            val exito = repository.login(matricula, password)


            if (exito) {
                // Si el login es correcto, recuperamos el perfil
                val resultado = repository.recuperarPerfil()
                if (resultado != null) {
                    perfilXml = resultado
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