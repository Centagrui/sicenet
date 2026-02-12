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

    val scrollState = rememberScrollState()
    // Obtenemos los datos del alumno desde el ViewModel
    val alumno = vm.alumnoData

    // LaunchedEffect se ejecuta una sola vez cuando la pantalla se carga (Unit)
    LaunchedEffect(Unit) {
        // Si  el objeto alumno está vacío, mandamos llamar la petición al servidor
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

        // Si ya tenemos datos del alumno, dibujamos la tarjeta (Card)
        if (alumno != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    //'DatoItem' para no repetir código de diseño
                    DatoItem(label = "Nombre", valor = alumno.nombre)
                    Divider(modifier = Modifier.padding(vertical = 8.dp)) // Línea divisoria

                    DatoItem(label = "Matrícula", valor = alumno.matricula)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    DatoItem(label = "Estatus", valor = alumno.estatus)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    DatoItem(label = "Carrera", valor = alumno.carrera)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    DatoItem(label = "Especialidad", valor = alumno.especialidad)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    DatoItem(label = "Semestre Actual", valor = alumno.semestreActual)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    DatoItem(label = "Créditos Totales", valor = alumno.creditosTotales)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        } else {
            // Si el objeto 'alumno' es nulo, mostramos un indicador de carga (Loading)
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