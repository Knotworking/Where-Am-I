package com.knotworking.whereami.data.photo.datasource

import com.knotworking.whereami.domain.photo.model.Photo

interface RemotePhotoDataSource {
    suspend fun fetchPhotos(count: Int): List<Photo>
}
