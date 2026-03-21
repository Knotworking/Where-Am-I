package com.knotworking.whereami.data.photo.repository

import com.knotworking.whereami.data.photo.datasource.BenHikesDataSource
import com.knotworking.whereami.data.photo.datasource.FlickrDataSource
import com.knotworking.whereami.domain.photo.model.Photo
import com.knotworking.whereami.domain.photo.model.PhotoSource
import com.knotworking.whereami.domain.photo.repository.PhotoRepository
import com.knotworking.whereami.domain.photo.repository.SettingsRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class PhotoRepositoryImpl @Inject constructor(
    private val flickrDataSource: FlickrDataSource,
    private val benHikesDataSource: BenHikesDataSource,
    private val settingsRepository: SettingsRepository
) : PhotoRepository {
    
    override suspend fun getRandomGeotaggedPhoto(): Photo? {
        val source = settingsRepository.getPhotoSource().first()
        val dataSource = when (source) {
            PhotoSource.FLICKR -> flickrDataSource
            PhotoSource.BENHIKES -> benHikesDataSource
        }
        return dataSource.fetchPhotos(1).firstOrNull()
    }
}
