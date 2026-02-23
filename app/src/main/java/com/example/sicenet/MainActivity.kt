package com.example.sicenet

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material3.DrawerValue.Closed
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sicenet.data.RetrofitClient
import com.example.sicenet.data.SicenetLocalRepository
import com.example.sicenet.data.SicenetRepository
import com.example.sicenet.data.local.SicenetDatabase
import com.example.sicenet.navigation.Destinos
import com.example.sicenet.ui.SicenetViewModel
import com.example.sicenet.ui.screen.* import com.example.sicenet.ui.theme.SicenetTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SicenetTheme {
                val context = LocalContext.current
                val application = context.applicationContext as Application

                // 1. Inicializamos los repositorios (Red y Local)
                val apiService = RetrofitClient.apiService
                val repository = SicenetRepository(apiService)
                val database = SicenetDatabase.getDatabase(application)
                val localRepo = SicenetLocalRepository(database.sicenetDao())

                // 2. Usamos la Factory actualizada con los 3 parámetros necesarios
                val sicenetViewModel: SicenetViewModel = viewModel(
                    factory = SicenetViewModelFactory(repository, localRepo, application)
                )

                val navController = rememberNavController()
                val drawerState = rememberDrawerState(initialValue = Closed)
                val scope = rememberCoroutineScope()

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet {
                            Text(
                                "Menú SICENET",
                                modifier = Modifier.padding(16.dp),
                                style = MaterialTheme.typography.titleLarge
                            )
                            HorizontalDivider()

                            // Usamos el objeto Destinos para mantener consistencia
                            NavigationDrawerItem(
                                label = { Text(Destinos.Perfil.titulo) },
                                selected = false,
                                onClick = {
                                    navController.navigate(Destinos.Perfil.ruta)
                                    scope.launch { drawerState.close() }
                                }
                            )
                            NavigationDrawerItem(
                                label = { Text(Destinos.Carga.titulo) },
                                selected = false,
                                onClick = {
                                    navController.navigate(Destinos.Carga.ruta)
                                    scope.launch { drawerState.close() }
                                }
                            )
                            NavigationDrawerItem(
                                label = { Text(Destinos.Kardex.titulo) },
                                selected = false,
                                onClick = {
                                    navController.navigate(Destinos.Kardex.ruta)
                                    scope.launch { drawerState.close() }
                                }
                            )
                            NavigationDrawerItem(
                                label = { Text(Destinos.Finales.titulo) },
                                selected = false,
                                onClick = {
                                    navController.navigate(Destinos.Finales.ruta)
                                    scope.launch { drawerState.close() }
                                }
                            )
                        }
                    }
                ) {
                    NavHost(navController = navController, startDestination = "login") {
                        composable("login") {
                            LoginScreen(vm = sicenetViewModel) {
                                navController.navigate(Destinos.Perfil.ruta) {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        }

                        composable(Destinos.Perfil.ruta) {
                            ProfileScreen(vm = sicenetViewModel, onOpenMenu = { scope.launch { drawerState.open() } })
                        }

                        composable(Destinos.Carga.ruta) {
                            CargaScreen(vm = sicenetViewModel, onOpenMenu = { scope.launch { drawerState.open() } })
                        }

                        composable(Destinos.Kardex.ruta) {
                            KardexScreen(vm = sicenetViewModel, onOpenMenu = { scope.launch { drawerState.open() } })
                        }

                        composable(Destinos.Finales.ruta) {
                            FinalesScreen(vm = sicenetViewModel, onOpenMenu = { scope.launch { drawerState.open() } })
                        }
                    }
                }
            }
        }
    }
}

// 3. FACTORY ACTUALIZADA: Ahora recibe los 3 parámetros que pide SicenetViewModel
class SicenetViewModelFactory(
    private val repository: SicenetRepository,
    private val localRepository: SicenetLocalRepository,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SicenetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SicenetViewModel(repository, localRepository, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}