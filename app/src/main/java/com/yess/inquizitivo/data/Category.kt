package com.yess.inquizitivo.data

import androidx.compose.ui.graphics.Color

// MODELO: Representa la entidad de una Categoría en la capa de Datos
data class Category(
    val id: Int,
    val name: String,
    val color: Color
)
