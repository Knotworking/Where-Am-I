package com.knotworking.whereami.domain.photo.usecase

import com.knotworking.whereami.domain.photo.model.PhotoSource
import com.knotworking.whereami.domain.photo.repository.SettingsRepository
import javax.inject.Inject

class SetPhotoSourceUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(source: PhotoSource) = settingsRepository.setPhotoSource(source)
}
