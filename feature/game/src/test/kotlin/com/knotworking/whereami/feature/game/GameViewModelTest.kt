package com.knotworking.whereami.feature.game

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
    fun `initial state loads first round`() {
        val state = viewModel.uiState.value
        assertThat(state.currentRound).isEqualTo(1)
        assertThat(state.currentPhoto).isEqualTo(mockPhoto)
        assertThat(state.isLoading).isFalse()
    }

    @Test
    fun `submit guess updates score and round state`() {
        every { calculateDistanceUseCase(any(), any(), any(), any()) } returns 1000.0
        every { calculateScoreUseCase(1000.0) } returns 4000

        viewModel.submitGuess(11.0, 11.0)

        val state = viewModel.uiState.value
        assertThat(state.totalScore).isEqualTo(4000)
        assertThat(state.guesses).hasSize(1)
        assertThat(state.lastGuess).isNotNull()
        assertThat(state.lastGuess!!.score).isEqualTo(4000)
    }

    @Test
    fun `next round increments round count and loads new photo`() {
        val nextPhoto = mockPhoto.copy(id = "2")
        coEvery { getRandomPhotoUseCase() } returns nextPhoto

        viewModel.nextRound()

        val state = viewModel.uiState.value
        assertThat(state.currentRound).isEqualTo(2)
        assertThat(state.currentPhoto).isEqualTo(nextPhoto)
    }

    @Test
    fun `isGameOver set to true after 5 rounds`() {
        (1 until GameViewModel.TOTAL_ROUNDS).forEach { _ ->
            viewModel.nextRound()
        }
        assertThat(viewModel.uiState.value.currentRound).isEqualTo(5)
        assertThat(viewModel.uiState.value.isGameOver).isFalse()

        viewModel.nextRound()
        assertThat(viewModel.uiState.value.isGameOver).isTrue()
    }
}
