package com.example.sicenet.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.sicenet.ui.SicenetViewModel

@Composable
fun LoginScreen(vm: SicenetViewModel, alEntrar: () -> Unit) {
    val context = LocalContext.current

    // Paleta de colores Café
    val cafeProfundo = Color(0xFF3E2723)
    val cafeMedio = Color(0xFF5D4037)
    val cremaFondo = Color(0xFFF5F5F5)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = cremaFondo // Fondo crema suave para toda la pantalla
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = "SICENET ALUMNO",
                style = MaterialTheme.typography.headlineMedium,
                color = cafeProfundo,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(30.dp))

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

            OutlinedTextField(
                value = vm.password,
                onValueChange = { vm.password = it },
                label = { Text("Contraseña") },
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

            if (vm.mensajeError.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = vm.mensajeError,
                    color = Color(0xFFB71C1C), // Rojo oscuro para combinar
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = { vm.iniciarSesion(context, alEntrar) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !vm.estaCargando,
                colors = ButtonDefaults.buttonColors(
                    containerColor = cafeProfundo,
                    contentColor = Color.White,
                    disabledContainerColor = cafeMedio.copy(alpha = 0.6f)
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                if (vm.estaCargando) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "INICIAR SESIÓN",
                        fontWeight = FontWeight.Bold,
                        letterSpacing = androidx.compose.ui.unit.TextUnit.Unspecified
                    )
                }
            }
        }
    }
}