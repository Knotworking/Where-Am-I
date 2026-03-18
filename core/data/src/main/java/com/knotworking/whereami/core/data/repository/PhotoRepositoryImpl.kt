package com.knotworking.whereami.core.data.repository

import com.knotworking.whereami.core.domain.repository.PhotoRepository
import com.knotworking.whereami.core.model.Photo
import com.knotworking.whereami.core.network.PhotoRemoteDataSource
import javax.inject.Inject

class PhotoRepositoryImpl @Inject constructor(
    private val photoRemoteDataSource: PhotoRemoteDataSource
) : PhotoRepository {
    
    override suspend fun getRandomGeotaggedPhotos(count: Int): List<Photo> {
        // The repository simply coordinates. In the future, it could check 
        // a LocalDataSource (Room) before calling the RemoteDataSource.
        return photoRemoteDataSource.fetchPhotos(count)
    }
}
