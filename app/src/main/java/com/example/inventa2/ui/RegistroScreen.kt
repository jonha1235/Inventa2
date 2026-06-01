package com.example.inventa2.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.inventa2.viewmodel.InventarioViewModel

@Composable
fun RegistroScreen(
    viewModel: InventarioViewModel,
    codigoInicial: String,
    onRegistroExitoso: () -> Unit
) {
    val contexto = LocalContext.current

    var codigoBarras by remember { mutableStateOf(if (codigoInicial == "vacio") "" else codigoInicial) }
    var nombre by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var esEntrada by remember { mutableStateOf(true) }

    // NUEVO: Bandera para saber si el producto ya existe y bloquear la edición
    var existeProducto by remember { mutableStateOf(false) }

    // Función auxiliar para buscar el producto y rellenar campos si existe
    fun verificarProducto(codigo: String) {
        if (codigo.isNotBlank()) {
            val prod = viewModel.buscarPorCodigo(codigo)
            if (prod != null) {
                nombre = prod.nombre
                categoria = prod.categoria
                precio = prod.precio.toString()
                existeProducto = true
            } else {
                existeProducto = false
            }
        } else {
            existeProducto = false
        }
    }

    // Buscamos automáticamente al cargar la pantalla si venimos del escáner
    LaunchedEffect(codigoInicial) {
        if (codigoInicial != "vacio") {
            verificarProducto(codigoInicial)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Movimiento de Bodega",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 16.dp, bottom = 24.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { esEntrada = true },
                modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (esEntrada) MaterialTheme.colorScheme.primary else Color.LightGray
                )
            ) {
                Text("Entrada (+)", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { esEntrada = false },
                modifier = Modifier.weight(1f).height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!esEntrada) MaterialTheme.colorScheme.error else Color.LightGray
                )
            ) {
                Text("Salida (-)", fontWeight = FontWeight.Bold)
            }
        }

        // Campo de Código de Barras
        OutlinedTextField(
            value = codigoBarras,
            onValueChange = { nuevoCodigo ->
                codigoBarras = nuevoCodigo
                verificarProducto(nuevoCodigo)
            },
            label = { Text("Código de Barras") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            trailingIcon = {
                // NUEVO: Un botón al final del campo de texto
                IconButton(onClick = {
                    // Genera un código de 13 dígitos
                    val aleatorio = "750" + (1000000000L..9999999999L).random().toString()
                    codigoBarras = aleatorio
                    verificarProducto(aleatorio)
                }) {
                    Icon(
                        androidx.compose.material.icons.Icons.Default.Add, // Puedes cambiar el ícono si gustas
                        contentDescription = "Generar código automático",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre del equipo") },
            leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            singleLine = true,
            enabled = !existeProducto // <-- SE BLOQUEA SI YA EXISTE
        )

        OutlinedTextField(
            value = categoria,
            onValueChange = { categoria = it },
            label = { Text("Categoría") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            singleLine = true,
            enabled = !existeProducto // <-- SE BLOQUEA SI YA EXISTE
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = stock,
                onValueChange = { stock = it },
                label = { Text("Cantidad") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true
                // El campo stock SIEMPRE queda habilitado para editar la entrada o salida
            )

            OutlinedTextField(
                value = precio,
                onValueChange = { precio = it },
                label = { Text("Precio ($)") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                enabled = !existeProducto // SE BLOQUEA SI YA EXISTE
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                if (nombre.isNotBlank() && stock.isNotBlank() && precio.isNotBlank()) {
                    viewModel.guardarProducto(
                        nombre = nombre,
                        categoria = categoria,
                        stock = stock.toIntOrNull() ?: 0,
                        precio = precio.toDoubleOrNull() ?: 0.0,
                        codigoBarras = codigoBarras,
                        esEntrada = esEntrada
                    )
                    Toast.makeText(contexto, "Movimiento registrado con éxito", Toast.LENGTH_SHORT).show()
                    onRegistroExitoso()
                } else {
                    Toast.makeText(contexto, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Icon(Icons.Default.ShoppingCart, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(if (esEntrada) "Guardar Entrada" else "Registrar Salida", fontSize = 16.sp)
        }
    }
}