package com.example.sparkup.ui.theme.screens

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sparkup.model.Producto
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Composable
fun ConfirmacionPedidoScreen(
    productoJson: String,
    navController: NavController
) {
    // Decodifica y deserializa el producto
    val producto = remember(productoJson) {
        Json.decodeFromString<Producto>(Uri.decode(productoJson))
    }

    var enviando by remember { mutableStateOf(false) }
    var exito by remember { mutableStateOf<Boolean?>(null) }

    val user = Firebase.auth.currentUser
    val correoComprador = user?.email ?: "Desconocido"

    if (exito == true) {
        // Pantalla de éxito
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("¡Pedido enviado!", style = MaterialTheme.typography.headlineSmall)
                Spacer(Modifier.height(16.dp))
                Button(onClick = { navController.popBackStack("main", inclusive = false) }) {
                    Text("Volver al inicio")
                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "¿Confirmar compra de:",
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.height(8.dp))
            Text(
                producto.titulo,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(Modifier.height(8.dp))
            Text("Q${producto.precio}", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))
            Text("Vendedor: ${producto.vendedor}")
            Spacer(Modifier.height(28.dp))

            if (enviando) {
                CircularProgressIndicator()
            } else {
                Row {
                    Button(
                        onClick = {
                            enviando = true
                            // Guardar pedido en Firestore
                            val pedido = hashMapOf(
                                "nombreArticulo" to producto.titulo,
                                "correoComprador" to correoComprador,
                                "correoVendedor" to producto.vendedor,
                                "precio" to producto.precio
                            )
                            Firebase.firestore.collection("pedidos").add(pedido)
                                .addOnSuccessListener { exito = true }
                                .addOnFailureListener { exito = false }
                        }
                    ) { Text("Confirmar") }
                    Spacer(Modifier.width(16.dp))
                    OutlinedButton(onClick = { navController.popBackStack() }) {
                        Text("Cancelar")
                    }
                }
                exito?.let {
                    if (it == false) {
                        Spacer(Modifier.height(12.dp))
                        Text("Ocurrió un error al enviar el pedido", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
