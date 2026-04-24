package com.knotworking.whereami.feature.settings

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.knotworking.whereami.domain.photo.FakePhotoRepository
import com.knotworking.whereami.domain.photo.model.PhotoSource
import com.knotworking.whereami.domain.photo.usecase.GetPhotoSourceUseCase
import com.knotworking.whereami.domain.photo.usecase.SetPhotoSourceUseCase
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
class SettingsViewModelTest {
    private val fakePhotoRepository = FakePhotoRepository()
    private val getPhotoSourceUseCase = GetPhotoSourceUseCase(fakePhotoRepository)
    private val setPhotoSourceUseCase = SetPhotoSourceUseCase(fakePhotoRepository)

    private lateinit var viewModel: SettingsViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = SettingsViewModel(getPhotoSourceUseCase, setPhotoSourceUseCase)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state reflects saved photo source`() = runTest {
        viewModel.uiState.test {
            assertThat(awaitItem().photoSource).isEqualTo(PhotoSource.FLICKR)
        }
    }

    @Test
    fun `setPhotoSource updates uiState to new source`() = runTest {
        viewModel.uiState.test {
            awaitItem() // initial state
            viewModel.onAction(SettingsAction.SetPhotoSource(PhotoSource.BENHIKES))
            assertThat(awaitItem().photoSource).isEqualTo(PhotoSource.BENHIKES)
        }
    }

    @Test
    fun `setPhotoSource persists change to repository`() = runTest {
        viewModel.onAction(SettingsAction.SetPhotoSource(PhotoSource.BENHIKES))
        viewModel.uiState.test {
            assertThat(awaitItem().photoSource).isEqualTo(PhotoSource.BENHIKES)
        }
    }
}
