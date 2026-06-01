package com.example.inventa2.ui

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.inventa2.data.Producto
import com.example.inventa2.viewmodel.InventarioViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter

fun generarImagenCodigoBarras(codigo: String): Bitmap? {
    if (codigo.isBlank()) return null
    return try {
        val bitMatrix = MultiFormatWriter().encode(codigo, BarcodeFormat.CODE_128, 600, 200)
        val bmp = Bitmap.createBitmap(bitMatrix.width, bitMatrix.height, Bitmap.Config.RGB_565)
        for (x in 0 until bitMatrix.width) {
            for (y in 0 until bitMatrix.height) {
                bmp.setPixel(x, y, if (bitMatrix.get(x, y)) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
        bmp
    } catch (e: Exception) {
        null
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventarioScreen(
    viewModel: InventarioViewModel,
    onAgregarClick: () -> Unit
) {
    val listaProductos by viewModel.productos.collectAsState(initial = emptyList())
    var productoSeleccionado by remember { mutableStateOf<Producto?>(null) }
    var textoBusqueda by remember { mutableStateOf("") }

    // NUEVO: Bandera para mostrar la alerta de confirmación
    var mostrarAlertaBorrado by remember { mutableStateOf(false) }

    val listaFiltrada = listaProductos.filter { producto ->
        producto.nombre.contains(textoBusqueda, ignoreCase = true) ||
                producto.categoria.contains(textoBusqueda, ignoreCase = true) ||
                producto.codigoBarras.contains(textoBusqueda)
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onAgregarClick() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp)
        ) {
            Text(
                text = "Control de Almacén",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = textoBusqueda,
                onValueChange = { textoBusqueda = it },
                placeholder = { Text("Buscar por nombre, categoría o código...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                shape = MaterialTheme.shapes.medium,
                singleLine = true
            )

            if (listaFiltrada.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(if (textoBusqueda.isEmpty()) "La bodega está vacía." else "No se encontraron resultados.", color = Color.Gray)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(listaFiltrada) { producto ->
                        OutlinedCard(
                            modifier = Modifier.fillMaxWidth().clickable { productoSeleccionado = producto },
                            colors = CardDefaults.outlinedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                        ) {
                            Column(modifier = Modifier.padding(18.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(producto.categoria.uppercase(), fontSize = 11.sp, color = MaterialTheme.colorScheme.secondary)
                                        Text(producto.nombre, fontWeight = FontWeight.Bold, fontSize = 19.sp)
                                    }
                                    Text("$${producto.precio}", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("ID: ${producto.codigoBarras}", fontSize = 13.sp, color = Color.Gray)
                                    val bajoStock = producto.stock <= 3
                                    SuggestionChip(
                                        onClick = { },
                                        label = { Text("Stock: ${producto.stock} u.", fontWeight = FontWeight.Bold) },
                                        colors = SuggestionChipDefaults.suggestionChipColors(
                                            containerColor = if (bajoStock) Color(0xFFFCE8E6) else Color(0xFFE6F4EA),
                                            labelColor = if (bajoStock) Color(0xFFC5221F) else Color(0xFF137333)
                                        ),
                                        border = SuggestionChipDefaults.suggestionChipBorder(enabled = true, borderColor = Color.Transparent)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Ventana emergente
        if (productoSeleccionado != null) {
            Dialog(onDismissRequest = { productoSeleccionado = null }) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("ETIQUETA DE CONTROL LOGÍSTICO", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp, color = Color.Black)
                        Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.Black, thickness = 1.dp)

                        Text(productoSeleccionado!!.nombre, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text("CATEGORÍA: ${productoSeleccionado!!.categoria.uppercase()}", fontSize = 12.sp, color = Color.DarkGray)

                        Spacer(modifier = Modifier.height(20.dp))

                        val bitmap = generarImagenCodigoBarras(productoSeleccionado!!.codigoBarras)
                        if (bitmap != null) {
                            Image(bitmap.asImageBitmap(), contentDescription = "Código de barras", modifier = Modifier.fillMaxWidth().height(85.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(productoSeleccionado!!.codigoBarras, fontSize = 14.sp, color = Color.Black, letterSpacing = 3.sp, fontWeight = FontWeight.Bold)
                        }

                        Divider(modifier = Modifier.padding(vertical = 16.dp), color = Color.LightGray, thickness = 0.5.dp)

                        // Botón de Cerrar normal
                        Button(
                            onClick = { productoSeleccionado = null },
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Text("Cerrar")
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // NUEVO: Botón de Eliminar
                        OutlinedButton(
                            onClick = { mostrarAlertaBorrado = true },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Eliminar producto")
                        }
                    }
                }
            }
        }


        if (mostrarAlertaBorrado && productoSeleccionado != null) {
            AlertDialog(
                onDismissRequest = { mostrarAlertaBorrado = false },
                title = { Text("¿Eliminar equipo?") },
                text = { Text("Estás a punto de borrar '${productoSeleccionado!!.nombre}' de la bodega. Esta acción no se puede deshacer.") },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.eliminarProducto(productoSeleccionado!!)
                            mostrarAlertaBorrado = false
                            productoSeleccionado = null // Cerramos todo
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Sí, eliminar")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { mostrarAlertaBorrado = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}