package com.example.sicenet.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "carga_academica")
data class Materia(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clave: String,
    val nombre: String,
    val profesor: String,
    val lunes: String = "",
    val martes: String = "",
    val miercoles: String = "",
    val jueves: String = "",
    val viernes: String = ""
)