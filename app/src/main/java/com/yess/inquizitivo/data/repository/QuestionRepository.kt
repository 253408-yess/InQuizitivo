package com.yess.inquizitivo.data.repository

import com.yess.inquizitivo.data.model.Category
import com.yess.inquizitivo.data.model.CustomQuestion
import com.yess.inquizitivo.data.model.Question
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class QuestionRepository {

    // Lista de las 5 categorías de la aplicación
    val categories: List<Category> = Category.defaultCategories

    // Lista reactiva para almacenar preguntas personalizadas agregadas en memoria/StateFlow
    private val _customQuestions = MutableStateFlow<List<CustomQuestion>>(emptyList())
    val customQuestions: StateFlow<List<CustomQuestion>> = _customQuestions.asStateFlow()

    // Banco de preguntas semilla precargadas (2 por categoría)
    private val initialQuestions = listOf(
        // 1. Programación (categoryId = "programming" o "1")
        Question(
            id = "1",
            categoryId = "programming",
            questionText = "¿Qué paradigma de programación utiliza Kotlin principalmente?",
            options = listOf("Solo Estructurado", "Orientado a Objetos y Funcional", "Solo Lógico", "Ensamblador"),
            correctAnswerIndex = 1,
            explanation = "Kotlin combina programación orientada a objetos con características de programación funcional."
        ),
        Question(
            id = "2",
            categoryId = "programming",
            questionText = "¿Qué palabra clave se usa para declarar una variable inmutable en Kotlin?",
            options = listOf("var", "val", "const", "let"),
            correctAnswerIndex = 1,
            explanation = "La palabra clave 'val' declara variables de solo lectura (inmutables)."
        ),

        // 2. Base de datos (categoryId = "database" o "2")
        Question(
            id = "3",
            categoryId = "database",
            questionText = "¿Qué comando SQL se utiliza para recuperar datos de una tabla?",
            options = listOf("UPDATE", "INSERT", "SELECT", "DELETE"),
            correctAnswerIndex = 2,
            explanation = "El comando SELECT se utiliza para realizar consultas y extraer datos de tablas."
        ),
        Question(
            id = "4",
            categoryId = "database",
            questionText = "¿Qué tipo de clave identifica de forma única una fila en una tabla?",
            options = listOf("Clave Foránea", "Clave Primaria", "Clave Candidata", "Clave Compuesta"),
            correctAnswerIndex = 1,
            explanation = "La Clave Primaria (Primary Key) identifica de forma única cada fila."
        ),

        // 3. Redes (categoryId = "networking" o "3")
        Question(
            id = "5",
            categoryId = "networking",
            questionText = "¿En qué capa del modelo OSI opera el protocolo IP?",
            options = listOf("Capa de Enlace", "Capa de Red", "Capa de Transporte", "Capa de Aplicación"),
            correctAnswerIndex = 1,
            explanation = "IP es el protocolo fundamental de la Capa 3 (Red) del modelo OSI."
        ),
        Question(
            id = "6",
            categoryId = "networking",
            questionText = "¿Cuál es el puerto predeterminado para el protocolo HTTPS?",
            options = listOf("80", "21", "443", "8080"),
            correctAnswerIndex = 2,
            explanation = "HTTPS utiliza el puerto TCP 443 para comunicaciones cifradas."
        ),

        // 4. Android (categoryId = "android" o "4")
        Question(
            id = "7",
            categoryId = "android",
            questionText = "¿Qué toolkit moderno de Android se usa para construir UIs declarativas?",
            options = listOf("XML Layouts", "Jetpack Compose", "Flutter", "React Native"),
            correctAnswerIndex = 1,
            explanation = "Jetpack Compose es el toolkit moderno para interfaces de usuario declarativas en Android."
        ),
        Question(
            id = "8",
            categoryId = "android",
            questionText = "¿Cuál es el componente que gestiona el ciclo de vida de una pantalla en Android?",
            options = listOf("Service", "BroadcastReceiver", "Activity", "ContentProvider"),
            correctAnswerIndex = 2,
            explanation = "Una Activity representa una pantalla individual con su ciclo de vida."
        ),

        // 5. Cultura general (categoryId = "general" o "5")
        Question(
            id = "9",
            categoryId = "general",
            questionText = "¿Cuál es el océano más grande del mundo?",
            options = listOf("Atlántico", "Índico", "Pacífico", "Ártico"),
            correctAnswerIndex = 2,
            explanation = "El Océano Pacífico es la masa de agua más grande del planeta."
        ),
        Question(
            id = "10",
            categoryId = "general",
            questionText = "¿En qué año llegó el ser humano a la Luna?",
            options = listOf("1965", "1969", "1972", "1980"),
            correctAnswerIndex = 1,
            explanation = "Neil Armstrong pisó la superficie de la Luna en 1969 durante la misión Apolo 11."
        )
    )

    /**
     * Normaliza el ID de categoría a la cadena utilizada en las preguntas.
     */
    private fun normalizeCategoryId(categoryId: String): String {
        return when (categoryId.lowercase()) {
            "1", "programming", "programación", "programacion" -> "programming"
            "2", "database", "base de datos" -> "database"
            "3", "networking", "redes" -> "networking"
            "4", "android" -> "android"
            "5", "general", "cultura general" -> "general"
            else -> categoryId
        }
    }

    /**
     * Obtiene las preguntas filtrando por categoría (Acepta String como "programming" o "1").
     */
    fun getQuestionsForCategory(categoryId: String, customQuestions: List<Question> = emptyList()): List<Question> {
        val targetId = normalizeCategoryId(categoryId)
        val seed = initialQuestions.filter { it.categoryId == targetId || it.categoryId == categoryId }
        val custom = customQuestions.filter { it.categoryId == targetId || it.categoryId == categoryId }
        val memoryCustom = _customQuestions.value
            .filter { normalizeCategoryId(it.categoryId) == targetId }
            .map { customQ ->
                Question(
                    id = customQ.id,
                    categoryId = customQ.categoryId,
                    questionText = customQ.questionText,
                    options = customQ.options,
                    correctAnswerIndex = customQ.correctAnswerIndex,
                    explanation = customQ.explanation
                )
            }
        return seed + custom + memoryCustom
    }

    /**
     * Sobrecarga para obtener preguntas por ID entero o cadena.
     */
    fun getQuestionsByCategory(categoryId: Int): List<Question> {
        return getQuestionsForCategory(categoryId.toString())
    }

    fun getQuestionsByCategory(categoryId: String): List<Question> {
        return getQuestionsForCategory(categoryId)
    }

    /**
     * Agrega una pregunta personalizada en la lista reactiva en memoria.
     */
    fun addCustomQuestion(customQuestion: CustomQuestion) {
        _customQuestions.value = _customQuestions.value + customQuestion
    }
}
