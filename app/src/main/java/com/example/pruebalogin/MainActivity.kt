package com.example.pruebalogin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import com.example.pruebalogin.auth.AuthScreen
import com.example.pruebalogin.ui.theme.PruebaLoginTheme
import kotlinx.coroutines.CoroutineScope
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

            PruebaLoginTheme(darkTheme = isDarkMode.value) {
                Surface {
                    AuthScreen(
                        isDarkMode = isDarkMode,
                        snackbarHostState = snackbarHostState,
                        scope = scope
                    )
                }
            }
        }
    }
}
