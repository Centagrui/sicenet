package com.example.sicenet.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import com.example.sicenet.navigation.Destinos
import com.example.sicenet.ui.screen.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SicenetApp(vm: SicenetViewModel) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Menú SICENET", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.titleLarge)
                HorizontalDivider()

                // Opción Perfil
                NavigationDrawerItem(
                    label = { Text(Destinos.Perfil.titulo) },
                    selected = false,
                    onClick = {
                        navController.navigate(Destinos.Perfil.ruta)
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) }
                )

                // Opción Carga Académica
                NavigationDrawerItem(
                    label = { Text(Destinos.Carga.titulo) },
                    selected = false,
                    onClick = {
                        navController.navigate(Destinos.Carga.ruta)
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.DateRange, contentDescription = null) }
                )

                // Opción Kardex
                NavigationDrawerItem(
                    label = { Text(Destinos.Kardex.titulo) },
                    selected = false,
                    onClick = {
                        navController.navigate(Destinos.Kardex.ruta)
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.Star, contentDescription = null) }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("SICENET") },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = "login",
                modifier = Modifier.padding(paddingValues)
            ) {
                composable("login") {
                    LoginScreen(vm) {
                        navController.navigate(Destinos.Perfil.ruta) {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                }
                composable(Destinos.Perfil.ruta) { ProfileScreen(vm) }
                composable(Destinos.Carga.ruta) { CargaScreen(vm) }
                composable(Destinos.Kardex.ruta) { KardexScreen(vm) }
            }
        }
    }
}

@Composable
fun KardexScreen(x0: SicenetViewModel) {
    TODO("Not yet implemented")
}

@Composable
fun CargaScreen(x0: SicenetViewModel) {
    TODO("Not yet implemented")
}
