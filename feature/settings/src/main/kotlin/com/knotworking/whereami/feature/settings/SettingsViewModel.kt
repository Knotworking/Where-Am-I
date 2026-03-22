package com.knotworking.whereami.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knotworking.whereami.domain.photo.model.PhotoSource
import com.knotworking.whereami.domain.photo.usecase.GetPhotoSourceUseCase
import com.knotworking.whereami.domain.photo.usecase.SetPhotoSourceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    getPhotoSourceUseCase: GetPhotoSourceUseCase,
    private val setPhotoSourceUseCase: SetPhotoSourceUseCase
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = getPhotoSourceUseCase()
        .map { SettingsUiState(photoSource = it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SettingsUiState()
        )

    fun setPhotoSource(source: PhotoSource) {
        viewModelScope.launch {
            setPhotoSourceUseCase(source)
        }
    }
}

