package com.example.sicenet.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa la tabla "carga_academica" en la base de datos local.
 * Esta entidad almacena el horario y detalles de las materias que el alumno
 * está cursando en el semestre actual.
 */
@Entity(tableName = "carga_academica")
data class Materia(
    /**
     * La 'clave' actúa como identificador único.
     * En tu repositorio, estás asignando el nombre de la materia a este campo
     * para asegurar que cada materia sea una fila única en la tabla.
     */
    @PrimaryKey val clave: String,

    val nombre: String,       // Nombre oficial de la asignatura
    val profesor: String = "", // Nombre del docente que imparte la materia
    val creditos: String = "0", // Valor en créditos

    // Campos para el horario (pueden venir vacíos según la respuesta del servidor)
    val lunes: String = "",
    val martes: String = "",
    val miercoles: String = "",
    val jueves: String = "",
    val viernes: String = "",

    val grupo: String         // Identificador del grupo (ej. "7A", "A", etc.)
)