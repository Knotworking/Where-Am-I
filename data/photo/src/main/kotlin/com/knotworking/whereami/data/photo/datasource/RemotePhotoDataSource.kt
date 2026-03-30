package com.knotworking.whereami.data.photo.datasource

import com.knotworking.whereami.domain.photo.model.Photo

interface RemotePhotoDataSource {
    suspend fun fetchPhoto(): Photo?
}
