package com.knotworking.whereami.feature.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knotworking.whereami.domain.game.model.HighScore
import com.knotworking.whereami.domain.game.usecase.ClearHighScoresUseCase
import com.knotworking.whereami.domain.game.usecase.GetHighScoresUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LeaderboardViewModel @Inject constructor(
    private val getHighScoresUseCase: GetHighScoresUseCase,
    private val clearHighScoresUseCase: ClearHighScoresUseCase
) : ViewModel() {

    val scores: StateFlow<List<HighScore>> = getHighScoresUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun clearAll() {
        viewModelScope.launch { clearHighScoresUseCase() }
    }
}
