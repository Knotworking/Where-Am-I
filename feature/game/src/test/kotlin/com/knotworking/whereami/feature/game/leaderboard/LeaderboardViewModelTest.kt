package com.knotworking.whereami.feature.game.leaderboard

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isEmpty
import assertk.assertions.isTrue
import com.knotworking.whereami.domain.game.model.HighScore
import com.knotworking.whereami.domain.game.repository.HighScoreRepository
import com.knotworking.whereami.domain.game.usecase.ClearHighScoresUseCase
import com.knotworking.whereami.domain.game.usecase.GetHighScoresUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
class LeaderboardViewModelTest {

    private val fakeRepository = FakeHighScoreRepository()
    private val getHighScoresUseCase = GetHighScoresUseCase(fakeRepository)
    private val clearHighScoresUseCase = ClearHighScoresUseCase(fakeRepository)
    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: LeaderboardViewModel

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = LeaderboardViewModel(getHighScoresUseCase, clearHighScoresUseCase)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state shows empty scores and not loading`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.scores).isEmpty()
            assertThat(state.isLoading).isEqualTo(false)
        }
    }

    @Test
    fun `scores flow into uiState`() = runTest {
        val scores = listOf(
            HighScore(id = 1, totalScore = 5000, timestamp = 1000L),
            HighScore(id = 2, totalScore = 3000, timestamp = 2000L)
        )

        viewModel.uiState.test {
            awaitItem() // initial empty state
            fakeRepository.emitScores(scores)
            val state = awaitItem()
            assertThat(state.scores).isEqualTo(scores)
            assertThat(state.isLoading).isEqualTo(false)
        }
    }

    @Test
    fun `clearAll delegates to use case and clears scores`() = runTest {
        fakeRepository.emitScores(
            listOf(HighScore(id = 1, totalScore = 5000, timestamp = 1000L))
        )

        viewModel.uiState.test {
            expectMostRecentItem() // skip to latest
            viewModel.onAction(LeaderboardAction.ClearAll)
            val state = awaitItem()
            assertThat(state.scores).isEmpty()
            assertThat(fakeRepository.cleared).isTrue()
        }
    }
}

private class FakeHighScoreRepository : HighScoreRepository {
    private val scoresFlow = MutableStateFlow<List<HighScore>>(emptyList())
    var cleared = false

    fun emitScores(scores: List<HighScore>) {
        scoresFlow.value = scores
    }

    override fun getTopScores(): Flow<List<HighScore>> = scoresFlow

    override suspend fun save(totalScore: Int) = Unit

    override suspend fun clearAll() {
        cleared = true
        scoresFlow.value = emptyList()
    }
}
