package com.yess.inquizitivo.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.yess.inquizitivo.data.model.Question
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

data class UserStats(
    val bestScore: Int = 0,
    val quizzesCompleted: Int = 0,
    val categoryPlayCounts: Map<String, Int> = emptyMap()
) {
    val mostPlayedCategory: String
        get() {
            if (categoryPlayCounts.isEmpty()) return "Ninguna"
            val maxEntry = categoryPlayCounts.maxByOrNull { it.value } ?: return "Ninguna"
            if (maxEntry.value == 0) return "Ninguna"
            return when (maxEntry.key) {
                "programming" -> "Programación"
                "database" -> "Base de Datos"
                "networking" -> "Redes"
                "android" -> "Android"
                "general" -> "Cultura General"
                else -> maxEntry.key
            }
        }
}

class UserPreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val BEST_SCORE = intPreferencesKey("best_score")
        val QUIZZES_COMPLETED = intPreferencesKey("quizzes_completed")
        val CUSTOM_QUESTIONS = stringPreferencesKey("custom_questions")
    }

    val userStatsFlow: Flow<UserStats> = context.dataStore.data.map { preferences ->
        val bestScore = preferences[PreferencesKeys.BEST_SCORE] ?: 0
        val quizzesCompleted = preferences[PreferencesKeys.QUIZZES_COMPLETED] ?: 0

        val playCounts = mutableMapOf<String, Int>()
        val categories = listOf("programming", "database", "networking", "android", "general")
        for (cat in categories) {
            val key = intPreferencesKey("count_$cat")
            playCounts[cat] = preferences[key] ?: 0
        }

        UserStats(
            bestScore = bestScore,
            quizzesCompleted = quizzesCompleted,
            categoryPlayCounts = playCounts
        )
    }

    val customQuestionsFlow: Flow<List<Question>> = context.dataStore.data.map { preferences ->
        val rawJson = preferences[PreferencesKeys.CUSTOM_QUESTIONS] ?: ""
        parseCustomQuestions(rawJson)
    }

    suspend fun saveQuizResult(score: Int, categoryId: String) {
        context.dataStore.edit { preferences ->
            val currentBest = preferences[PreferencesKeys.BEST_SCORE] ?: 0
            if (score > currentBest) {
                preferences[PreferencesKeys.BEST_SCORE] = score
            }

            val currentCompleted = preferences[PreferencesKeys.QUIZZES_COMPLETED] ?: 0
            preferences[PreferencesKeys.QUIZZES_COMPLETED] = currentCompleted + 1

            val categoryKey = intPreferencesKey("count_$categoryId")
            val currentCount = preferences[categoryKey] ?: 0
            preferences[categoryKey] = currentCount + 1
        }
    }

    suspend fun addCustomQuestion(question: Question) {
        context.dataStore.edit { preferences ->
            val currentRaw = preferences[PreferencesKeys.CUSTOM_QUESTIONS] ?: ""
            val currentList = parseCustomQuestions(currentRaw).toMutableList()
            currentList.add(question)
            preferences[PreferencesKeys.CUSTOM_QUESTIONS] = serializeCustomQuestions(currentList)
        }
    }

    private fun parseCustomQuestions(raw: String): List<Question> {
        if (raw.isBlank()) return emptyList()
        return try {
            raw.split(";;;").mapNotNull { item ->
                val parts = item.split("|||")
                if (parts.size >= 6) {
                    val id = parts[0]
                    val categoryId = parts[1]
                    val questionText = parts[2]
                    val options = parts[3].split("~~~")
                    val correctAnswerIndex = parts[4].toIntOrNull() ?: 0
                    val explanation = parts[5]
                    Question(id, categoryId, questionText, options, correctAnswerIndex, explanation)
                } else null
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun serializeCustomQuestions(questions: List<Question>): String {
        return questions.joinToString(";;;") { q ->
            val optionsStr = q.options.joinToString("~~~")
            "${q.id}|||${q.categoryId}|||${q.questionText}|||${optionsStr}|||${q.correctAnswerIndex}|||${q.explanation}"
        }
    }
}
