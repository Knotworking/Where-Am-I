package com.knotworking.whereami.core.data.repository

import com.knotworking.whereami.core.domain.repository.PhotoRepository
import com.knotworking.whereami.core.model.Photo
import com.knotworking.whereami.core.network.FlickrService
import com.knotworking.whereami.core.network.di.FlickrApiKey
import javax.inject.Inject

class PhotoRepositoryImpl @Inject constructor(
    private val flickrService: FlickrService,
    @FlickrApiKey private val apiKey: String
) : PhotoRepository {
    override suspend fun getRandomGeotaggedPhotos(count: Int): List<Photo> {
        // For simplicity, we just fetch one page and pick random ones if needed, 
        // or just return the first few.
        // In a real app, we might randomize the page.
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
