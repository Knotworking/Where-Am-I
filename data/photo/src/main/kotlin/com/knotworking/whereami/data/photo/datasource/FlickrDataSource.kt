package com.knotworking.whereami.data.photo.datasource

import com.knotworking.whereami.domain.photo.model.Photo
import com.knotworking.whereami.data.photo.datasource.api.FlickrApi
import com.knotworking.whereami.data.photo.di.FlickrApiKey
import javax.inject.Inject

class FlickrDataSource @Inject constructor(
    private val flickrApi: FlickrApi,
    @param:FlickrApiKey private val apiKey: String
) : RemotePhotoDataSource {

    override suspend fun fetchPhoto(): Photo? {
        val response = flickrApi.searchPhotos(apiKey = apiKey, perPage = 1)
        return response.photos.photo.map { dto ->
            Photo(
                id = dto.id,
                title = dto.title,
                latitude = dto.latitude.toDoubleOrNull() ?: 0.0,
                longitude = dto.longitude.toDoubleOrNull() ?: 0.0,
                urlM = dto.urlM
            )
        }.firstOrNull()
    }
}
