package com.example.sicenet.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "perfil_alumno")
data class AlumnoPerfil(
    @PrimaryKey
    val matricula: String, // Room necesita una llave primaria obligatoria
    val nombre: String = "",
    val estatus: String = "",
    val inscrito: String = "",
    val carrera: String = "",
    val especialidad: String = "",
    val semestreActual: String = "",
    val creditosTotales: String = "",
    // Este campo es nuevo para cumplir con el requisito de "fecha de última actualización"
    val fechaActualizacion: Long = System.currentTimeMillis()
)