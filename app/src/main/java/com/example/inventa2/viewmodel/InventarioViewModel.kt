package com.example.inventa2.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.inventa2.data.Producto
import com.example.inventa2.data.ProductoDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class InventarioViewModel(private val dao: ProductoDao) : ViewModel() {

    val productos: Flow<List<Producto>> = dao.obtenerTodos()

    // NUEVO: Esta función le permite a la pantalla buscar un producto inmediatamente
    fun buscarPorCodigo(codigo: String): Producto? {
        return dao.buscarPorCodigo(codigo)
    }

    fun guardarProducto(nombre: String, categoria: String, stock: Int, precio: Double, codigoBarras: String, esEntrada: Boolean) {
        viewModelScope.launch {
            val productoExistente = if (codigoBarras.isNotBlank()) {
                dao.buscarPorCodigo(codigoBarras)
            } else {
                null
            }

            if (productoExistente != null) {
                val nuevoStock = if (esEntrada) {
                    productoExistente.stock + stock
                } else {
                    val resta = productoExistente.stock - stock
                    if (resta < 0) 0 else resta
                }

                val productoActualizado = productoExistente.copy(
                    stock = nuevoStock,
                    precio = precio
                )
                dao.actualizarProducto(productoActualizado)
            } else {
                val stockInicial = if (esEntrada) stock else 0

                val nuevoProducto = Producto(
                    nombre = nombre,
                    categoria = categoria,
                    stock = stockInicial,
                    precio = precio,
                    codigoBarras = codigoBarras
                )
                dao.insertarProducto(nuevoProducto)
            }
        }
    }
    fun eliminarProducto(producto: Producto) {
        viewModelScope.launch {
            dao.eliminarProducto(producto)
        }
    }
}

class InventarioViewModelFactory(private val dao: ProductoDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventarioViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InventarioViewModel(dao) as T
        }
        throw IllegalArgumentException("ViewModel desconocido")
    }

}