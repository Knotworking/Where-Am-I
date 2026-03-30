package com.knotworking.whereami.domain.photo

import com.knotworking.whereami.core.domain.DataError
import com.knotworking.whereami.core.domain.Result
import com.knotworking.whereami.domain.photo.model.Photo
import com.knotworking.whereami.domain.photo.model.PhotoSource
import com.knotworking.whereami.domain.photo.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakePhotoRepository : PhotoRepository {
    var photoResult: Result<Photo, DataError.Network> = Result.Error(DataError.Network.NOT_FOUND)
    private val photoSourceFlow = MutableStateFlow(PhotoSource.FLICKR)

    fun setNextPhoto(photo: Photo) {
        photoResult = Result.Success(photo)
    }

    fun setError(error: DataError.Network) {
        photoResult = Result.Error(error)
    }

    override suspend fun getRandomGeotaggedPhoto(): Result<Photo, DataError.Network> = photoResult

    override fun getPhotoSource(): Flow<PhotoSource> = photoSourceFlow

    override suspend fun setPhotoSource(source: PhotoSource) {
        photoSourceFlow.value = source
    }
}
