package com.example.sicenet.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa la tabla "kardex" en la base de datos local.
 * Aquí se guarda el registro histórico de todas las materias acreditadas o cursadas.
 */
@Entity(tableName = "kardex")
data class Kardex(
    /**
     * ID único autoincremental.
     * Como una materia puede aparecer varias veces (por ejemplo, si se recursó),
     * generamos un ID automático para que Room pueda distinguir cada fila.
     */
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val materia: String,      // Nombre de la asignatura
    val calificacion: String, // Calificación obtenida (ej. "80", "100", "AC")
    val creditos: String,     // Valor en créditos de la materia
    val periodo: String       // Semestre/Año en que se cursó (ej. "Ago-Dic 2023")
)