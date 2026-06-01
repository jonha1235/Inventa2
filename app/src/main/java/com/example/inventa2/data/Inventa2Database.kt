package com.example.inventa2.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Producto::class], version = 1, exportSchema = false)
abstract class Inventa2Database : RoomDatabase() {

    abstract fun productoDao(): ProductoDao

    companion object {
        @Volatile
        private var INSTANCIA: Inventa2Database? = null

        fun obtenerInstancia(contexto: Context): Inventa2Database {
            return INSTANCIA ?: synchronized(this) {
                val instancia = Room.databaseBuilder(
                    contexto.applicationContext,
                    Inventa2Database::class.java,
                    "inventa2_bd"
                )
                    .allowMainThreadQueries() // <-- ¡ESTA ES LA LÍNEA MÁGICA!
                    .build()

                INSTANCIA = instancia
                instancia
            }
        }
    }
}