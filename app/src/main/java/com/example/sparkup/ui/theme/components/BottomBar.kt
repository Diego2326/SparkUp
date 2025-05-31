package com.example.sparkup.ui.theme.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BottomBar(
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    onLogout: () -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface, // <-- Cambia aquí
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            selected = selectedIndex == 0,
            onClick = { onItemSelected(0) },
            icon = { Icon(Icons.Filled.Home, contentDescription = "Inicio") },
            label = {
                Text(
                    text = "Inicio",
                    fontSize = 12.sp,
                    fontWeight = if (selectedIndex == 0) FontWeight.Bold else FontWeight.Normal
                )
            },
            alwaysShowLabel = true
        )
        NavigationBarItem(
            selected = selectedIndex == 1,
            onClick = { onItemSelected(1) },
            icon = { Icon(Icons.Outlined.ShoppingCart, contentDescription = "Pedidos") },
            label = {
                Text(
                    text = "Pedidos",
                    fontSize = 12.sp,
                    fontWeight = if (selectedIndex == 1) FontWeight.Bold else FontWeight.Normal
                )
            },
            alwaysShowLabel = true
        )
        NavigationBarItem(
            selected = selectedIndex == 2,
            onClick = { onItemSelected(2) },
            icon = { Icon(Icons.Filled.AddCircle, contentDescription = "Vender") },
            label = {
                Text(
                    text = "Vender",
                    fontSize = 12.sp,
                    fontWeight = if (selectedIndex == 2) FontWeight.Bold else FontWeight.Normal
                )
            },
            alwaysShowLabel = true
        )
        NavigationBarItem(
            selected = selectedIndex == 3,
            onClick = { onLogout() },
            icon = { Icon(Icons.Outlined.ExitToApp, contentDescription = "Log Out") },
            label = {
                Text(
                    text = "Log Out",
                    fontSize = 12.sp,
                    fontWeight = if (selectedIndex == 3) FontWeight.Bold else FontWeight.Normal
                )
            },
            alwaysShowLabel = true
        )
    }
}
