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
    // Observamos las unidades desde Room
    val listaUnidades by vm.unidadesLocal.collectAsState(initial = emptyList())
    val context = LocalContext.current

    val cafeProfundo = Color(0xFF3E2723)
    val cafeMedio = Color(0xFF5D4037)
    val cremaFondo = Color(0xFFF5F5F5)

// para ña fecha al momento de sincronizarlo
    val sharedPref = context.getSharedPreferences("sicenet_prefs", android.content.Context.MODE_PRIVATE)
    val ultimaSinc = sharedPref.getString("fecha_unidades", "Sin sincronizar") ?: "Sin sincronizar"


    LaunchedEffect(Unit) {
        vm.sincronizarDato("UNIDADES")
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

                val materiasUnicas = listaUnidades.map { it.materia }.distinct()

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(materiasUnicas) { nombreMateria ->
                        val unidadesDeEstaMateria = listaUnidades.filter { it.materia == nombreMateria }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, cafeMedio.copy(alpha = 0.3f)),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Column {
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

                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Box(modifier = Modifier.weight(2f).border(0.5.dp, Color.LightGray).padding(vertical = 4.dp), contentAlignment = Alignment.Center) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text("Calif.", fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                            Text("Faltas", fontSize = 8.sp, color = Color.Gray)
                                        }
                                    }

                                    for (i in 1..9) {
                                        val uActual = unidadesDeEstaMateria.find { it.unidad == i.toString() }
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