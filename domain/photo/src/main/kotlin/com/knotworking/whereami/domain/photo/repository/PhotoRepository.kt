package com.knotworking.whereami.domain.photo.repository

import com.knotworking.whereami.domain.photo.model.Photo
import com.knotworking.whereami.domain.photo.model.PhotoSource
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {
    suspend fun getRandomGeotaggedPhoto(): Photo?
    fun getPhotoSource(): Flow<PhotoSource>
    suspend fun setPhotoSource(source: PhotoSource)
}
