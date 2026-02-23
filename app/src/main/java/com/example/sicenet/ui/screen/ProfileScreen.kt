package com.example.sicenet.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.sicenet.ui.SicenetViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(vm: SicenetViewModel, onOpenMenu: () -> Unit) {
    val scrollState = rememberScrollState()
    val perfil by vm.perfilLocal.collectAsState(initial = null)
    val context = LocalContext.current

    // Sincronización automática al entrar (Punto 2b)
    LaunchedEffect(Unit) {
        vm.sincronizarPerfil(context)
    }

    // CORRECCIÓN: Observamos el perfil desde Room (base de datos local)
    val alumno by vm.perfilLocal.collectAsState(initial = null)

    // Ya no necesitas el LaunchedEffect para cargarPerfil(),
    // porque el Worker de inicio de sesión ya guardó los datos en Room.

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil") },
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
            Text(
                text = "Perfil Académico",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (alumno != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Usamos !! porque ya verificamos que alumno no es nulo
                        DatoItem(label = "Nombre", valor = alumno!!.nombre)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        DatoItem(label = "Matrícula", valor = alumno!!.matricula)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        DatoItem(label = "Estatus", valor = alumno!!.estatus)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        DatoItem(label = "Carrera", valor = alumno!!.carrera)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        DatoItem(label = "Especialidad", valor = alumno!!.especialidad)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        DatoItem(label = "Semestre Actual", valor = alumno!!.semestreActual)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                        DatoItem(label = "Créditos Totales", valor = alumno!!.creditosTotales)
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Esperando datos locales...")
                    }
                }
            }
        }
    }
}
@Composable
fun DatoItem(label: String, valor: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = if (valor.isEmpty()) "No disponible" else valor,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold
        )
    }
}