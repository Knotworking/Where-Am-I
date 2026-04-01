package com.knotworking.whereami.feature.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knotworking.whereami.core.domain.DataError
import com.knotworking.whereami.core.domain.Result
import com.knotworking.whereami.domain.game.model.Guess
import com.knotworking.whereami.domain.game.usecase.CalculateDistanceUseCase
import com.knotworking.whereami.domain.game.usecase.CalculateScoreUseCase
import com.knotworking.whereami.domain.game.usecase.SaveHighScoreUseCase
import com.knotworking.whereami.domain.photo.model.PhotoError
import com.knotworking.whereami.domain.photo.usecase.GetRandomPhotoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val getRandomPhotoUseCase: GetRandomPhotoUseCase,
    private val calculateDistanceUseCase: CalculateDistanceUseCase,
    private val calculateScoreUseCase: CalculateScoreUseCase,
    private val saveHighScoreUseCase: SaveHighScoreUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    companion object {
        const val TOTAL_ROUNDS = 5
    }

    init {
        startNewGame()
    }

    fun startNewGame() {
        _uiState.update { 
            it.copy(
                totalScore = 0,
                currentRound = 1,
                guesses = emptyList(),
                lastGuess = null,
                isGameOver = false,
                isLoading = true,
                isPhotoLoading = true
            )
        }
        loadNextRound()
    }

    private fun loadNextRound() {
        viewModelScope.launch {
            _uiState.update { it.copy(isPhotoLoading = true, error = null, currentPhoto = null) }
            when (val result = getRandomPhotoUseCase()) {
                is Result.Success -> _uiState.update {
                    it.copy(isLoading = false, isPhotoLoading = false, currentPhoto = result.data)
                }
                is Result.Error -> {
                    val gameError = when (result.error) {
                        PhotoError.NO_PHOTO_FOUND -> GameError.NoPhotoAvailable
                        is DataError.Network -> GameError.NetworkError
                        else -> GameError.Unknown
                    }
                    _uiState.update {
                        it.copy(isLoading = false, isPhotoLoading = false, error = gameError)
                    }
                }
            }
        }
    }

    fun submitGuess(latitude: Double, longitude: Double) {
        val currentPhoto = _uiState.value.currentPhoto ?: return
        
        val distance = calculateDistanceUseCase(
            lat1 = latitude,
            lon1 = longitude,
            lat2 = currentPhoto.latitude,
            lon2 = currentPhoto.longitude
        )
        
        val score = calculateScoreUseCase(distance)
        
        val guess = Guess(
            latitude = latitude,
            longitude = longitude,
            actualLatitude = currentPhoto.latitude,
            actualLongitude = currentPhoto.longitude,
            distanceMeters = distance,
            score = score
        )
        
        _uiState.update { state ->
            val updatedGuesses = state.guesses + guess
            val updatedTotalScore = state.totalScore + score
            state.copy(
                guesses = updatedGuesses,
                totalScore = updatedTotalScore,
                lastGuess = guess
            )
        }
    }

    fun nextRound() {
        if (_uiState.value.currentRound < TOTAL_ROUNDS) {
            _uiState.update { it.copy(currentRound = it.currentRound + 1, lastGuess = null) }
            loadNextRound()
        } else {
            viewModelScope.launch { saveHighScoreUseCase(_uiState.value.totalScore) }
            _uiState.update { it.copy(isGameOver = true) }
        }
    }
}

