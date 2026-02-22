package com.example.sicenet.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sicenet.model.Materia
import com.example.sicenet.ui.SicenetViewModel

@Composable
fun CargaScreen(vm: SicenetViewModel) {
    // Escuchamos la base de datos en tiempo real usando Flow
    val materias by vm.materiasCarga.collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Mi Carga Académica",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (materias.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("No hay materias guardadas o sincronizando...")
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(materias) { materia ->
                    MateriaCard(materia)
                }
            }
        }
    }
}

@Composable
fun MateriaCard(materia: Materia) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = materia.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = "Profesor: ${materia.profesor}", style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))

            // Horarios simplificados
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                HorarioItem("Lun", materia.lunes)
                HorarioItem("Mar", materia.martes)
                HorarioItem("Mie", materia.miercoles)
                HorarioItem("Jue", materia.jueves)
                HorarioItem("Vie", materia.viernes)
            }
        }
    }
}

@Composable
fun HorarioItem(dia: String, hora: String) {
    Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
        Text(text = dia, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        Text(text = if (hora.isEmpty()) "-" else hora, style = MaterialTheme.typography.labelSmall)
    }
}