package com.knotworking.whereami.data.photo.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.knotworking.whereami.data.photo.datasource.BenHikesDataSource
import com.knotworking.whereami.data.photo.datasource.FlickrDataSource
import com.knotworking.whereami.domain.photo.model.Photo
import com.knotworking.whereami.domain.photo.model.PhotoSource
import com.knotworking.whereami.domain.photo.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PhotoRepositoryImpl @Inject constructor(
    private val flickrDataSource: FlickrDataSource,
    private val benHikesDataSource: BenHikesDataSource,
    private val dataStore: DataStore<Preferences>
) : PhotoRepository {

    private object PreferencesKeys {
        val PHOTO_SOURCE = stringPreferencesKey("photo_source")
    }

    override fun getPhotoSource(): Flow<PhotoSource> = dataStore.data.map { preferences ->
        val sourceName = preferences[PreferencesKeys.PHOTO_SOURCE] ?: PhotoSource.BENHIKES.name
        PhotoSource.valueOf(sourceName)
    }

    override suspend fun setPhotoSource(source: PhotoSource) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.PHOTO_SOURCE] = source.name
        }
    }

    override suspend fun getRandomGeotaggedPhoto(): Photo? {
        val source = getPhotoSource().first()
        val dataSource = when (source) {
            PhotoSource.FLICKR -> flickrDataSource
            PhotoSource.BENHIKES -> benHikesDataSource
        }
        return dataSource.fetchPhotos(1).firstOrNull()
    }
}
