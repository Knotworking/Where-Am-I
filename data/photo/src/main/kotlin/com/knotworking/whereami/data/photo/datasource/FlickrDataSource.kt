package com.knotworking.whereami.data.photo.datasource

import com.knotworking.whereami.domain.photo.model.Photo
import com.knotworking.whereami.data.photo.datasource.api.FlickrApi
import com.knotworking.whereami.data.photo.di.FlickrApiKey
import javax.inject.Inject

class FlickrDataSource @Inject constructor(
    private val flickrApi: FlickrApi,
    @param:FlickrApiKey private val apiKey: String
) : RemotePhotoDataSource {
    
    override suspend fun fetchPhotos(count: Int): List<Photo> {
        val response = flickrApi.searchPhotos(apiKey = apiKey, perPage = count)
        return response.photos.photo.map { dto ->
            Photo(
                id = dto.id,
                title = dto.title,
                latitude = dto.latitude.toDoubleOrNull() ?: 0.0,
                longitude = dto.longitude.toDoubleOrNull() ?: 0.0,
                urlM = dto.urlM
            )
        }
    }
}
