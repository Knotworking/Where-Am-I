package com.knotworking.whereami.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knotworking.whereami.domain.photo.model.PhotoSource
import com.knotworking.whereami.domain.photo.usecase.GetPhotoSourceUseCase
import com.knotworking.whereami.domain.photo.usecase.SetPhotoSourceUseCase
import com.knotworking.whereami.domain.settings.model.AppTheme
import com.knotworking.whereami.domain.settings.usecase.GetThemeUseCase
import com.knotworking.whereami.domain.settings.usecase.SetThemeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import com.knotworking.whereami.core.ui.CONFIG_CHANGE_TIMEOUT_MS
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SettingsAction {
    data class SetPhotoSource(val source: PhotoSource) : SettingsAction
    data class SetTheme(val theme: AppTheme) : SettingsAction
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getPhotoSourceUseCase: GetPhotoSourceUseCase,
    private val setPhotoSourceUseCase: SetPhotoSourceUseCase,
    getThemeUseCase: GetThemeUseCase,
    private val setThemeUseCase: SetThemeUseCase
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        getPhotoSourceUseCase(),
        getThemeUseCase()
    ) { source, theme ->
        SettingsUiState(photoSource = source, appTheme = theme)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(CONFIG_CHANGE_TIMEOUT_MS),
        initialValue = SettingsUiState()
    )

    fun onAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.SetPhotoSource -> setPhotoSource(action.source)
            is SettingsAction.SetTheme -> setTheme(action.theme)
        }
    }

    private fun setPhotoSource(source: PhotoSource) {
        viewModelScope.launch {
            setPhotoSourceUseCase(source)
        }
    }

    private fun setTheme(theme: AppTheme) {
        viewModelScope.launch {
            setThemeUseCase(theme)
        }
    }
}
