package com.example.sicenet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sicenet.data.RetrofitClient
import com.example.sicenet.data.SicenetRepository
import com.example.sicenet.data.ISicenetRepository // Importamos la interfaz
import com.example.sicenet.ui.SicenetViewModel
import com.example.sicenet.ui.screen.LoginScreen
import com.example.sicenet.ui.screen.ProfileScreen
import com.example.sicenet.ui.theme.SicenetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Configuramos las dependencias UNA SOLA VEZ aquí arriba
        val apiService = RetrofitClient.apiService

        // Declaramos el repositorio usando la Interfaz como tipo
        val repository: ISicenetRepository = SicenetRepository(apiService)

        // El ViewModel recibe la interfaz, cumpliendo con la arquitectura MVVM
        val viewModel = SicenetViewModel(repository)

        setContent {
            SicenetTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "login"
                    ) {
                        // Ruta para la pantalla de Login
                        composable("login") {
                            LoginScreen(vm = viewModel) {
                                // Al tener éxito, navegamos a perfil
                                navController.navigate("perfil")
                            }
                        }

                        // Ruta para la pantalla de Perfiil
                        composable("perfil") {
                            // Solo llamamos a la pantalla, el viewModel ya tiene los datos
                            ProfileScreen(vm = viewModel)
                        }
                    }
                }
            }
        }
    }
}