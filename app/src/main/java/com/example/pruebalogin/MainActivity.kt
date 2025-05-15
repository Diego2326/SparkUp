package com.example.pruebalogin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pruebalogin.ui.theme.PruebaLoginTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkMode = remember { mutableStateOf(false) }

            PruebaLoginTheme(darkTheme = isDarkMode.value) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    LoginScreenWithDrawer(isDarkMode)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreenWithDrawer(isDarkMode: MutableState<Boolean>) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text(
                    "Configuración",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text("Modo oscuro", modifier = Modifier.weight(1f))
                    Switch(
                        checked = isDarkMode.value,
                        onCheckedChange = { isDarkMode.value = it }
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {},
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                // Botón hamburguesa flotante arriba a la izquierda
                IconButton(
                    onClick = { scope.launch { drawerState.open() } },
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.TopStart)
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menú",
                        tint = MaterialTheme.colorScheme.tertiary
                    )
                }

                LoginForm(snackbarHostState, Modifier.padding(top = 72.dp))
            }
        }
    }
}


@Composable
fun LoginForm(snackbarHostState: SnackbarHostState, modifier: Modifier = Modifier) {
    val usuario = remember { mutableStateOf("") }
    val contrasena = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .then(modifier),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Placeholder del logo
        Image(
            painter = painterResource(id = R.drawable.logosparkup),
            contentDescription = "Logo SparkUp",
            modifier = Modifier
                .fillMaxWidth(0.8f) // Ocupa el 80% del ancho
                .padding(bottom = 40.dp)
                .heightIn(min = 120.dp, max = 220.dp) // Control vertical adaptable
                .align(Alignment.CenterHorizontally)
        )


        OutlinedTextField(
            value = usuario.value,
            onValueChange = { usuario.value = it },
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.tertiary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface
            )
        )



        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = contrasena.value,
            onValueChange = { contrasena.value = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.tertiary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface
            )
        )



        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                scope.launch {
                    snackbarHostState.showSnackbar("Iniciando sesión como ${usuario.value}")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary
            )
        ) {
            Text("Iniciar Sesión")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "¿Olvidaste la Contraseña?",
            color = MaterialTheme.colorScheme.tertiary,
            fontSize = 14.sp,
            modifier = Modifier
                .clickable {
                    scope.launch {
                        snackbarHostState.showSnackbar("Función no disponible todavía")
                    }
                }
                .padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = {
                scope.launch {
                    snackbarHostState.showSnackbar("Iniciar sesión con Google")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.tertiary
            )
        ) {
            Text("Iniciar con Google")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = {
                scope.launch {
                    snackbarHostState.showSnackbar("Redirigiendo a registro...")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.tertiary
            )
        ) {
            Text("Registrarse")
        }
    }
}

