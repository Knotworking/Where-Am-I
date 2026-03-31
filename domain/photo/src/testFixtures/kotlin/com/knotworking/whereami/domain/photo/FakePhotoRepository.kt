package com.knotworking.whereami.domain.photo

import com.knotworking.whereami.core.domain.Error
import com.knotworking.whereami.core.domain.Result
import com.knotworking.whereami.domain.photo.model.Photo
import com.knotworking.whereami.domain.photo.model.PhotoError
import com.knotworking.whereami.domain.photo.model.PhotoSource
import com.knotworking.whereami.domain.photo.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakePhotoRepository : PhotoRepository {
    var photoResult: Result<Photo, Error> = Result.Error(PhotoError.NO_PHOTO_FOUND)
    private val photoSourceFlow = MutableStateFlow(PhotoSource.FLICKR)

    fun setNextPhoto(photo: Photo) {
        photoResult = Result.Success(photo)
    }

    fun setError(error: Error) {
        photoResult = Result.Error(error)
    }

    override suspend fun getRandomGeotaggedPhoto(): Result<Photo, Error> = photoResult

    override fun getPhotoSource(): Flow<PhotoSource> = photoSourceFlow

    override suspend fun setPhotoSource(source: PhotoSource) {
        photoSourceFlow.value = source
    }
}
