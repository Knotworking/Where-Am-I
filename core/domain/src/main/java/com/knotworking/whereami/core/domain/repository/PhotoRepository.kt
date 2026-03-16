package com.knotworking.whereami.core.domain.repository

import com.knotworking.whereami.core.model.Photo

interface PhotoRepository {
    suspend fun getRandomGeotaggedPhotos(count: Int): List<Photo>
}
