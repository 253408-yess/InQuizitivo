package com.yess.inquizitivo.ui.screens.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.yess.inquizitivo.data.local.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class StatsUiState(
    val bestScore: Int = 0,
    val quizzesCompleted: Int = 0,
    val mostPlayedCategory: String = "Ninguna",
    val isLoading: Boolean = false
)

class StatsViewModel(
    userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<StatsUiState> = userPreferencesRepository.userStatsFlow
        .map { stats ->
            StatsUiState(
                bestScore = stats.bestScore,
                quizzesCompleted = stats.quizzesCompleted,
                mostPlayedCategory = stats.mostPlayedCategory,
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = StatsUiState(isLoading = true)
        )
}

class StatsViewModelFactory(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return StatsViewModel(userPreferencesRepository) as T
    }
}
