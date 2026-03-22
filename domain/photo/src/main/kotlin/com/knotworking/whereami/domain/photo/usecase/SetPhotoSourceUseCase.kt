package com.knotworking.whereami.domain.photo.usecase

import com.knotworking.whereami.domain.photo.model.PhotoSource
import com.knotworking.whereami.domain.photo.repository.PhotoRepository
import javax.inject.Inject

class SetPhotoSourceUseCase @Inject constructor(
    private val settingsRepository: PhotoRepository
) {
    suspend operator fun invoke(source: PhotoSource) = settingsRepository.setPhotoSource(source)
}
