package com.example.sicenet.model

// clase diseñada  para transportar datos.
data class AlumnoPerfil(
    // Definimos cada atributo como String y les asignamos un valor por defecto vacío ""
    // Esto evita errores de 'NullPointerException' si algún dato no llega del servidor.

    val nombre: String = "",           // Nombre completo del alumno
    val matricula: String = "",        // Número de control
    val estatus: String ="",           // Si está vigente o no
    val inscrito: String ="",          // Estado de inscripción actual
    val carrera: String = "",          // Carrera que cursa
    val especialidad: String = "",     // Especialidad de su plan de estudios
    val semestreActual: String = "",   // El número de semestre en curso
    val creditosTotales: String = "",  // Suma de créditos acumulados
)