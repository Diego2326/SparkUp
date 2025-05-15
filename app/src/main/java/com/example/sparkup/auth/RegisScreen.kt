package com.example.sparkup.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sparkup.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.navigation.NavHostController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisScreen(
    navController: NavHostController,
    isDarkMode: MutableState<Boolean>,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope

) {
    val nombre = remember { mutableStateOf("") }
    val usuario = remember { mutableStateOf("") }
    val contrasena = remember { mutableStateOf("") }

    val drawerState = rememberDrawerState(DrawerValue.Closed)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("Configuración", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(16.dp))
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

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 72.dp, start = 24.dp, end = 24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logosparkup),
                        contentDescription = "Logo SparkUp",
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .padding(bottom = 50.dp)
                            .heightIn(min = 120.dp, max = 220.dp)
                    )

                    OutlinedTextField(
                        value = nombre.value,
                        onValueChange = { nombre.value = it },
                        label = { Text("Nombre") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedLabelColor = MaterialTheme.colorScheme.tertiary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface
                        )
                    )

                    OutlinedTextField(
                        value = usuario.value,
                        onValueChange = { usuario.value = it },
                        label = { Text("Correo Electronico") },
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
                            .padding(top = 0.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                snackbarHostState.showSnackbar("Registrando como ${nombre.value} con el Email ${usuario.value}")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Text("Registrarse")
                    }



                    Spacer(modifier = Modifier.height(24.dp))

                    val googleButtonRes = if (isDarkMode.value) {
                        R.drawable.googledark
                    } else {
                        R.drawable.googlelight
                    }
                    Image(
                        painter = painterResource(id = googleButtonRes),
                        contentDescription = "Iniciar sesión con Google",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clickable {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Iniciar sesión con Google")
                                }
                            }
                    )



                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Iniciar Sesion",
                        color = MaterialTheme.colorScheme.tertiary,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .clickable {
                                navController.navigate("login") {
                                    popUpTo("register") { inclusive = true }
                                }
                            }

                            .padding(top = 16.dp)
                    )
                }
            }
        }
    }
}
