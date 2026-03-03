package com.example.sicenet.model

import androidx.room.Entity
import androidx.room.PrimaryKey
// tabla para la bd de la room o see de forma local

@Entity(tableName = "calificaciones_unidades")
data class UnidadCalificacion(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val materia: String,
    val unidad: String,
    val calificacion: String
)