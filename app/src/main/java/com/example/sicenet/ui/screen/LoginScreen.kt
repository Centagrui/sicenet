package com.example.sicenet.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.sicenet.ui.SicenetViewModel

// Marcamos la función como @Composable para que Android sepa que debe dibujar esta UI
@Composable
fun LoginScreen(vm: SicenetViewModel, alEntrar: () -> Unit) {
    // Column organiza los elementos de arriba hacia abajo (verticalmente)
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp), // Ocupa toda la pantalla con margen
        horizontalAlignment = Alignment.CenterHorizontally, // Centra contenido horizontalmente
        verticalArrangement = Arrangement.Center // Centra contenido verticalmente
    ) {
        // Título principal con estilo predefinido de Material Design 3
        Text(text = "SICENET Alumnos", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(20.dp)) // Espacio en blanco de 20dp

        // Campo de texto para la matrícula conectado al ViewModel
        OutlinedTextField(
            value = vm.matricula, // El valor que se muestra viene del ViewModel
            onValueChange = { vm.matricula = it }, // Cuando el usuario escribe, actualiza el ViewModel
            label = { Text("Matrícula") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Campo de texto para la contraseña
        OutlinedTextField(
            value = vm.password,
            onValueChange = { vm.password = it },
            label = { Text("Contraseña") },
            // Transforma el texto en puntos para que no se vea la contraseña
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        // Si hay un error en el ViewModel, se muestra este texto en color rojo
        if (vm.mensajeError.isNotEmpty()) {
            Text(text = vm.mensajeError, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Botón de acción principal
        Button(
            onClick = { vm.iniciarSesion(alEntrar) }, // Llama a la lógica de login al hacer clic
            modifier = Modifier.fillMaxWidth(),
            // El botón se desactiva mientras está cargando para evitar múltiples clics
            enabled = !vm.estaCargando
        ) {
            // Si está cargando muestra un círculo de progreso, si no, muestra el texto
            if (vm.estaCargando) CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
            else Text("INICIAR SESIÓN")
        }
    }
}