package com.example.sicenet.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue // IMPORTANTE: Corrige el error de 'delegate'
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sicenet.ui.SicenetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KardexScreen(vm: SicenetViewModel, onOpenMenu: () -> Unit) {
    // Obtenemos la lista desde el Flow del ViewModel
    val listaKardex by vm.kardexLocal.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kárdex Académico") },
                navigationIcon = {
                    IconButton(onClick = onOpenMenu) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (listaKardex.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("No hay datos en el Kárdex aún.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(listaKardex) { item ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // REVISA: Que en tu modelo 'Kardex' el nombre sea exactamente 'materia' y 'calificacion'
                            Text(text = item.materia, style = MaterialTheme.typography.titleMedium)
                            Text(text = "Calificación: ${item.calificacion}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Créditos: ${item.creditos}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}