package com.knotworking.whereami.domain.photo

import com.knotworking.whereami.domain.photo.model.Photo
import com.knotworking.whereami.domain.photo.model.PhotoSource
import com.knotworking.whereami.domain.photo.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakePhotoRepository : PhotoRepository {
    var nextPhoto: Photo? = null
    private val photoSourceFlow = MutableStateFlow(PhotoSource.FLICKR)

    override suspend fun getRandomGeotaggedPhoto(): Photo? = nextPhoto

    override fun getPhotoSource(): Flow<PhotoSource> = photoSourceFlow

    override suspend fun setPhotoSource(source: PhotoSource) {
        photoSourceFlow.value = source
    }
}
