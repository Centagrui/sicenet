package com.example.sicenet.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// tabla de la bd de la room
@Entity(tableName = "carga_academica")
data class Materia(

    @PrimaryKey val clave: String,

    val nombre: String,
    val profesor: String = "",
    val creditos: String = "0",

// esto es para el horario
    val lunes: String = "",
    val martes: String = "",
    val miercoles: String = "",
    val jueves: String = "",
    val viernes: String = "",

    val grupo: String
)