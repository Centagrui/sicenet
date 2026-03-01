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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sicenet.ui.SicenetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinalesScreen(vm: SicenetViewModel, onOpenMenu: () -> Unit) {
    val listaFinales by vm.finalesLocal.collectAsState(initial = emptyList())
    val context = LocalContext.current

    // Sincronización automática al entrar
    LaunchedEffect(Unit) {
        vm.sincronizarFinales(context)
    }

    // LÓGICA IGUAL A UNIDADES: Recuperar fecha directamente
    val sharedPref = context.getSharedPreferences("sicenet_prefs", android.content.Context.MODE_PRIVATE)
    val ultimaSinc = sharedPref.getString("fecha_finales", "Sin sincronizar") ?: "Sin sincronizar"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calificaciones Finales") },
                navigationIcon = {
                    IconButton(onClick = onOpenMenu) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Etiqueta de fecha (Misma lógica que Unidades)
            Surface(
                color = MaterialTheme.colorScheme.tertiaryContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Última sincronización: $ultimaSinc",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(8.dp),
                    textAlign = TextAlign.Center
                )
            }

            if (listaFinales.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(listaFinales) { materia ->
                        KardexItemCard(materia)
                    }
                }
            }
        }
    }
}

@Composable
fun FinalItemCard(materia: String, calificacion: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = materia,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            // Lógica de colores: Verde para aprobado (>=70), Rojo para reprobado
            val nota = calificacion.toIntOrNull() ?: 0
            val colorTexto = if (nota >= 70)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.error

            Text(
                text = calificacion,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = colorTexto
            )
        }
    }
}