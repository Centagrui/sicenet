package com.example.sicenet.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sicenet.ui.SicenetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnidadesScreen(vm: SicenetViewModel, onOpenMenu: () -> Unit) {
    val listaUnidades by vm.unidadesLocal.collectAsState(initial = emptyList())
    val context = LocalContext.current

    // Paleta de colores Café
    val cafeProfundo = Color(0xFF3E2723)
    val cafeMedio = Color(0xFF5D4037)
    val cremaFondo = Color(0xFFF5F5F5)

    val sharedPref = context.getSharedPreferences("sicenet_prefs", android.content.Context.MODE_PRIVATE)
    val ultimaSinc = sharedPref.getString("fecha_unidades", "Sin sincronizar") ?: "Sin sincronizar"

    LaunchedEffect(Unit) {
        vm.sincronizarUnidades(context)
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calificaciones por Unidad", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onOpenMenu) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Etiqueta de sincronización
            Surface(color = cafeProfundo, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Última sincronización: $ultimaSinc",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    modifier = Modifier.padding(8.dp),
                    textAlign = TextAlign.Center
                )
            }

            if (listaUnidades.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = cafeProfundo)
                }
            } else {
                // Agrupamos las unidades por nombre de materia para armar las tablas
                val materiasUnicas = listaUnidades.map { it.materia }.distinct()

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(materiasUnicas) { nombreMateria ->
                        val unidadesDeEstaMateria = listaUnidades.filter { it.materia == nombreMateria }

                        // Tabla visual estilo Café
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, cafeMedio.copy(alpha = 0.3f)),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column {
                                // ENCABEZADO: Nombre de la materia
                                Box(modifier = Modifier.fillMaxWidth().background(cafeProfundo).padding(8.dp)) {
                                    Text(
                                        text = nombreMateria.uppercase(),
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                // FILA DE TITULOS: U1 a U9
                                Row(modifier = Modifier.fillMaxWidth().background(cremaFondo)) {
                                    Box(modifier = Modifier.weight(2f).border(0.5.dp, Color.LightGray).padding(4.dp), contentAlignment = Alignment.Center) {
                                        Text("Unidades", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                    for (i in 1..9) {
                                        Box(modifier = Modifier.weight(1f).border(0.5.dp, Color.LightGray).padding(4.dp), contentAlignment = Alignment.Center) {
                                            Text("U$i", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                // FILA DE DATOS: Calif / Faltas
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    // Etiqueta lateral
                                    Box(modifier = Modifier.weight(2f).border(0.5.dp, Color.LightGray).padding(vertical = 4.dp), contentAlignment = Alignment.Center) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("Calif.", fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                            Text("Faltas", fontSize = 8.sp, color = Color.Gray)
                                        }
                                    }

                                    // Celdas U1 a U9
                                    for (i in 1..9) {
                                        // Buscamos si existe la unidad i en la lista
                                        val uActual = unidadesDeEstaMateria.find { it.unidad == i.toString() || it.unidad == "$i" }
                                        val calif = uActual?.calificacion ?: "-"

                                        Box(modifier = Modifier.weight(1f).border(0.5.dp, Color.LightGray).padding(vertical = 4.dp), contentAlignment = Alignment.Center) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text = calif,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (calif != "-" && (calif.toIntOrNull() ?: 0) < 70) Color.Red else Color.Black
                                                )
                                                Text("0", fontSize = 8.sp, color = Color.Gray)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}