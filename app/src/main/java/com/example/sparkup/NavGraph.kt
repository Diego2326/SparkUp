package com.example.sparkup.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.sparkup.auth.AuthScreen
import com.example.sparkup.auth.RegisScreen
import com.example.sparkup.ui.theme.screens.*
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import kotlinx.coroutines.CoroutineScope

@Composable
fun AppNavGraph(
    navController: NavHostController,
    isDarkMode: MutableState<Boolean>,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope,
    startDestination: String
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            AuthScreen(isDarkMode, snackbarHostState, scope, navController)
        }
        composable("register") {
            RegisScreen(
                navController = navController,
                isDarkMode = isDarkMode,
                snackbarHostState = snackbarHostState,
                scope = scope
            )
        }
        // Tu HomeScreen como pantalla principal
        composable("main") {
            HomeScreen(
                navController = navController,
                isDarkMode = isDarkMode
            )
        }
        composable("detalle/{productoId}") { backStackEntry ->
            val productoId = backStackEntry.arguments?.getString("productoId") ?: ""
            ProductoDetalleScreen(productoId = productoId, navController = navController)
        }
        composable("confirmarPedido/{productoJson}") { backStackEntry ->
            val productoJson = backStackEntry.arguments?.getString("productoJson") ?: ""
            ConfirmacionPedidoScreen(
                productoJson = productoJson,
                navController = navController
            )
        }
        // Puedes añadir aquí más rutas (ejemplo: pedidos, vender, etc.)
    }
}
