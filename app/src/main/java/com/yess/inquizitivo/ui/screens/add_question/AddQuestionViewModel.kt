package com.yess.inquizitivo.ui.screens.add_question

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yess.inquizitivo.data.local.UserPreferencesRepository
import com.yess.inquizitivo.data.model.Question
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddQuestionUiState(
    val categoryId: String = "programming",
    val questionText: String = "",
    val option1: String = "",
    val option2: String = "",
    val option3: String = "",
    val option4: String = "",
    val correctAnswerIndex: Int = 0,
    val explanation: String = "",
    val errorMessage: String? = null,
    val isSavedSuccess: Boolean = false
)

class AddQuestionViewModel(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddQuestionUiState())
    val uiState: StateFlow<AddQuestionUiState> = _uiState.asStateFlow()

    fun updateCategoryId(categoryId: String) {
        _uiState.update { it.copy(categoryId = categoryId, errorMessage = null) }
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

    fun updateExplanation(text: String) {
        _uiState.update { it.copy(explanation = text, errorMessage = null) }
    }

    fun saveQuestion() {
        val state = _uiState.value
        if (state.questionText.isBlank() ||
            state.option1.isBlank() ||
            state.option2.isBlank() ||
            state.option3.isBlank() ||
            state.option4.isBlank()
        ) {
            _uiState.update { it.copy(errorMessage = "Por favor completa la pregunta y las 4 opciones.") }
            return
        }

        viewModelScope.launch {
            val newQuestion = Question(
                id = "custom_${System.currentTimeMillis()}",
                categoryId = state.categoryId,
                questionText = state.questionText.trim(),
                options = listOf(
                    state.option1.trim(),
                    state.option2.trim(),
                    state.option3.trim(),
                    state.option4.trim()
                ),
                correctAnswerIndex = state.correctAnswerIndex,
                explanation = state.explanation.trim()
            )

            userPreferencesRepository.addCustomQuestion(newQuestion)
            _uiState.update { it.copy(isSavedSuccess = true, errorMessage = null) }
        }
    }

    fun resetSavedState() {
        _uiState.update { AddQuestionUiState() }
    }
}

class AddQuestionViewModelFactory(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AddQuestionViewModel(userPreferencesRepository) as T
    }
}
