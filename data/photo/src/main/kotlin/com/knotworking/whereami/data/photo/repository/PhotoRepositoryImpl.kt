package com.knotworking.whereami.data.photo.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.knotworking.whereami.core.domain.Error
import com.knotworking.whereami.core.domain.Result
import com.knotworking.whereami.core.network.safeCall
import com.knotworking.whereami.data.photo.datasource.BenHikesDataSource
import com.knotworking.whereami.data.photo.datasource.FlickrDataSource
import com.knotworking.whereami.domain.photo.model.Photo
import com.knotworking.whereami.domain.photo.model.PhotoError
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
        PhotoSource.entries.find { it.name == sourceName } ?: PhotoSource.BENHIKES
    }

    override suspend fun setPhotoSource(source: PhotoSource) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.PHOTO_SOURCE] = source.name
        }
    }

    override suspend fun getRandomGeotaggedPhoto(): Result<Photo, Error> {
        //TODO Fetch [round_count] photos in one network call, cache them in memory.
        // return next cached photo until cache empty.

        val source = getPhotoSource().first()
        val dataSource = when (source) {
            PhotoSource.FLICKR -> flickrDataSource
            PhotoSource.BENHIKES -> benHikesDataSource
        }
        return when (val result = safeCall { dataSource.fetchPhoto() }) {
            is Result.Success -> result.data?.let { Result.Success(it) }
                ?: Result.Error(PhotoError.NO_PHOTO_FOUND)
            is Result.Error -> result
        }
    }
}
