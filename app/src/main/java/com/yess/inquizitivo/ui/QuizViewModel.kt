package com.yess.inquizitivo.ui

import androidx.lifecycle.ViewModel
import com.yess.inquizitivo.data.Category
import com.yess.inquizitivo.data.Question
import com.yess.inquizitivo.data.QuestionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// VIEWMODEL / STATE: Estado UI para la pantalla del cuestionario
data class QuizUiState(
    val category: Category? = null,
    val questions: List<Question> = emptyList(),
    val currentIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val score: Int = 0,
    val isQuizFinished: Boolean = false
) {
    val currentQuestion: Question?
        get() = questions.getOrNull(currentIndex)
}

// VIEWMODEL: Gestiona la lógica de negocio y mantiene el Estado expuesto mediante StateFlow
class QuizViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    fun loadCategory(categoryId: Int) {
        val category = QuestionRepository.categories.find { it.id == categoryId }
        val questions = QuestionRepository.getQuestionsByCategory(categoryId)

        _uiState.update {
            QuizUiState(
                category = category,
                questions = questions,
                currentIndex = 0,
                selectedOptionIndex = null,
                score = 0,
                isQuizFinished = false
            )
        }
    }

    fun selectOption(optionIndex: Int) {
        _uiState.update { it.copy(selectedOptionIndex = optionIndex) }
    }

    fun submitAnswerAndNext() {
        val currentState = _uiState.value
        val currentQ = currentState.currentQuestion ?: return
        val selected = currentState.selectedOptionIndex ?: return

        val isCorrect = selected == currentQ.correctAnswer
        val newScore = if (isCorrect) currentState.score + 10 else currentState.score
        val nextIndex = currentState.currentIndex + 1

        if (nextIndex < currentState.questions.size) {
            _uiState.update {
                it.copy(
                    score = newScore,
                    currentIndex = nextIndex,
                    selectedOptionIndex = null
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    score = newScore,
                    isQuizFinished = true
                )
            }
        }
    }

    fun resetQuiz() {
        val currentCategoryId = _uiState.value.category?.id
        if (currentCategoryId != null) {
            loadCategory(currentCategoryId)
        }
    }
}
