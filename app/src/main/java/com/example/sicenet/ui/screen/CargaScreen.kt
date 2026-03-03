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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sicenet.model.Materia
import com.example.sicenet.ui.SicenetViewModel

/**
 * Pantalla que muestra el horario y materias actuales del alumno.
 * @param vm Instancia del ViewModel para acceder a los datos.
 * @param onOpenMenu Callback para abrir el Drawer de navegación.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CargaScreen(vm: SicenetViewModel, onOpenMenu: () -> Unit) {
    // Definición de la gama de colores tipo "Café/Ocre" para la identidad visual
    val cafeProfundo = Color(0xFF3E2723)
    val cafeMedio = Color(0xFF5D4037)
    val cafeClaro = Color(0xFFD7CCC8)
    val cremaFondo = Color(0xFFEFEBE9)

    // Observamos el Flow de materias desde la DB local y lo convertimos a un Estado de Compose
    val materias by vm.materiasLocal.collectAsState(initial = emptyList())
    val context = LocalContext.current

    /**
     * LaunchedEffect con 'Unit' se ejecuta solo una vez cuando se monta la pantalla.
     * Lanza la sincronización para que el Worker busque datos nuevos en el servidor.
     */
    LaunchedEffect(Unit) {
        vm.sincronizarDato("CARGA")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Carga Académica", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onOpenMenu) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú Principal", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = cafeProfundo,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        // Estado de carga: Si la lista está vacía, mostramos un indicador de progreso
        if (materias.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = cafeProfundo)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Cargando materias...", style = MaterialTheme.typography.bodyMedium, color = cafeMedio)
                }
            }
        } else {
            // Lista eficiente de materias (solo renderiza lo que se ve en pantalla)
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(materias) { materia ->
                    // Componente visual para cada materia
                    MateriaCard(materia = materia, colorPrimario = cafeProfundo, colorFondo = cremaFondo)
                }
            }
        }
    }
}

/**
 * Componente visual individual para representar una materia.
 */
@Composable
fun MateriaCard(materia: Materia, colorPrimario: Color, colorFondo: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorFondo
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            // Nombre de la Materia
            Text(
                text = materia.nombre,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = colorPrimario
            )

            // Línea divisora sutil
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 8.dp),
                color = colorPrimario.copy(alpha = 0.2f)
            )

            // Fila de información secundaria (Grupo)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Grupo: ${materia.grupo}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Información del Docente
            Text(
                text = "Profesor: ${materia.profesor}",
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                color = Color.DarkGray
            )
        }
    }
}