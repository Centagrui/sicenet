package com.example.sicenet.model

import androidx.room.Entity
import androidx.room.PrimaryKey
// la tabla para la room

@Entity(tableName = "perfil_alumno")
data class AlumnoPerfil(

    @PrimaryKey
    val matricula: String,

    val nombre: String = "",
    val estatus: String = "",
    val inscrito: String = "",
    val carrera: String = "",
    val especialidad: String = "",
    val semestreActual: String = "",
    val creditosTotales: String = "",

   // para lo de la ultima sincronizacion
    val fechaActualizacion: Long = System.currentTimeMillis()
)