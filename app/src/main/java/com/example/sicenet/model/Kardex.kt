package com.example.sicenet.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kardex")
data class Kardex(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val materia: String,
    val calificacion: String,
    val periodo: String,
    val creditos: String
)