package com.knotworking.whereami

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import com.knotworking.whereami.domain.settings.FakeSettingsRepository
import com.knotworking.whereami.domain.settings.model.AppTheme
import com.knotworking.whereami.domain.settings.usecase.GetThemeUseCase
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
class MainViewModelTest {
    private val fakeSettingsRepository = FakeSettingsRepository()
    private val getThemeUseCase = GetThemeUseCase(fakeSettingsRepository)

    private lateinit var viewModel: MainViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MainViewModel(getThemeUseCase)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial appTheme is AUTO`() = runTest {
        viewModel.appTheme.test {
            assertThat(awaitItem()).isEqualTo(AppTheme.AUTO)
        }
    }

    @Test
    fun `appTheme propagates theme changes from repository`() = runTest {
        viewModel.appTheme.test {
            awaitItem() // initial AUTO
            fakeSettingsRepository.setTheme(AppTheme.DARK)
            assertThat(awaitItem()).isEqualTo(AppTheme.DARK)
        }
    }
}
