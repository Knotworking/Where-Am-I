package com.knotworking.whereami.feature.settings

import com.knotworking.whereami.domain.photo.model.PhotoSource
import com.knotworking.whereami.domain.settings.model.AppTheme

data class SettingsUiState(
    val photoSource: PhotoSource = PhotoSource.BENHIKES,
    val appTheme: AppTheme = AppTheme.AUTO
)
