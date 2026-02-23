package com.example.sicenet.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue // IMPORTANTE: Soluciona error de delegado 'by'
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sicenet.model.Materia
import com.example.sicenet.ui.SicenetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CargaScreen(vm: SicenetViewModel, onOpenMenu: () -> Unit) {
    // Se usa 'by' para observar el Flow de Room como un estado de Compose
    val materias by vm.materiasLocal.collectAsState(initial = emptyList())
    val context = LocalContext.current

    // Sincronización automática al entrar (Punto 2b de la rúbrica)
    LaunchedEffect(Unit) {
        vm.sincronizarCarga(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carga Académica") },
                navigationIcon = {
                    IconButton(onClick = onOpenMenu) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                    }
                }
            )
        }
    ) { padding ->
        if (materias.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator() // Feedback visual mientras llega la red
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).fillMaxSize()) {
                items(materias) { materia ->
                    // Aquí diseñas tu tarjeta de materia
                    ListItem(
                        headlineContent = { Text(materia.nombre) },
                        supportingContent = { Text("Grupo: ${materia.grupo}") }
                    )
                }
            }
        }
    }
}


@Composable
fun MateriaCard(materia: Materia) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = materia.nombre, // CAMBIO: Usamos .nombre (antes nombreMateria)
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Créditos: ${materia.creditos}", // CAMBIO: Usamos .creditos
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Prof: ${materia.profesor}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}