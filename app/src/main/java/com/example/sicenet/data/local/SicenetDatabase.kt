package com.example.sicenet.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sicenet.model.*

@Database(
    entities = [
        AlumnoPerfil::class,
        Materia::class,
        Kardex::class,
        UnidadCalificacion::class,
        CalificacionFinal::class
    ],
    version = 4,
    exportSchema = false
)
abstract class SicenetDatabase : RoomDatabase() {

    abstract fun sicenetDao(): SicenetDao

    /**
     * El bloque 'companion object' permite acceder a los métodos sin instanciar la clase.
     * Aquí implementamos el patrón SINGLETON para que solo exista una instancia de la DB.
     */
    companion object {
        // @Volatile  siempre actualizado para todos los hilos.
        @Volatile
        private var Instance: SicenetDatabase? = null

        fun getDatabase(context: Context): SicenetDatabase {
            // Si 'Instance' no es nula, la regresa. Si es nula, entra al bloque synchronized.
            return Instance ?: synchronized(this) {
                // synchronized evita que dos hilos creen dos bases de datos al mismo tiempo.
                Room.databaseBuilder(
                    context.applicationContext,
                    SicenetDatabase::class.java,
                    "sicenet_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}