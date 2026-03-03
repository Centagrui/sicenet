package com.example.sicenet.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Clase sellada (Sealed Class) para definir los destinos de la App.
 * Al ser 'sealed', restringimos que solo los objetos definidos aquí
 * puedan ser rutas válidas en la navegación.
 */
sealed class Destinos(val ruta: String, val titulo: String, val icono: ImageVector) {

    // Cada objeto representa una pantalla específica
    // El 'ruta' es el ID interno, 'titulo' es lo que ve el usuario, 'icono' es el gráfico

    object Perfil : Destinos("perfil", "Mi Perfil", Icons.Default.AccountCircle)

    object Carga : Destinos("carga", "Carga Académica", Icons.Default.DateRange)

    object Kardex : Destinos("kardex", "Kárdex", Icons.Default.List)

    // Pantalla para ver el desglose de unidades (C1, C2, etc.)
    object Unidades : Destinos("unidades", "Calificaciones Parciales", Icons.Default.Star)

    // Pantalla para ver el promedio final de las materias
    object Finales : Destinos("finales", "Calificaciones Finales", Icons.Default.CheckCircle)
}

/**
 * Lista global para generar la interfaz de navegación.
 * Se utiliza para iterar y crear automáticamente los elementos del
 * Menú Lateral (Navigation Drawer) o la Barra Inferior (BottomBar).
 */
val listaDestinos = listOf(
    Destinos.Perfil,
    Destinos.Carga,
    Destinos.Kardex,
    Destinos.Unidades,
    Destinos.Finales
)