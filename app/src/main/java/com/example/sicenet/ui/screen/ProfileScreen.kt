package com.example.sicenet.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sicenet.ui.SicenetViewModel

@Composable
fun ProfileScreen(vm: SicenetViewModel) {
    // Estado para controlar el scroll si hay muchos datos
    val scrollState = rememberScrollState()
    val alumno = vm.alumnoData

    // Si ya procesamos los datos en el Login, vm.alumnoData no será nulo.
    // Aun así, lanzamos cargarPerfil por si acaso la sesión se refresca.
    LaunchedEffect(Unit) {
        if (alumno == null) {
            vm.cargarPerfil()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Perfil Académico",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (alumno != null) {
            // Tarjeta de información principal
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    DatoItem(label = "Nombre", valor = alumno.nombre)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    DatoItem(label = "Matrícula", valor = alumno.matricula)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    DatoItem(label = "Estatus", valor = alumno.estatus)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
//                    DatoItem(label = "Inscrito", valor = alumno.inscrito)
//                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    DatoItem(label = "Carrera", valor = alumno.carrera)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    DatoItem(label = "Especialidad", valor = alumno.especialidad)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    DatoItem(label = "Semestre Actual", valor = alumno.semestreActual)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    DatoItem(label = "Créditos Totales", valor = alumno.creditosTotales)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))


                }
            }
        } else {
            // Pantalla de carga
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Recuperando datos de Sicenet...")
                }
            }
        }
    }
}

@Composable
fun DatoItem(label: String, valor: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = if (valor.isEmpty()) "No disponible" else valor,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}