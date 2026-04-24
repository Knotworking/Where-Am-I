package com.knotworking.whereami.feature.game

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.hasSize
import assertk.assertions.isFalse
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import com.knotworking.whereami.core.domain.DataError
import com.knotworking.whereami.domain.game.GameConstants.TOTAL_ROUNDS
import com.knotworking.whereami.domain.game.model.HighScore
import com.knotworking.whereami.domain.game.repository.HighScoreRepository
import com.knotworking.whereami.domain.game.usecase.CalculateDistanceUseCase
import com.knotworking.whereami.domain.photo.model.PhotoError
import com.knotworking.whereami.domain.game.usecase.CalculateScoreUseCase
import com.knotworking.whereami.domain.game.usecase.SaveHighScoreUseCase
import com.knotworking.whereami.domain.photo.FakePhotoRepository
import com.knotworking.whereami.domain.photo.usecase.GetRandomPhotoUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import com.knotworking.whereami.domain.photo.model.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

//TODO could be moved to a testFixture for reusability
private class FakeHighScoreRepository : HighScoreRepository {
    override fun getTopScores(): Flow<List<HighScore>> = flowOf(emptyList())
    override suspend fun save(totalScore: Int) = Unit
    override suspend fun clearAll() = Unit
}

@ExperimentalCoroutinesApi
class GameViewModelTest {
    private val fakePhotoRepository = FakePhotoRepository()
    private val getRandomPhotoUseCase = GetRandomPhotoUseCase(fakePhotoRepository)
    private val calculateDistanceUseCase = CalculateDistanceUseCase()
    private val calculateScoreUseCase = CalculateScoreUseCase()
    private val saveHighScoreUseCase = SaveHighScoreUseCase(FakeHighScoreRepository())

    private lateinit var viewModel: GameViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    private val testPhoto = Photo(
        id = "1",
        title = "title",
        latitude = 10.0,
        longitude = 10.0,
        urlM = "url"
    )

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        fakePhotoRepository.setNextPhoto(testPhoto)
        viewModel = GameViewModel(
            getRandomPhotoUseCase,
            calculateDistanceUseCase,
            calculateScoreUseCase,
            saveHighScoreUseCase
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
            assertThat(state.currentPhoto).isEqualTo(testPhoto)
            assertThat(state.isLoading).isFalse()
        }
    }

    @Test
    fun `submit guess updates score and round state`() = runTest {
        val guessLat = 11.0
        val guessLon = 11.0
        val expectedDistance = calculateDistanceUseCase(
            testPhoto.latitude, testPhoto.longitude, guessLat, guessLon
        )
        val expectedScore = calculateScoreUseCase(expectedDistance)

        viewModel.uiState.test {
            awaitItem() // initial state
            viewModel.onAction(GameAction.SubmitGuess(guessLat, guessLon))
            val state = awaitItem()
            assertThat(state.totalScore).isEqualTo(expectedScore)
            assertThat(state.guesses).hasSize(1)
            assertThat(state.lastGuess).isNotNull()
            assertThat(state.lastGuess!!.score).isEqualTo(expectedScore)
        }
    }

    @Test
    fun `next round increments round count and loads new photo`() = runTest {
        val nextPhoto = testPhoto.copy(id = "2")

        viewModel.uiState.test {
            awaitItem() // initial state
            fakePhotoRepository.setNextPhoto(nextPhoto)
            viewModel.onAction(GameAction.NextRound)
            val state = expectMostRecentItem()
            assertThat(state.currentRound).isEqualTo(2)
            assertThat(state.currentPhoto).isEqualTo(nextPhoto)
        }
    }

    @Test
    fun `loadNextRound with network error sets NetworkError`() = runTest {
        fakePhotoRepository.setError(DataError.Network.NO_INTERNET)
        viewModel = GameViewModel(getRandomPhotoUseCase, calculateDistanceUseCase, calculateScoreUseCase, saveHighScoreUseCase)

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isEqualTo(GameError.NetworkError)
            assertThat(state.isLoading).isFalse()
        }
    }

    @Test
    fun `loadNextRound with no photo sets NoPhotoAvailable`() = runTest {
        fakePhotoRepository.setError(PhotoError.NO_PHOTO_FOUND)
        viewModel = GameViewModel(getRandomPhotoUseCase, calculateDistanceUseCase, calculateScoreUseCase, saveHighScoreUseCase)

        viewModel.uiState.test {
            val state = awaitItem()
            assertThat(state.error).isEqualTo(GameError.NoPhotoAvailable)
            assertThat(state.isLoading).isFalse()
        }
    }

    @Test
    fun `start new game resets state after game has progressed`() = runTest {
        viewModel.uiState.test {
            awaitItem() // initial state
            viewModel.onAction(GameAction.SubmitGuess(11.0, 11.0))
            awaitItem() // state after guess

            viewModel.onAction(GameAction.StartNewGame)
            val state = expectMostRecentItem()
            assertThat(state.totalScore).isEqualTo(0)
            assertThat(state.currentRound).isEqualTo(1)
            assertThat(state.guesses).hasSize(0)
            assertThat(state.isGameOver).isFalse()
        }
    }

    @Test
    fun `isGameOver set to true after 5 rounds`() = runTest {
        viewModel.uiState.test {
            awaitItem() // initial state
            repeat(TOTAL_ROUNDS - 1) {
                viewModel.onAction(GameAction.NextRound)
            }
            val midState = expectMostRecentItem()
            assertThat(midState.currentRound).isEqualTo(5)
            assertThat(midState.isGameOver).isFalse()

            // Final nextRound() on round 5 emits exactly one update: isGameOver=true
            viewModel.onAction(GameAction.NextRound)
            assertThat(awaitItem().isGameOver).isTrue()
        }
    }
}
