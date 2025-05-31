package com.example.sparkup.ui.theme.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sparkup.model.Producto
import com.example.sparkup.ui.theme.components.ProductCard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    isDarkMode: MutableState<Boolean>, // <-- ¡NO inicializar aquí!
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    val backgroundColor = MaterialTheme.colorScheme.background
    var productos by remember { mutableStateOf<List<Producto>>(emptyList()) }
    var isRefreshing by remember { mutableStateOf(false) }
    var mostrarSoloMios by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val refreshState = rememberSwipeRefreshState(isRefreshing)
    val userEmail = FirebaseAuth.getInstance().currentUser?.email ?: ""

    // Drawer State
    val drawerState = rememberDrawerState(DrawerValue.Closed)

    suspend fun cargarProductos() {
        isRefreshing = true
        try {
            val snapshot = Firebase.firestore.collection("producto").get().await()
            productos = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Producto::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            scope.launch {
                snackbarHostState.showSnackbar("Error al cargar productos.")
            }
        }
        isRefreshing = false
    }

    // Cargar productos al inicio o cuando se actualiza el filtro
    LaunchedEffect(mostrarSoloMios) {
        cargarProductos()
    }

    val productosMostrados = if (mostrarSoloMios) {
        productos.filter { it.vendedor == userEmail }
    } else {
        productos
    }

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
            containerColor = backgroundColor,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Image(
                            painter = painterResource(id = com.example.sparkup.R.drawable.logosparkup),
                            contentDescription = "Logo SparkUp",
                            modifier = Modifier
                                .height(46.dp)
                                .fillMaxWidth(0.5f)
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch { drawerState.open() }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menú",
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { mostrarSoloMios = !mostrarSoloMios },
                    containerColor = if (mostrarSoloMios) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
                    contentColor = Color.White
                ) {
                    if (mostrarSoloMios) {
                        Icon(Icons.Filled.Person, contentDescription = "Ver solo mis productos")
                    } else {
                        Icon(Icons.Outlined.ShoppingCart, contentDescription = "Filtrar mis productos")
                    }
                }
            },
        ) { innerPadding ->
            SwipeRefresh(
                state = refreshState,
                onRefresh = {
                    scope.launch { cargarProductos() }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(productosMostrados) { producto ->
                        ProductCard(producto = producto) {
                            navController.navigate("detalle/${producto.id}")
                        }
                    }
                }
            }
        }
    }
}
