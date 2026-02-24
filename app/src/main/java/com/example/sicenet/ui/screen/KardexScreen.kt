package com.example.sicenet.ui.screen

import android.content.Context
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
import com.example.sicenet.model.Kardex
import com.example.sicenet.ui.SicenetViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KardexScreen(vm: SicenetViewModel, onOpenMenu: () -> Unit) {
    val context = LocalContext.current
    val listaKardex by vm.kardexLocal.collectAsState(initial = emptyList())

    // 1. SINCRONIZACIÓN: Solo se dispara UNA VEZ al entrar (usamos Unit)
    LaunchedEffect(Unit) {
        vm.sincronizarKardex(context)
    }

    // 2. FECHA: Esta SÍ debe reaccionar cuando cambie la lista
    // remember(listaKardex) hace que solo se recalcule el String, no que dispare el Worker

    
// Cambia 'historial' por 'listaKardex' para que reaccione al cambio de datos
    val ultimaSinc = remember(listaKardex) {
        context.getSharedPreferences("sicenet_prefs", Context.MODE_PRIVATE)
            .getString("fecha_kardex", "Sin sincronizar") ?: "Sin sincronizar"
    }
    // Cálculos de promedio y créditos
    val promedio = if (listaKardex.isNotEmpty()) {
        val suma = listaKardex.sumOf { it.calificacion.toDoubleOrNull() ?: 0.0 }
        (suma / listaKardex.size).format(1)
    } else { "0.0" }

    val totalCreditos = listaKardex.sumOf { materia ->
        val calif = materia.calificacion.toIntOrNull() ?: 0
        if (calif >= 70) (materia.creditos.toIntOrNull() ?: 0) else 0
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Kárdex") },
                navigationIcon = {
                    IconButton(onClick = onOpenMenu) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                    }
                }
            )
        }
    ) { padding ->
        if (listaKardex.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(padding).fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // ETIQUETA DE ACTUALIZACIÓN DINÁMICA
                item {
                    Text(
                        text = "Última actualización: $ultimaSinc",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }

                // TARJETA DE RESUMEN
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ResumenItem("Promedio Gral", promedio)
                            VerticalDivider(modifier = Modifier.height(40.dp), thickness = 1.dp)
                            ResumenItem("Créditos Totales", totalCreditos.toString())
                        }
                    }
                }

                items(listaKardex) { materia ->
                    KardexItemCard(materia)
                }
            }
        }
    }
}

@Composable
fun KardexItemCard(kardex: Kardex) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = kardex.materia,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Créditos: ${kardex.creditos}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            val nota = kardex.calificacion.toIntOrNull() ?: 0
            val colorFondo = if (nota >= 70) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error

            Surface(
                color = colorFondo,
                shape = MaterialTheme.shapes.small
            ) {
                Text(
                    text = kardex.calificacion,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
fun ResumenItem(label: String, valor: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

fun Double.format(digits: Int) = "%.${digits}f".format(Locale.US, this)