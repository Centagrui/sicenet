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
import androidx.compose.ui.unit.dp
import com.example.sicenet.model.Materia
import com.example.sicenet.ui.SicenetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CargaScreen(vm: SicenetViewModel, onOpenMenu: () -> Unit) {
    // Observamos los datos de la base de datos local
    val materias by vm.materiasLocal.collectAsState(initial = emptyList())
    val context = LocalContext.current

    // Sincronización automática con el servidor al cargar la pantalla
    LaunchedEffect(Unit) {
        vm.sincronizarCarga(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Carga Académica") },
                navigationIcon = {
                    IconButton(onClick = onOpenMenu) {
                        Icon(Icons.Default.Menu, contentDescription = "Menú Principal")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { padding ->
        if (materias.isEmpty()) {
            // Pantalla de carga mientras se obtienen los datos
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Cargando materias...", style = MaterialTheme.typography.bodyMedium)
                }
            }
        } else {
            // Lista visual de las materias
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(16.dp), // Margen alrededor de toda la lista
                verticalArrangement = Arrangement.spacedBy(12.dp) // Espacio entre cada tarjeta
            ) {
                items(materias) { materia ->
                    // Llamamos a la función visual que definiste abajo
                    MateriaCard(materia = materia)
                }
            }
        }
    }
}

@Composable
fun MateriaCard(materia: Materia) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = materia.nombre,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Grupo: ${materia.grupo}",
                    style = MaterialTheme.typography.bodyMedium
                )
                //Text(
                //
                //    text = "Créditos: ${materia.creditos}",
                   // style = MaterialTheme.typography.labelLarge,
                   // fontWeight = FontWeight.SemiBold
             //   )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Profesor: ${materia.profesor}",
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
    }
}