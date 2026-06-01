package com.example.inventa2.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabla_productos")
data class Producto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,           // Ej. "Monitor Dell 24 pulgadas"
    val categoria: String,        // Ej. "Monitores", "Computadoras", "Accesorios"
    val stock: Int,               // Cantidad disponible en bodega
    val precio: Double,           // Costo del equipo
    val codigoBarras: String      // El código que leerá la cámara
)