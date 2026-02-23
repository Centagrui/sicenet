package com.example.sicenet.ui.screen

import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sicenet.ui.SicenetViewModel



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
}@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnidadesScreen(vm: SicenetViewModel, onOpenMenu: () -> Unit) {
    val listaUnidades by vm.unidadesLocal.collectAsState(initial = emptyList())
    val context = LocalContext.current

    // Recuperar fecha de actualización de SharedPreferences
    val sharedPref = context.getSharedPreferences("sicenet_prefs", android.content.Context.MODE_PRIVATE)
    val ultimaSinc = sharedPref.getString("fecha_unidades", "Sin sincronizar") ?: "Sin sincronizar"

    // Sincronización automática al entrar (Punto 2b de la rúbrica)
    LaunchedEffect(Unit) {
        vm.sincronizarUnidades(context)
    }

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
        Column(modifier = Modifier.padding(padding)) {

            // ETIQUETA DE FECHA (Requisito de la rúbrica)
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

            if (listaUnidades.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Cargando unidades...")
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Cabecera de la tabla
                    item {
                        Row(
                            Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Unidad", fontWeight = FontWeight.Bold, modifier = Modifier.weight(2f))
                            Text("Calif.", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                            Text("Faltas", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                        }
                        HorizontalDivider(Modifier.padding(vertical = 8.dp))
                    }

                    items(listaUnidades) { unidad ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            // Usamos tu componente UnidadRow aquí
                            UnidadRow(
                                materia = unidad.materia,
                                unidad = "Unidad ${unidad.unidad}",
                                calif = unidad.calificacion,
                                faltas = "0" // O el campo faltas si lo agregas al modelo
                            )
                        }
                    }
                }
            }
        }
    }
}