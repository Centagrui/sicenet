package com.example.sicenet.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calificaciones_unidades")
data class UnidadCalificacion(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val materia: String,
    val unidad: String,
    val calificacion: String
)