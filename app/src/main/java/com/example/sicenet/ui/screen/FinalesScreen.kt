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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sicenet.ui.SicenetViewModel

/**
 * Pantalla de Calificaciones Finales.
 * Muestra el promedio final de cada materia y la última fecha en que se actualizaron los datos.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinalesScreen(vm: SicenetViewModel, onOpenMenu: () -> Unit) {
    // Observamos los promedios finales almacenados en Room
    val listaFinales by vm.finalesLocal.collectAsState(initial = emptyList())
    val context = LocalContext.current

    /**
     * Sincronización automática: Al entrar a la pantalla, se dispara el Worker
     * para traer las calificaciones más recientes del servidor.
     */
    LaunchedEffect(Unit) {
        vm.sincronizarDato("FINALES")
    }

    /**
     * Recuperación de Metadatos:
     * Leemos de las preferencias compartidas la fecha guardada por el SaveWorker.
     */
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
            // Banner informativo con la fecha de la última actualización
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

            // Estado de la UI: Si no hay datos, muestra el Spinner de carga
            if (listaFinales.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                // Lista de tarjetas con las materias y sus promedios
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(listaFinales) { item ->
                        FinalItemCard(
                            materia = item.materia,
                            calificacion = item.calificacion
                        )
                    }
                }
            }
        }
    }
}

/**
 * Componente visual para una calificación final.
 * Aplica lógica de colores: Verde para aprobados y Rojo para reprobados.
 */
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
            // Nombre de la materia (ocupa el espacio disponible a la izquierda)
            Text(
                text = materia,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            /**
             * Lógica de Semáforo:
             * Intentamos convertir la nota a número. Si es mayor o igual a 70,
             * usamos el color primario (azul/verde), de lo contrario, rojo de error.
             */
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