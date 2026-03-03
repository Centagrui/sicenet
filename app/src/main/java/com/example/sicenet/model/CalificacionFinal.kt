package com.example.sicenet.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// tabla para BD de la room
@Entity(tableName = "calificaciones_finales")
data class CalificacionFinal(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val materia: String,
    val calificacion: String
)