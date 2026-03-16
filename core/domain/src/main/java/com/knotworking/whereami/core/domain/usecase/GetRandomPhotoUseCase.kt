package com.knotworking.whereami.core.domain.usecase

import com.knotworking.whereami.core.domain.repository.PhotoRepository
import com.knotworking.whereami.core.model.Photo
import javax.inject.Inject

class GetRandomPhotoUseCase @Inject constructor(
    private val photoRepository: PhotoRepository
) {
    suspend operator fun invoke(): Photo? {
        return photoRepository.getRandomGeotaggedPhotos(10).randomOrNull()
    }
}
