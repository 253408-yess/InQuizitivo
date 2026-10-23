package com.yess.inquizitivo.ui.screens.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yess.inquizitivo.data.local.UserPreferencesRepository
import com.yess.inquizitivo.data.model.Category
import com.yess.inquizitivo.data.model.Question
import com.yess.inquizitivo.data.repository.QuestionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class QuizUiState(
    val categoryId: String = "",
    val categoryName: String = "",
    val questions: List<Question> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val score: Int = 0,
    val isQuizFinished: Boolean = false,
    val correctAnswersCount: Int = 0,
    val totalQuestions: Int = 0,
    val isLoading: Boolean = true
) {
    val currentQuestion: Question?
        get() = questions.getOrNull(currentQuestionIndex)

    val accuracyPercentage: Int
        get() = if (totalQuestions > 0) ((correctAnswersCount.toFloat() / totalQuestions) * 100).toInt() else 0
}

class QuizViewModel(
    private val questionRepository: QuestionRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState.asStateFlow()

    fun loadQuestionsForCategory(categoryId: String) {
        viewModelScope.launch {
            val customQuestions = userPreferencesRepository.customQuestionsFlow.first()
            val questions = questionRepository.getQuestionsForCategory(categoryId, customQuestions)
            val category = Category.defaultCategories.find { it.id == categoryId }

            _uiState.update {
                QuizUiState(
                    categoryId = categoryId,
                    categoryName = category?.name ?: categoryId,
                    questions = questions,
                    currentQuestionIndex = 0,
                    selectedOptionIndex = null,
                    score = 0,
                    isQuizFinished = false,
                    correctAnswersCount = 0,
                    totalQuestions = questions.size,
                    isLoading = false
                )
            }
        }
    }

    fun selectOption(index: Int) {
        _uiState.update { it.copy(selectedOptionIndex = index) }
    }

    fun answerCurrentQuestion(selectedIndex: Int) {
        val currentState = _uiState.value
        val currentQ = currentState.currentQuestion ?: return

        val isCorrect = selectedIndex == currentQ.correctAnswerIndex
        val newScore = if (isCorrect) currentState.score + 10 else currentState.score
        val newCorrectCount = if (isCorrect) currentState.correctAnswersCount + 1 else currentState.correctAnswersCount
        val nextIndex = currentState.currentQuestionIndex + 1

        if (nextIndex < currentState.questions.size) {
            _uiState.update {
                it.copy(
                    score = newScore,
                    correctAnswersCount = newCorrectCount,
                    currentQuestionIndex = nextIndex,
                    selectedOptionIndex = null
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    score = newScore,
                    correctAnswersCount = newCorrectCount,
                    isQuizFinished = true
                )
            }
            viewModelScope.launch {
                userPreferencesRepository.saveQuizResult(newScore, currentState.categoryId)
            }
        }
    }

    fun restartQuiz() {
        val categoryId = _uiState.value.categoryId
        if (categoryId.isNotEmpty()) {
            loadQuestionsForCategory(categoryId)
        }
    }
}

class QuizViewModelFactory(
    private val questionRepository: QuestionRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return QuizViewModel(questionRepository, userPreferencesRepository) as T
    }
}
