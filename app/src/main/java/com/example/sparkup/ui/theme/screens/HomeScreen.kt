package com.example.sparkup.ui.theme.screens

import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.sparkup.ui.theme.components.BottomBar

@Composable
fun HomeScreen(
    navController: NavController,
    isDarkMode: MutableState<Boolean>
) {
    var selectedIndex by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            BottomBar(
                selectedIndex = selectedIndex,
                onItemSelected = { index -> selectedIndex = index },
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("main") { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            when (selectedIndex) {
                0 -> MainScreen(navController, isDarkMode = isDarkMode) // <-- Pásalo aquí
                1 -> PedidosScreen(navController)
                2 -> VenderScreen(
                    onPublicado = {
                        navController.popBackStack("main", inclusive = false)
                    }
                )
            }
        }
    }
}

