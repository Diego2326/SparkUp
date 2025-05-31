package com.example.sparkup.model

data class Pedido(
    val nombreArticulo: String = "",
    val correoComprador: String = "",
    val correoVendedor: String = "",
    val precio: Double = 0.0
)
