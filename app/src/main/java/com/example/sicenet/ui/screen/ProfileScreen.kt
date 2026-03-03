package com.example.sicenet.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sicenet.ui.SicenetViewModel

/**
 * Pantalla de Perfil del Alumno.
 * Muestra la información personal y académica recuperada del servidor y almacenada en Room.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(vm: SicenetViewModel, onOpenMenu: () -> Unit) {
    // Estado del scroll para pantallas pequeñas donde el contenido sobrepase el alto
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // Observamos el perfil desde la base de datos local.
    // Usamos 'null' como valor inicial para manejar el estado de carga.
    val alumno by vm.perfilLocal.collectAsState(initial = null)

    // Colores Café consistentes con el diseño global
    val cafeProfundo = Color(0xFF3E2723)
    val cafeClaro = Color(0xFFD7CCC8)

    /**
     * Sincronización automática: Al entrar a la pantalla, se dispara el Worker
     * para traer los datos de perfil más recientes del SICENET.
     */
    LaunchedEffect(Unit) {
        vm.sincronizarDato("PERFIL")
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
                // Habilitamos el scroll vertical
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // --- SECCIÓN DEL AVATAR (ICONO DE PERSONA) ---
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape) // Corta el fondo en forma circular
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

            // Lógica condicional: Si el alumno ya fue cargado de la DB
            if (alumno != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Reutilizamos el componente DatoItem para cada campo
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
                // Pantalla de carga (Shimmer/Spinner) mientras 'alumno' sea null
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

/**
 * Componente interno para mostrar una etiqueta y su valor con estilo consistente.
 */
@Composable
fun DatoItem(label: String, valor: String, colorLabel: Color) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label.uppercase(), // Etiquetas en mayúsculas para mejor jerarquía visual
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