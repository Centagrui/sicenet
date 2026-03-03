package com.example.sicenet.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa la tabla "perfil_alumno" en la base de datos local (Room).
 * Cada propiedad de esta clase será una columna en SQLite.
 */
@Entity(tableName = "perfil_alumno")
data class AlumnoPerfil(
    // La matrícula es única para cada alumno, por lo que sirve perfectamente como ID.
    @PrimaryKey
    val matricula: String,

    val nombre: String = "",
    val estatus: String = "",
    val inscrito: String = "",
    val carrera: String = "",
    val especialidad: String = "",
    val semestreActual: String = "",
    val creditosTotales: String = "",

    /**
     * Almacena el momento exacto (en milisegundos) en que se guardaron los datos.
     * Sirve para mostrarle al usuario: "Última actualización: hace 5 minutos".
     */
    val fechaActualizacion: Long = System.currentTimeMillis()
)