package com.knotworking.whereami.feature.game

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isFalse
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import com.knotworking.whereami.domain.game.usecase.CalculateDistanceUseCase
import com.knotworking.whereami.domain.game.usecase.CalculateScoreUseCase
import com.knotworking.whereami.domain.photo.usecase.GetRandomPhotoUseCase
import com.knotworking.whereami.domain.photo.model.Photo
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@ExperimentalCoroutinesApi
class GameViewModelTest {
    private val getRandomPhotoUseCase: GetRandomPhotoUseCase = mockk()
    private val calculateDistanceUseCase: CalculateDistanceUseCase = mockk()
    private val calculateScoreUseCase: CalculateScoreUseCase = mockk()

    private lateinit var viewModel: GameViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    private val mockPhoto = Photo(
        id = "1",
        title = "title",
        latitude = 10.0,
        longitude = 10.0,
        urlM = "url"
    )

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { getRandomPhotoUseCase() } returns mockPhoto
        viewModel = GameViewModel(
            getRandomPhotoUseCase,
            calculateDistanceUseCase,
            calculateScoreUseCase
        )
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state loads first round`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.currentRound).isEqualTo(1)
            assertThat(state.currentPhoto).isEqualTo(mockPhoto)
            assertThat(state.isLoading).isFalse()
        }
    }

    @Test
    fun `submit guess updates score and round state`() = runTest {
        every { calculateDistanceUseCase(any(), any(), any(), any()) } returns 1000.0
        every { calculateScoreUseCase(1000.0) } returns 4000

        viewModel.uiState.test {
            awaitItem() // initial state
            viewModel.submitGuess(11.0, 11.0)
            val state = awaitItem()
            assertThat(state.totalScore).isEqualTo(4000)
            assertThat(state.guesses).hasSize(1)
            assertThat(state.lastGuess).isNotNull()
            assertThat(state.lastGuess!!.score).isEqualTo(4000)
        }
    }

    @Test
    fun `next round increments round count and loads new photo`() = runTest {
        val nextPhoto = mockPhoto.copy(id = "2")
        coEvery { getRandomPhotoUseCase() } returns nextPhoto

        viewModel.uiState.test {
            awaitItem() // initial state
            viewModel.nextRound()
            // nextRound emits: round++ → clear photo → set photo (3 updates)
            val state = expectMostRecentItem()
            assertThat(state.currentRound).isEqualTo(2)
            assertThat(state.currentPhoto).isEqualTo(nextPhoto)
        }
    }

    @Test
    fun `isGameOver set to true after 5 rounds`() = runTest {
        viewModel.uiState.test {
            awaitItem() // initial state
            repeat(GameViewModel.TOTAL_ROUNDS - 1) {
                viewModel.nextRound()
            }
            val midState = expectMostRecentItem()
            assertThat(midState.currentRound).isEqualTo(5)
            assertThat(midState.isGameOver).isFalse()

            // Final nextRound() on round 5 emits exactly one update: isGameOver=true
            viewModel.nextRound()
            assertThat(awaitItem().isGameOver).isTrue()
        }
    }
}
