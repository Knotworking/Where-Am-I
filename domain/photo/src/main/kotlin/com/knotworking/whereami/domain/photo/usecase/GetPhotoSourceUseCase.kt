package com.knotworking.whereami.domain.photo.usecase

import com.knotworking.whereami.domain.photo.model.PhotoSource
import com.knotworking.whereami.domain.photo.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPhotoSourceUseCase @Inject constructor(
    private val settingsRepository: PhotoRepository
) {
    operator fun invoke(): Flow<PhotoSource> = settingsRepository.getPhotoSource()
}
