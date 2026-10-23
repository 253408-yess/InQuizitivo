package com.yess.inquizitivo.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Storage
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class Category(
    val id: String,
    val name: String,
    val description: String = "",
    val color: Color,
    val icon: ImageVector
) {
    companion object {
        val defaultCategories = listOf(
            Category(
                id = "programming",
                name = "Programación",
                description = "Conceptos de desarrollo y lenguajes",
                color = Color(0xFF2196F3), // Azul
                icon = Icons.Default.Code
            ),
            Category(
                id = "database",
                name = "Base de Datos",
                description = "SQL, NoSQL y modelado de datos",
                color = Color(0xFF009688), // Verde Teal
                icon = Icons.Default.Storage
            ),
            Category(
                id = "networking",
                name = "Redes",
                description = "Protocolos, arquitectura y conectividad",
                color = Color(0xFFFF9800), // Naranja
                icon = Icons.Default.Public
            ),
            Category(
                id = "android",
                name = "Android",
                description = "Kotlin, Jetpack Compose y SDK",
                color = Color(0xFF4CAF50), // Verde Android
                icon = Icons.Default.Android
            ),
            Category(
                id = "general",
                name = "Cultura General",
                description = "Preguntas de conocimientos generales",
                color = Color(0xFF9C27B0), // Morado
                icon = Icons.Default.Lightbulb
            )
        )
    }
}
