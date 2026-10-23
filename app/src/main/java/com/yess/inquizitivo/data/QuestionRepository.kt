package com.yess.inquizitivo.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color

// MODELO / REPOSITORIO: Fuente de verdad para los datos (Singleton u objeto repositorio)
object QuestionRepository {

    val categories = listOf(
        Category(1, "Programación", Color(0xFF2196F3)),
        Category(2, "Base de Datos", Color(0xFF009688)),
        Category(3, "Redes", Color(0xFFFF9800)),
        Category(4, "Android", Color(0xFF4CAF50)),
        Category(5, "Cultura General", Color(0xFF9C27B0))
    )

    private val questions = mutableStateListOf(
        // Programación (id: 1)
        Question(1, 1, "¿Qué palabra clave se usa para declarar variables inmutables en Kotlin?", listOf("var", "val", "const", "let"), 1),
        Question(2, 1, "¿Qué paradigma de programación combina Kotlin?", listOf("Solo Imperativo", "Orientado a Objetos y Funcional", "Solo Lógico", "Ensamblador"), 1),

        // Base de Datos (id: 2)
        Question(3, 2, "¿Qué comando SQL se utiliza para consultar registros?", listOf("UPDATE", "INSERT", "SELECT", "DELETE"), 2),
        Question(4, 2, "¿Qué clave identifica de forma única cada fila en una tabla?", listOf("Clave Foránea", "Clave Primaria", "Clave Índice", "Clave Vista"), 1),

        // Redes (id: 3)
        Question(5, 3, "¿En qué capa del modelo OSI opera el protocolo IP?", listOf("Capa Física", "Capa de Red", "Capa de Transporte", "Capa de Aplicación"), 1),
        Question(6, 3, "¿Cuál es el puerto predeterminado para el protocolo HTTPS?", listOf("80", "21", "443", "8080"), 2),

        // Android (id: 4)
        Question(7, 4, "¿Qué toolkit moderno de Android se usa para UIs declarativas?", listOf("XML Layouts", "Jetpack Compose", "Flutter", "React Native"), 1),
        Question(8, 4, "¿Qué componente representa una pantalla individual en Android?", listOf("Service", "Activity", "BroadcastReceiver", "ContentProvider"), 1),

        // Cultura General (id: 5)
        Question(9, 5, "¿Cuál es el océano más grande del mundo?", listOf("Atlántico", "Índico", "Pacífico", "Ártico"), 2),
        Question(10, 5, "¿En qué año llegó el ser humano a la Luna?", listOf("1965", "1969", "1972", "1980"), 1)
    )

    fun getQuestionsByCategory(categoryId: Int): List<Question> {
        return questions.filter { it.categoryId == categoryId }
    }

    fun addQuestion(question: Question) {
        questions.add(question)
    }

    fun getTotalQuestionsCount(): Int = questions.size
}
