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
import com.example.sicenet.ui.SicenetViewModel
import com.example.sicenet.ui.screen.LoginScreen
import com.example.sicenet.ui.screen.ProfileScreen
import com.example.sicenet.ui.theme.SicenetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiService = RetrofitClient.apiService
        val repository = SicenetRepository(apiService)

        val viewModel = SicenetViewModel(repository)

        setContent {
            SicenetTheme {
                // Contenedor principal de la interfaz
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 3. Configuramos el NavController para la navegación
                    val navController = rememberNavController()

                    // 4. Definimos el Grafo de Navegación
                    NavHost(
                        navController = navController,
                        startDestination = "login" // La app inicia en el Login
                    ) {
                        // Ruta para la pantalla de Login
                        composable("login") {
                            LoginScreen(vm = viewModel) {
                                // Acción al autenticarse con éxito: Navegar al Perfil
                                navController.navigate("perfil")
                            }
                        }


                        composable("perfil") {
                            ProfileScreen(vm = viewModel)
                        }
                    }
                }
            }
        }
    }
}