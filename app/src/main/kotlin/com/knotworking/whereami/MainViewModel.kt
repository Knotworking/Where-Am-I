package com.knotworking.whereami

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.knotworking.whereami.core.ui.CONFIG_CHANGE_TIMEOUT_MS
import com.knotworking.whereami.domain.settings.model.AppTheme
import com.knotworking.whereami.domain.settings.usecase.GetThemeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    getThemeUseCase: GetThemeUseCase
) : ViewModel() {

    val appTheme: StateFlow<AppTheme> = getThemeUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(CONFIG_CHANGE_TIMEOUT_MS),
            initialValue = AppTheme.AUTO
        )
}
