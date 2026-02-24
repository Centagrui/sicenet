package com.example.sicenet.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person // Icono de persona
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sicenet.ui.SicenetViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(vm: SicenetViewModel, onOpenMenu: () -> Unit) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val alumno by vm.perfilLocal.collectAsState(initial = null)

    // Colores Café consistentes
    val cafeProfundo = Color(0xFF3E2723)
    val cafeClaro = Color(0xFFD7CCC8)

    LaunchedEffect(Unit) {
        vm.sincronizarPerfil(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onOpenMenu) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- SECCIÓN DEL ICONO DE PERSONA ---
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(cafeClaro),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier.size(70.dp),
                    tint = cafeProfundo
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Perfil Académico",
                style = MaterialTheme.typography.headlineSmall,
                color = cafeProfundo,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (alumno != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DatoItem(label = "Nombre", valor = alumno!!.nombre, colorLabel = cafeProfundo)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = cafeClaro.copy(alpha = 0.5f))

                        DatoItem(label = "Matrícula", valor = alumno!!.matricula, colorLabel = cafeProfundo)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = cafeClaro.copy(alpha = 0.5f))

                        DatoItem(label = "Estatus", valor = alumno!!.estatus, colorLabel = cafeProfundo)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = cafeClaro.copy(alpha = 0.5f))

                        DatoItem(label = "Carrera", valor = alumno!!.carrera, colorLabel = cafeProfundo)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = cafeClaro.copy(alpha = 0.5f))

                        DatoItem(label = "Especialidad", valor = alumno!!.especialidad, colorLabel = cafeProfundo)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = cafeClaro.copy(alpha = 0.5f))

                        DatoItem(label = "Semestre Actual", valor = alumno!!.semestreActual, colorLabel = cafeProfundo)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = cafeClaro.copy(alpha = 0.5f))

                        DatoItem(label = "Créditos Totales", valor = alumno!!.creditosTotales, colorLabel = cafeProfundo)
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = cafeProfundo)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Cargando perfil...", color = cafeProfundo)
                    }
                }
            }
        }
    }
}

@Composable
fun DatoItem(label: String, valor: String, colorLabel: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = colorLabel,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = if (valor.isEmpty()) "No disponible" else valor,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
    }
}