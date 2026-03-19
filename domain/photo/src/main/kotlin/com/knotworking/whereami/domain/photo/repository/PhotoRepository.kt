package com.knotworking.whereami.domain.photo.repository

import com.knotworking.whereami.domain.photo.model.Photo

interface PhotoRepository {
    suspend fun getRandomGeotaggedPhotos(count: Int): List<Photo>
}
