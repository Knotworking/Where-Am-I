package com.knotworking.whereami.data.photo.datasource

import com.knotworking.whereami.core.network.BenHikesApi
import com.knotworking.whereami.domain.photo.model.Photo
import javax.inject.Inject

class BenHikesDataSource @Inject constructor(
    private val benHikesApi: BenHikesApi
) : RemotePhotoDataSource {

    override suspend fun fetchPhotos(count: Int): List<Photo> {
        // BenHikes endpoint currently returns only 1 photo per call
        // We can call it multiple times if needed, or just return one in a list
        val photos = mutableListOf<Photo>()
        repeat(count) {
            try {
                val dto = benHikesApi.getRandomPhoto()
                photos.add(
                    Photo(
                        id = dto.filename,
                        title = dto.filename,
                        latitude = dto.lat,
                        longitude = dto.lng,
                        urlM = dto.url
                    )
                )
            } catch (e: Exception) {
                // handle or log error
            }
        }
        return photos
    }
}
