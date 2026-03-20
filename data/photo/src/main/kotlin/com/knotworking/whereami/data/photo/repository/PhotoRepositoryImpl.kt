package com.knotworking.whereami.data.photo.repository

import com.knotworking.whereami.domain.photo.repository.PhotoRepository
import com.knotworking.whereami.domain.photo.model.Photo
import com.knotworking.whereami.core.network.RemotePhotoDataSource
import javax.inject.Inject

class PhotoRepositoryImpl @Inject constructor(
    private val remotePhotoDataSource: RemotePhotoDataSource
) : PhotoRepository {
    
    override suspend fun getRandomGeotaggedPhotos(count: Int): List<Photo> {
        return remotePhotoDataSource.fetchPhotos(count)
    }
}
