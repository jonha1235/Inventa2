package com.example.inventa2.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductoDao {

    // Le quitamos el "suspend" a todo
    @Insert
    fun insertarProducto(producto: Producto): Long

    @Update
    fun actualizarProducto(producto: Producto): Int

    @Query("SELECT * FROM tabla_productos ORDER BY nombre ASC")
    fun obtenerTodos(): Flow<List<Producto>>

    @Query("SELECT * FROM tabla_productos WHERE codigoBarras = :codigo LIMIT 1")
    fun buscarPorCodigo(codigo: String): Producto?
    
    @Delete
    fun eliminarProducto(producto: Producto): Int
}