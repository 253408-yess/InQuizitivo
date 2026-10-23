package com.yess.inquizitivo.data

// MODELO: Representa la entidad de una Pregunta en la capa de Datos
data class Question(
    val id: Int,
    val categoryId: Int,
    val question: String,
    val options: List<String>,
    val correctAnswer: Int
)
