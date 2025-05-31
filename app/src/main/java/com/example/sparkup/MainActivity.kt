package com.example.sparkup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.rememberNavController
import com.example.sparkup.navigation.AppNavGraph
import com.example.sparkup.ui.theme.PruebaLoginTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val defaultDark = runCatching { isSystemInDarkTheme() }.getOrDefault(true)
            val isDarkMode = rememberSaveable { mutableStateOf(defaultDark) }
            val snackbarHostState = remember { SnackbarHostState() }
            val scope = rememberCoroutineScope()
            val navController = rememberNavController()

            var startDestination by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(Unit) {
                delay(500)
                val user = Firebase.auth.currentUser
                startDestination = if (user != null) "main" else "login"
            }

            if (startDestination != null) {
                PruebaLoginTheme(darkTheme = isDarkMode.value) {
                    Surface {
                        AppNavGraph(
                            navController = navController,
                            isDarkMode = isDarkMode, // <- Pásalo!
                            snackbarHostState = snackbarHostState,
                            scope = scope,
                            startDestination = startDestination!!
                        )
                    }
                }
            }
        }
    }
}
