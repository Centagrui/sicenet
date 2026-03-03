package com.example.sicenet.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.ui.graphics.vector.ImageVector


sealed class Destinos(val ruta: String, val titulo: String, val icono: ImageVector) {

   // obj = pantalla

    object Perfil : Destinos("perfil", "Mi Perfil", Icons.Default.AccountCircle)

    object Carga : Destinos("carga", "Carga Académica", Icons.Default.DateRange)

    object Kardex : Destinos("kardex", "Kárdex", Icons.Default.List)


    object Unidades : Destinos("unidades", "Calificaciones Parciales", Icons.Default.Star)


    object Finales : Destinos("finales", "Calificaciones Finales", Icons.Default.CheckCircle)
}


// es el menu de la app
val listaDestinos = listOf(
    Destinos.Perfil,
    Destinos.Carga,
    Destinos.Kardex,
    Destinos.Unidades,
    Destinos.Finales
)