package com.knotworking.whereami.feature.game

import com.knotworking.whereami.domain.game.usecase.CalculateDistanceUseCase
import com.knotworking.whereami.domain.game.usecase.CalculateScoreUseCase
import com.knotworking.whereami.domain.photo.usecase.GetRandomPhotoUseCase
import com.knotworking.whereami.domain.photo.model.Photo
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

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

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        coEvery { getRandomPhotoUseCase() } returns mockPhoto
        viewModel = GameViewModel(
            getRandomPhotoUseCase,
            calculateDistanceUseCase,
            calculateScoreUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state loads first round`() {
        val state = viewModel.uiState.value
        assertEquals(1, state.currentRound)
        assertEquals(mockPhoto, state.currentPhoto)
        assertFalse(state.isLoading)
    }

    @Test
    fun `submit guess updates score and round state`() {
        every { calculateDistanceUseCase(any(), any(), any(), any()) } returns 1000.0
        every { calculateScoreUseCase(1000.0) } returns 4000

        viewModel.submitGuess(11.0, 11.0)

        val state = viewModel.uiState.value
        assertEquals(4000, state.totalScore)
        assertEquals(1, state.guesses.size)
        assertEquals(4000, state.lastGuess?.score)
    }

    @Test
    fun `next round increments round count and loads new photo`() {
        val nextPhoto = mockPhoto.copy(id = "2")
        coEvery { getRandomPhotoUseCase() } returns nextPhoto

        viewModel.nextRound()

        val state = viewModel.uiState.value
        assertEquals(2, state.currentRound)
        assertEquals(nextPhoto, state.currentPhoto)
    }

    @Test
    fun `isGameOver set to true after 5 rounds`() {
        (1 until GameViewModel.TOTAL_ROUNDS).forEach { _ ->
            viewModel.nextRound()
        }
        assertEquals(5, viewModel.uiState.value.currentRound)
        assertFalse(viewModel.uiState.value.isGameOver)

        viewModel.nextRound()
        assertTrue(viewModel.uiState.value.isGameOver)
    }
}
