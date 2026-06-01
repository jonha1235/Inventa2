package com.example.inventa2.ui

import androidx.activity.compose.BackHandler // <-- NUEVO: Para controlar el botón físico
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.inventa2.viewmodel.InventarioViewModel

@Composable
fun DashboardScreen(
    viewModel: InventarioViewModel,
    onVerInventarioClick: () -> Unit,
    onEscanearClick: () -> Unit,
    onRegistroClick: () -> Unit,
    onSalirClick: () -> Unit
) {
    val listaProductos by viewModel.productos.collectAsState(initial = emptyList())

    var mostrarAlertaSalir by remember { mutableStateOf(false) }

    BackHandler {
        mostrarAlertaSalir = true
    }

    val totalArticulos = listaProductos.sumOf { it.stock }
    val inversionTotal = listaProductos.sumOf { it.stock * it.precio }
    val productosEnAlerta = listaProductos.count { it.stock in 1..3 }
    val productosAgotados = listaProductos.count { it.stock == 0 }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Panel de Control",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(top = 16.dp, bottom = 24.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ElevatedCard(
                modifier = Modifier.weight(1f).height(100.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(12.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Valor en Bodega", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("$$inversionTotal", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }

            ElevatedCard(
                modifier = Modifier.weight(1f).height(100.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(12.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Total de Piezas", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("$totalArticulos u.", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        if (productosEnAlerta > 0 || productosAgotados > 0) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFCE8E6))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Warning, contentDescription = "Alerta", tint = Color(0xFFC5221F))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Alertas de Stock", fontWeight = FontWeight.Bold, color = Color(0xFFC5221F))
                        if (productosAgotados > 0) Text("• $productosAgotados equipos AGOTADOS", fontSize = 12.sp, color = Color.DarkGray)
                        if (productosEnAlerta > 0) Text("• $productosEnAlerta equipos por agotarse", fontSize = 12.sp, color = Color.DarkGray)
                    }
                }
            }
        } else {
            Spacer(modifier = Modifier.height(32.dp))
        }

        ElevatedButton(
            onClick = { onEscanearClick() },
            modifier = Modifier.fillMaxWidth().height(70.dp).padding(bottom = 12.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(Icons.Default.Search, contentDescription = "Escanear", modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text("Escanear producto", fontSize = 16.sp)
        }

        ElevatedButton(
            onClick = { onRegistroClick() },
            modifier = Modifier.fillMaxWidth().height(70.dp).padding(bottom = 12.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(Icons.Default.AddCircle, contentDescription = "Movimiento", modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text("Registrar entrada/salida", fontSize = 16.sp)
        }

        ElevatedButton(
            onClick = { onVerInventarioClick() },
            modifier = Modifier.fillMaxWidth().height(70.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            Icon(Icons.Default.List, contentDescription = "Inventario", modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text("Ver inventario local", fontSize = 16.sp)
        }
    }


    if (mostrarAlertaSalir) {
        AlertDialog(
            onDismissRequest = { mostrarAlertaSalir = false },
            title = { Text("¿Cerrar sesión?") },
            text = { Text("¿Estás seguro de que deseas salir de tu cuenta y regresar al inicio de sesión?") },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarAlertaSalir = false
                        onSalirClick() // Ejecutamos la salida real
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Sí, salir")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarAlertaSalir = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}