package com.yess.inquizitivo.data.model

data class Question(
    val id: String,
    val categoryId: String,
    val questionText: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String = ""
)
