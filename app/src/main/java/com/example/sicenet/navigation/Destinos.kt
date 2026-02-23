package com.example.sicenet.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Destinos(val ruta: String, val titulo: String, val icono: ImageVector) {
    object Perfil : Destinos("perfil", "Mi Perfil", Icons.Default.AccountCircle)
    object Carga : Destinos("carga", "Carga Académica", Icons.Default.DateRange)
    object Kardex : Destinos("kardex", "Kárdex", Icons.Default.List)

    // Representa las calificaciones por unidades (Parciales)
    object Unidades : Destinos("unidades", "Calificaciones Parciales", Icons.Default.Star)

    // Representa las calificaciones finales
    object Finales : Destinos("finales", "Calificaciones Finales", Icons.Default.CheckCircle)
}

// Lista para iterar fácilmente en el Menú (Drawer) y cumplir con el requisito de navegación
val listaDestinos = listOf(
    Destinos.Perfil,
    Destinos.Carga,
    Destinos.Kardex,
    Destinos.Unidades,
    Destinos.Finales
)