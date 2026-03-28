package com.knotworking.whereami.feature.settings

import com.knotworking.whereami.domain.photo.model.PhotoSource

data class SettingsUiState(
    val photoSource: PhotoSource = PhotoSource.BENHIKES
)
