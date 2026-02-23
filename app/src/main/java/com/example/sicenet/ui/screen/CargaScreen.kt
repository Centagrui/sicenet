package com.example.sicenet.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue // IMPORTANTE: Soluciona error de delegado 'by'
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sicenet.model.Materia
import com.example.sicenet.ui.SicenetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun CargaScreen(vm: SicenetViewModel, onOpenMenu: () -> Unit) {
    // 1. Obtener datos de Room
    val listaCarga: List<Materia> by vm.materiasCarga.collectAsState(initial = emptyList())

    // 2. OBTENER FECHA DE SHARED PREFERENCES (Agregado aquí)
    val context = androidx.compose.ui.platform.LocalContext.current
    val sharedPref = context.getSharedPreferences("sicenet_prefs", android.content.Context.MODE_PRIVATE)
    val ultimaSinc = sharedPref.getString("fecha_carga", "Sin sincronizar") ?: "Sin sincronizar"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Carga Académica") },
                navigationIcon = {
                    IconButton(onClick = onOpenMenu) {
                        Icon(imageVector = Icons.Default.Menu, contentDescription = "Menú")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {

            // ETIQUETA DINÁMICA (Punto 2b de tu rúbrica)
            Surface(
                color = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Última actualización: $ultimaSinc", // <--- VALOR REAL
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(8.dp),
                    textAlign = TextAlign.Center
                )
            }

            if (listaCarga.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No hay materias cargadas.")
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(listaCarga) { materia ->
                        MateriaCard(materia)
                    }
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