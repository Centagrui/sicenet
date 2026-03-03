package com.example.sicenet.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.sicenet.ui.SicenetViewModel

/**
 * Pantalla de inicio de sesión.
 * Gestiona la entrada de credenciales y reacciona a los estados del ViewModel.
 * * @param vm ViewModel que contiene la lógica de negocio y estados de login.
 * @param alEntrar Callback que se ejecuta cuando el login es exitoso para navegar a la siguiente pantalla.
 */
@Composable
fun LoginScreen(vm: SicenetViewModel, alEntrar: () -> Unit) {
    val context = LocalContext.current

    // Paleta de colores consistente con el estilo "Café/Ocre" de la App
    val cafeProfundo = Color(0xFF3E2723)
    val cafeMedio = Color(0xFF5D4037)
    val cremaFondo = Color(0xFFF5F5F5)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = cremaFondo // Fondo crema suave para una estética profesional
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // Título de la aplicación
            Text(
                text = "SICENET ALUMNO",
                style = MaterialTheme.typography.headlineMedium,
                color = cafeProfundo,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Campo de entrada para la Matrícula
            OutlinedTextField(
                value = vm.matricula,
                onValueChange = { vm.matricula = it },
                label = { Text("Matrícula") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = cafeProfundo,
                    unfocusedBorderColor = cafeMedio,
                    focusedLabelColor = cafeProfundo,
                    cursorColor = cafeProfundo
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(15.dp))

            // Campo de entrada para la Contraseña
            OutlinedTextField(
                value = vm.password,
                onValueChange = { vm.password = it },
                label = { Text("Contraseña") },
                // Oculta los caracteres de la contraseña
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = cafeProfundo,
                    unfocusedBorderColor = cafeMedio,
                    focusedLabelColor = cafeProfundo,
                    cursorColor = cafeProfundo
                ),
                singleLine = true
            )

            // Sección de Mensaje de Error (solo se muestra si existe un error)
            if (vm.mensajeError.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = vm.mensajeError,
                    color = Color(0xFFB71C1C), // Rojo oscuro para alerta
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Botón de Inicio de Sesión
            Button(
                onClick = { vm.iniciarSesion(context, alEntrar) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                // Se deshabilita mientras la petición está en curso para evitar múltiples clicks
                enabled = !vm.estaCargando,
                colors = ButtonDefaults.buttonColors(
                    containerColor = cafeProfundo,
                    contentColor = Color.White,
                    disabledContainerColor = cafeMedio.copy(alpha = 0.6f)
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                // Si está cargando, muestra un Spinner circular
                if (vm.estaCargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "INICIAR SESIÓN",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}