package com.example.sparkup.model

import kotlinx.serialization.Serializable

@Serializable
data class Producto(
    val id: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0,
    val imagenURL: String = "",
    val vendedor: String = ""
)
