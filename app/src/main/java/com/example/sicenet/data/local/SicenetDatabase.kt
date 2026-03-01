package com.example.sicenet.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sicenet.model.AlumnoPerfil
import com.example.sicenet.model.CalificacionFinal
import com.example.sicenet.model.Materia
import com.example.sicenet.model.Kardex
import com.example.sicenet.model.UnidadCalificacion


@Database(
    entities = [
        AlumnoPerfil::class,
        Materia::class, Kardex::class,
        UnidadCalificacion::class,
        CalificacionFinal::class ]
    ,
    version =4 ,
    exportSchema = false
)
abstract class SicenetDatabase : RoomDatabase() {

    abstract fun sicenetDao(): SicenetDao

    companion object {
        @Volatile
        private var Instance: SicenetDatabase? = null

        fun getDatabase(context: Context): SicenetDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
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