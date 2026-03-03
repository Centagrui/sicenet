package com.example.sicenet.data

/**
 * Esta es una "clase de datos" simplificada.
 * Se utiliza para agrupar únicamente la información que el usuario verá en la pantalla
 * de calificaciones finales, ignorando datos técnicos (como IDs o claves de materia).
 */
data class FinalUIModel(
    val materia: String,      // El nombre legible de la materia (ej. "Cálculo Integral")
    val calificacion: String   // El número o letra del promedio final (ej. "95" o "NA")
)