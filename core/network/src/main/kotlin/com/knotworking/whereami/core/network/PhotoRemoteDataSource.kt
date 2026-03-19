package com.knotworking.whereami.core.network

import com.knotworking.whereami.domain.photo.model.Photo

interface PhotoRemoteDataSource {
    suspend fun fetchPhotos(count: Int): List<Photo>
}
