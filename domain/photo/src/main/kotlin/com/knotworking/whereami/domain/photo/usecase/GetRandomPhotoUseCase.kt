package com.knotworking.whereami.domain.photo.usecase

import com.knotworking.whereami.domain.photo.repository.PhotoRepository
import com.knotworking.whereami.domain.photo.model.Photo
import javax.inject.Inject

class GetRandomPhotoUseCase @Inject constructor(
    private val photoRepository: PhotoRepository
) {
    suspend operator fun invoke(): Photo? {
        return photoRepository.getRandomGeotaggedPhotos(10).randomOrNull()
    }
}
