package com.knotworking.whereami.domain.photo.repository

import com.knotworking.whereami.domain.photo.model.PhotoSource
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getPhotoSource(): Flow<PhotoSource>
    suspend fun setPhotoSource(source: PhotoSource)
}
