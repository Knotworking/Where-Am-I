package com.knotworking.whereami.domain.photo.repository

import com.knotworking.whereami.core.domain.DataError
import com.knotworking.whereami.core.domain.Result
import com.knotworking.whereami.domain.photo.model.Photo
import com.knotworking.whereami.domain.photo.model.PhotoSource
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {
    suspend fun getRandomGeotaggedPhoto(): Result<Photo, DataError.Network>
    fun getPhotoSource(): Flow<PhotoSource>
    suspend fun setPhotoSource(source: PhotoSource)
}
