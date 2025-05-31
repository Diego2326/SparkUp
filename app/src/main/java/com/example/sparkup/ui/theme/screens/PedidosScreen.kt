package com.example.sparkup.ui.theme.screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.coroutines.launch

data class Pedido(
    val id: String = "",
    val nombreArticulo: String = "",
    val correoComprador: String = "",
    val correoVendedor: String = "",
    val precio: Double = 0.0,
    val confirmado: Boolean = false
)

@Composable
fun PedidosScreen(
    navController: NavController
) {
    val user = Firebase.auth.currentUser
    val email = user?.email ?: ""

    var tabIndex by remember { mutableStateOf(0) }
    var pedidosComprador by remember { mutableStateOf<List<Pedido>>(emptyList()) }
    var pedidosVendedor by remember { mutableStateOf<List<Pedido>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val refreshState = rememberSwipeRefreshState(isRefreshing)
    val isDark = isSystemInDarkTheme()
    val tabTextColor = if (isDark) Color.White else Color.Black

    fun cargarPedidos() {
        loading = true
        isRefreshing = true
        val db = Firebase.firestore
        // Como Comprador
        db.collection("pedidos")
            .whereEqualTo("correoComprador", email)
            .get().addOnSuccessListener { result ->
                pedidosComprador = result.documents.mapNotNull { doc ->
                    doc.toObject(Pedido::class.java)?.copy(id = doc.id)
                }
            }
        // Como Vendedor
        db.collection("pedidos")
            .whereEqualTo("correoVendedor", email)
            .get().addOnSuccessListener { result ->
                pedidosVendedor = result.documents.mapNotNull { doc ->
                    doc.toObject(Pedido::class.java)?.copy(id = doc.id)
                }
            }
            .addOnCompleteListener {
                loading = false
                isRefreshing = false
            }
    }

    // Cargar pedidos al iniciar y al refrescar
    LaunchedEffect(email) { cargarPedidos() }

    Column(Modifier.fillMaxSize().padding(18.dp)) {
        TabRow(selectedTabIndex = tabIndex) {
            Tab(
                selected = tabIndex == 0,
                onClick = { tabIndex = 0 },
                text = {
                    Text(
                        "Como comprador",
                        color = tabTextColor
                    )
                }
            )
            Tab(
                selected = tabIndex == 1,
                onClick = { tabIndex = 1 },
                text = {
                    Text(
                        "Como vendedor",
                        color = tabTextColor
                    )
                }
            )
        }
        Spacer(Modifier.height(16.dp))
        SwipeRefresh(
            state = refreshState,
            onRefresh = { scope.launch { cargarPedidos() } },
            modifier = Modifier.fillMaxSize()
        ) {
            if (loading && !isRefreshing) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                when (tabIndex) {
                    0 -> PedidoList(
                        pedidosComprador,
                        "No tienes pedidos como comprador.",
                        esVendedor = false,
                        onConfirmar = {},
                        onCancelar = { pedidoId -> scope.launch {
                            val db = Firebase.firestore
                            db.collection("pedidos").document(pedidoId).delete().addOnSuccessListener {
                                cargarPedidos()
                            }
                        }}
                    )
                    1 -> PedidoList(
                        pedidosVendedor,
                        "No tienes pedidos como vendedor.",
                        esVendedor = true,
                        onConfirmar = { pedidoId -> scope.launch {
                            val db = Firebase.firestore
                            db.collection("pedidos").document(pedidoId)
                                .update("confirmado", true)
                                .addOnSuccessListener { cargarPedidos() }
                        }},
                        onCancelar = {}
                    )
                }
            }
        }
    }
}

@Composable
fun PedidoList(
    pedidos: List<Pedido>,
    emptyMsg: String,
    esVendedor: Boolean,
    onConfirmar: (String) -> Unit,
    onCancelar: (String) -> Unit
) {
    if (pedidos.isEmpty()) {
        Text(emptyMsg)
    } else {
        // SCROLL EN LA LISTA DE PEDIDOS
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            pedidos.forEach { pedido ->
                Card(Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Artículo: ${pedido.nombreArticulo}")
                        Text("Precio: Q${pedido.precio}")
                        Text("Comprador: ${pedido.correoComprador}")
                        Text("Vendedor: ${pedido.correoVendedor}")
                        if (esVendedor) {
                            if (pedido.confirmado) {
                                Text("Pedido confirmado ✅", color = Color.Green)
                            } else {
                                Button(
                                    onClick = { onConfirmar(pedido.id) },
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Text("Confirmar pedido")
                                }
                            }
                        } else {
                            if (pedido.confirmado) {
                                Text("Pedido confirmado ✅", color = Color.Green)
                            } else {
                                Button(
                                    onClick = { onCancelar(pedido.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Text("Cancelar pedido", color = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
