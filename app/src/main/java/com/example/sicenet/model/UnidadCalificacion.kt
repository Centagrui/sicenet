package com.example.sicenet.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa la tabla "calificaciones_unidades" en la base de datos local.
 * Esta tabla almacena el detalle de cada parcial de todas las materias.
 */
@Entity(tableName = "calificaciones_unidades")
data class UnidadCalificacion(
    /**
     * ID único autoincremental.
     * Como una materia tiene muchas unidades (C1, C2, C3...), generamos un
     * ID automático para que cada calificación de unidad sea un registro único.
     */
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val materia: String,      // Nombre de la materia a la que pertenece la unidad
    val unidad: String,       // Número de la unidad (ej. "1", "2", "3")
    val calificacion: String  // La nota obtenida (ej. "90", "70", "NA")
)