package com.example.inventa2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.inventa2.data.Inventa2Database
import com.example.inventa2.ui.DashboardScreen
import com.example.inventa2.ui.EscanerScreen
import com.example.inventa2.ui.InventarioScreen
import com.example.inventa2.ui.LoginScreen
import com.example.inventa2.ui.RegistroScreen
import com.example.inventa2.ui.theme.Inventa2Theme
import com.example.inventa2.viewmodel.InventarioViewModel
import com.example.inventa2.viewmodel.InventarioViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val baseDatos = Inventa2Database.obtenerInstancia(this)
        val dao = baseDatos.productoDao()

        enableEdgeToEdge()
        setContent {
            Inventa2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(navController = navController, startDestination = "login") {

                        composable("login") {
                            LoginScreen(onLoginClick = { navController.navigate("dashboard") })
                        }

                        composable("dashboard") {
                            val viewModel: InventarioViewModel = viewModel(factory = InventarioViewModelFactory(dao))
                            DashboardScreen(
                                viewModel = viewModel,
                                onVerInventarioClick = { navController.navigate("inventario") },
                                onEscanearClick = { navController.navigate("escaner") },
                                onRegistroClick = { navController.navigate("registro/vacio") },
                                onSalirClick = { navController.popBackStack() }
                            )
                        }

                        composable("inventario") {
                            val viewModel: InventarioViewModel = viewModel(factory = InventarioViewModelFactory(dao))
                            InventarioScreen(
                                viewModel = viewModel,
                                onAgregarClick = { navController.navigate("registro/vacio") } // <-- ATAJO CONECTADO
                            )
                        }

                        composable("escaner") {
                            EscanerScreen(
                                onCodigoAceptado = { codigo ->
                                    // Cuando escanea, viaja directo al registro con el código pegado
                                    navController.navigate("registro/$codigo") {
                                        popUpTo("dashboard")
                                    }
                                }
                            )
                        }


                        composable("registro/{codigo}") { backStackEntry ->
                            // Rescatamos el código del paquete de viaje
                            val codigoViajero = backStackEntry.arguments?.getString("codigo") ?: "vacio"

                            val viewModel: InventarioViewModel = viewModel(factory = InventarioViewModelFactory(dao))
                            RegistroScreen(
                                viewModel = viewModel,
                                codigoInicial = codigoViajero, // Se lo pasamos a la pantalla
                                onRegistroExitoso = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}