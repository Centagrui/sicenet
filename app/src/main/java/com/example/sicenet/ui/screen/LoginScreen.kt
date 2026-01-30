package com.example.sicenet.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.sicenet.ui.SicenetViewModel

@Composable
fun LoginScreen(vm: SicenetViewModel, alEntrar: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "SICENET Alumnos", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = vm.matricula,
            onValueChange = { vm.matricula = it },
            label = { Text("Matrícula") },
            modifier = Modifier.fillMaxWidth()
        )


        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = vm.password,
            onValueChange = { vm.password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        if (vm.mensajeError.isNotEmpty()) {
            Text(text = vm.mensajeError, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { vm.iniciarSesion(alEntrar) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !vm.estaCargando
        ) {
            if (vm.estaCargando) CircularProgressIndicator(
                modifier = Modifier.size(20.dp), // El tamaño se define dentro del Modifier
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp // Opcional: para que el círculo no se vea muy grueso
            )
            else Text("INICIAR SESIÓN")
        }
    }
}