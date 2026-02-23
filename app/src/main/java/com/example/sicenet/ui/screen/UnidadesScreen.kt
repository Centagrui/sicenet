package com.example.sicenet.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sicenet.ui.SicenetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnidadesScreen(vm: SicenetViewModel, onOpenMenu: () -> Unit) {
    // Nota: Aquí deberías observar un flujo de calificaciones parciales desde tu VM
    // val listaUnidades by vm.unidadesLocal.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calificaciones por Unidad") },
                navigationIcon = {
                    IconButton(onClick = onOpenMenu) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            // ETIQUETA REQUISITO 2: Fecha de actualización
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Sincronizado: 22/02/2026 22:00",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(8.dp),
                    textAlign = TextAlign.Center
                )
            }

            // CABECERA DE TABLA
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Materia / Unidad", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold)
                Text("Calif.", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
                Text("Faltas", modifier = Modifier.weight(1f), textAlign = TextAlign.End, fontWeight = FontWeight.Bold)
            }

            // Aquí iría tu LazyColumn con los datos de Room
            // Por ahora, una simulación para que veas el diseño:
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    UnidadRow("CALCULO VECTORIAL", "U1", "90", "0")
                    UnidadRow("", "U2", "85", "1")
                    HorizontalDivider()
                    UnidadRow("ESTRUCTURA DE DATOS", "U1", "100", "0")
                }
            }
        }
    }
}

@Composable
fun UnidadRow(materia: String, unidad: String, calif: String, faltas: String) {
    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
        if (materia.isNotEmpty()) {
            Text(materia, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(unidad, modifier = Modifier.weight(2f))
            Text(calif, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            Text(faltas, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
        }
    }
}