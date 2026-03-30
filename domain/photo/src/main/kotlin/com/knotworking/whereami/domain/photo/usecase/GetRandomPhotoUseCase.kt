package com.knotworking.whereami.domain.photo.usecase

import com.knotworking.whereami.core.domain.DataError
import com.knotworking.whereami.core.domain.Result
import com.knotworking.whereami.domain.photo.model.Photo
import com.knotworking.whereami.domain.photo.repository.PhotoRepository
import javax.inject.Inject

class GetRandomPhotoUseCase @Inject constructor(
    private val photoRepository: PhotoRepository
) {
    suspend operator fun invoke(): Result<Photo, DataError.Network> {
        return photoRepository.getRandomGeotaggedPhoto()
    }
}
