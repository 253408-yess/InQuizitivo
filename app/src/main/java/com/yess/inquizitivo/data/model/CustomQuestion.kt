package com.yess.inquizitivo.data.model

data class CustomQuestion(
    val id: String = "",
    val categoryId: String,
    val questionText: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String = ""
)
