package com.knotworking.whereami.core.network

import com.knotworking.whereami.domain.photo.model.Photo
import com.knotworking.whereami.core.network.di.FlickrApiKey
import javax.inject.Inject

/**
 * Encapsulates all Flickr-specific logic, including mapping DTOs to Domain models.
 */
class FlickrPhotoRemoteDataSource @Inject constructor(
    private val flickrService: FlickrService,
    @FlickrApiKey private val apiKey: String
) : PhotoRemoteDataSource {
    
    override suspend fun fetchPhotos(count: Int): List<Photo> {
        val response = flickrService.searchPhotos(apiKey = apiKey, perPage = count)
        return response.photos.photo.map { dto ->
            Photo(
                id = dto.id,
                owner = dto.owner,
                secret = dto.secret,
                server = dto.server,
                farm = dto.farm,
                title = dto.title,
                latitude = dto.latitude.toDoubleOrNull() ?: 0.0,
                longitude = dto.longitude.toDoubleOrNull() ?: 0.0,
                urlM = dto.urlM
            )
        }
    }
}
