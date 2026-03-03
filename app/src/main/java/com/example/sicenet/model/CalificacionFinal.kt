package com.example.sicenet.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa la tabla "calificaciones_finales" en SQLite.
 * Se utiliza para almacenar el promedio final de cada materia del semestre actual.
 */
@Entity(tableName = "calificaciones_finales")
data class CalificacionFinal(
    /**
     * ID autoincremental.
     * Como un alumno tiene muchas materias, no podemos usar la matrícula como llave.
     * Room generará un número único (1, 2, 3...) para cada fila automáticamente.
     */
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val materia: String,    // Nombre de la asignatura (ej: "Programación Móvil")
    val calificacion: String // Nota final o estatus (ej: "95" o "S/N")
)