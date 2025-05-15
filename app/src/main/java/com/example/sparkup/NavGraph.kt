package com.example.sparkup.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.sparkup.auth.AuthScreen
import com.example.sparkup.auth.RegisScreen
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.MutableState
import kotlinx.coroutines.CoroutineScope

@Composable
fun AppNavGraph(
    navController: NavHostController,
    isDarkMode: MutableState<Boolean>,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
) {
    NavHost(navController = navController, startDestination = "login") {
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

    }
}
