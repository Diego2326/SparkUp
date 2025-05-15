package com.example.sparkup
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.rememberNavController
import com.example.sparkup.navigation.AppNavGraph
import com.example.sparkup.ui.theme.PruebaLoginTheme

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

            PruebaLoginTheme(darkTheme = isDarkMode.value) {
                Surface {
                    AppNavGraph(
                        navController = navController,
                        isDarkMode = isDarkMode,
                        snackbarHostState = snackbarHostState,
                        scope = scope
                    )
                }
            }
        }
    }
}
