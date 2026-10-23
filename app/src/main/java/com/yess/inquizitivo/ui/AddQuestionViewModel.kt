package com.yess.inquizitivo.ui

import androidx.lifecycle.ViewModel
import com.yess.inquizitivo.data.Category
import com.yess.inquizitivo.data.Question
import com.yess.inquizitivo.data.QuestionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// VIEWMODEL / STATE: Estado del formulario de agregar pregunta
data class AddQuestionUiState(
    val selectedCategoryId: Int = 1,
    val questionText: String = "",
    val option1: String = "",
    val option2: String = "",
    val option3: String = "",
    val option4: String = "",
    val correctAnswerIndex: Int = 0,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

// VIEWMODEL: Controla la lógica de validación y registro de nuevas preguntas en el repositorio
class AddQuestionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AddQuestionUiState())
    val uiState: StateFlow<AddQuestionUiState> = _uiState.asStateFlow()

    fun getCategories(): List<Category> = QuestionRepository.categories

    fun updateCategoryId(categoryId: Int) {
        _uiState.update { it.copy(selectedCategoryId = categoryId, errorMessage = null) }
    }

    fun updateQuestionText(text: String) {
        _uiState.update { it.copy(questionText = text, errorMessage = null) }
    }

    fun updateOption1(text: String) {
        _uiState.update { it.copy(option1 = text, errorMessage = null) }
    }

    fun updateOption2(text: String) {
        _uiState.update { it.copy(option2 = text, errorMessage = null) }
    }

    fun updateOption3(text: String) {
        _uiState.update { it.copy(option3 = text, errorMessage = null) }
    }

    fun updateOption4(text: String) {
        _uiState.update { it.copy(option4 = text, errorMessage = null) }
    }

    fun updateCorrectAnswerIndex(index: Int) {
        _uiState.update { it.copy(correctAnswerIndex = index, errorMessage = null) }
    }

    fun saveQuestion() {
        val state = _uiState.value
        if (state.questionText.isBlank() ||
            state.option1.isBlank() ||
            state.option2.isBlank() ||
            state.option3.isBlank() ||
            state.option4.isBlank()
        ) {
            _uiState.update { it.copy(errorMessage = "Por favor completa todos los campos.") }
            return
        }

        val newQuestion = Question(
            id = QuestionRepository.getTotalQuestionsCount() + 1,
            categoryId = state.selectedCategoryId,
            question = state.questionText.trim(),
            options = listOf(
                state.option1.trim(),
                state.option2.trim(),
                state.option3.trim(),
                state.option4.trim()
            ),
            correctAnswer = state.correctAnswerIndex
        )

        QuestionRepository.addQuestion(newQuestion)
        _uiState.update { it.copy(isSuccess = true, errorMessage = null) }
    }

    fun resetState() {
        _uiState.update { AddQuestionUiState() }
    }
}
