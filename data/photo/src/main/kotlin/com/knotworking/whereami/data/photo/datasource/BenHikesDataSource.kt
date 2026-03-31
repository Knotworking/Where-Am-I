package com.knotworking.whereami.data.photo.datasource

import com.knotworking.whereami.data.photo.datasource.api.BenHikesApi
import com.knotworking.whereami.domain.photo.model.Photo
import javax.inject.Inject

class BenHikesDataSource @Inject constructor(
    private val benHikesApi: BenHikesApi
) : RemotePhotoDataSource {

    override suspend fun fetchPhoto(): Photo {
        val dto = benHikesApi.getRandomPhoto()
        return Photo(
            id = dto.filename,
            title = dto.filename,
            latitude = dto.lat,
            longitude = dto.lng,
            urlM = dto.url
        )
    }
}
