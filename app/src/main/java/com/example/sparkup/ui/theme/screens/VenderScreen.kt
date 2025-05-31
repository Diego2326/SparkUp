package com.example.sparkup.ui.theme.screens

import android.content.Context
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.sparkup.model.Producto
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import java.io.File
import androidx.core.content.FileProvider
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.statement.*
import io.ktor.http.*
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VenderScreen(
    onPublicado: () -> Unit = {}
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Permisos para diferentes versiones de Android
    val readMediaImagesPermissionState = rememberPermissionState(
        android.Manifest.permission.READ_MEDIA_IMAGES
    )
    val readExternalStoragePermissionState = rememberPermissionState(
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    )

    // Estados de campos
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var uploading by remember { mutableStateOf(false) }
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }

    // Usuario actual de Firebase
    val user = Firebase.auth.currentUser
    val vendedor = user?.email ?: "Desconocido"

    // Lanzador para galería
    val pickPhotoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let { imageUri = it }
    }

    // Lanzador para cámara
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            imageUri = cameraImageUri
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .fillMaxWidth()
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Publicar nuevo producto", style = MaterialTheme.typography.titleLarge)

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            OutlinedTextField(
                value = precio,
                onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) precio = it },
                label = { Text("Precio") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Galería
                Button(
                    onClick = {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            // Android 13+
                            if (readMediaImagesPermissionState.status.isGranted) {
                                pickPhotoLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            } else {
                                readMediaImagesPermissionState.launchPermissionRequest()
                            }
                        } else {
                            // Android 12 o menor
                            if (readExternalStoragePermissionState.status.isGranted || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                                pickPhotoLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            } else {
                                readExternalStoragePermissionState.launchPermissionRequest()
                            }
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Galería")
                }
                Spacer(Modifier.width(8.dp))
                // Cámara
                Button(
                    onClick = {
                        val photoFile = File.createTempFile(
                            "IMG_", ".jpg", context.cacheDir
                        )
                        cameraImageUri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            photoFile
                        )
                        takePictureLauncher.launch(cameraImageUri)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cámara")
                }
            }

            // Mostrar la imagen seleccionada
            imageUri?.let {
                Image(
                    painter = rememberAsyncImagePainter(it),
                    contentDescription = "Imagen seleccionada",
                    modifier = Modifier
                        .height(180.dp)
                        .fillMaxWidth()
                )
            }

            OutlinedTextField(
                value = vendedor,
                onValueChange = {},
                label = { Text("Vendedor") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedLabelColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            Spacer(Modifier.height(16.dp))

            Button(
                enabled = !uploading,
                onClick = {
                    if (nombre.isBlank() || descripcion.isBlank() || precio.isBlank() || imageUri == null) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Completa todos los campos y agrega una foto.")
                        }
                        return@Button
                    }
                    uploading = true
                    scope.launch {
                        val imgurUrl = uploadImageToImgur(context, imageUri!!)
                        if (imgurUrl == null) {
                            snackbarHostState.showSnackbar("Error al subir imagen a Imgur.")
                            uploading = false
                            return@launch
                        }
                        val producto = Producto(
                            titulo = nombre,
                            descripcion = descripcion,
                            precio = precio.toDoubleOrNull() ?: 0.0,
                            imagenURL = imgurUrl,
                            vendedor = vendedor
                        )
                        Firebase.firestore.collection("producto")
                            .add(producto)
                            .addOnSuccessListener {
                                scope.launch {
                                    snackbarHostState.showSnackbar("Producto publicado exitosamente")
                                }
                                nombre = ""
                                descripcion = ""
                                precio = ""
                                imageUri = null
                                uploading = false
                                onPublicado()
                            }
                            .addOnFailureListener { e ->
                                scope.launch {
                                    snackbarHostState.showSnackbar("Error al publicar: ${e.message}")
                                }
                                uploading = false
                            }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(percent = 50)
            ) {
                Text(if (uploading) "Publicando..." else "Publicar")
            }
        }
    }
}

suspend fun uploadImageToImgur(context: Context, imageUri: Uri): String? {
    val client = HttpClient(OkHttp)
    val contentResolver = context.contentResolver
    val inputStream = contentResolver.openInputStream(imageUri) ?: return null
    val bytes = inputStream.readBytes()
    inputStream.close()

    val clientId = "051aa459f066b21" // <-- Cambia por tu Client-ID de Imgur
    return try {
        val response: HttpResponse = client.submitFormWithBinaryData(
            url = "https://api.imgur.com/3/image",
            formData = formData {
                append("image", bytes, Headers.build {
                    append(HttpHeaders.ContentType, "image/jpeg")
                    append(HttpHeaders.ContentDisposition, "filename=\"imagen.jpg\"")
                })
            }
        ) {
            headers {
                append("Authorization", "Client-ID $clientId")
            }
        }
        val body = response.bodyAsText()
        // Extraer URL de la respuesta JSON
        val url = "\"link\":\"(.*?)\"".toRegex().find(body)?.groupValues?.get(1)
        url?.replace("\\/", "/")
    } catch (e: Exception) {
        null
    } finally {
        client.close()
    }
}
