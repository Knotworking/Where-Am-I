package com.knotworking.whereami.feature.game.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knotworking.whereami.domain.game.usecase.ClearHighScoresUseCase
import com.knotworking.whereami.domain.game.usecase.GetHighScoresUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val getHighScoresUseCase: GetHighScoresUseCase,
    private val clearHighScoresUseCase: ClearHighScoresUseCase
) : ViewModel() {

    val uiState: StateFlow<LeaderboardUiState> = getHighScoresUseCase()
        .map { LeaderboardUiState(scores = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LeaderboardUiState())

    fun clearAll() {
        viewModelScope.launch { clearHighScoresUseCase() }
    }
}
