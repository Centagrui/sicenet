package com.example.sicenet.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sicenet.model.Kardex
import com.example.sicenet.ui.SicenetViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KardexScreen(vm: SicenetViewModel, onOpenMenu: () -> Unit) {
    // 1. Obtención de datos del flujo local (Room)
    val listaKardex by vm.kardexLocal.collectAsState(initial = emptyList())

    // 2. Lógica de cálculos (Separada de la UI)
    val promedio = if (listaKardex.isNotEmpty()) {
        val suma = listaKardex.sumOf { it.calificacion.toDoubleOrNull() ?: 0.0 }
        (suma / listaKardex.size).format(1)
    } else {
        "0.0"
    }

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
                // ETIQUETA DE ACTUALIZACIÓN (Requisito 2b)
                item {
                    Text(
                        text = "Última actualización: 22/02/2026 21:34",
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
                            // Divisor visual vertical
                            VerticalDivider(modifier = Modifier.height(40.dp), thickness = 1.dp)
                            ResumenItem("Créditos Totales", totalCreditos.toString())
                        }
                    }
                }

                // LISTADO DE MATERIAS
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