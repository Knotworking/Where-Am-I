package com.knotworking.whereami.data.photo.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.knotworking.whereami.core.domain.DataError
import com.knotworking.whereami.core.domain.Error
import com.knotworking.whereami.core.domain.Result
import com.knotworking.whereami.data.photo.datasource.BenHikesDataSource
import com.knotworking.whereami.data.photo.datasource.FlickrDataSource
import com.knotworking.whereami.domain.photo.model.Photo
import com.knotworking.whereami.domain.photo.model.PhotoError
import com.knotworking.whereami.domain.photo.model.PhotoSource
import com.knotworking.whereami.domain.photo.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
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

    override suspend fun getRandomGeotaggedPhoto(): Result<Photo, Error> {
        return try {
            val source = getPhotoSource().first()
            val dataSource = when (source) {
                PhotoSource.FLICKR -> flickrDataSource
                PhotoSource.BENHIKES -> benHikesDataSource
            }
            val photo = dataSource.fetchPhoto()
                ?: return Result.Error(PhotoError.NO_PHOTO_FOUND)
            Result.Success(photo)
        } catch (e: IOException) {
            Result.Error(DataError.Network.NO_INTERNET)
        } catch (e: HttpException) {
            when (e.code()) {
                401 -> Result.Error(DataError.Network.UNAUTHORIZED)
                408 -> Result.Error(DataError.Network.REQUEST_TIMEOUT)
                409 -> Result.Error(DataError.Network.CONFLICT)
                413 -> Result.Error(DataError.Network.PAYLOAD_TOO_LARGE)
                429 -> Result.Error(DataError.Network.TOO_MANY_REQUESTS)
                in 500..599 -> Result.Error(DataError.Network.SERVER_ERROR)
                else -> Result.Error(DataError.Network.UNKNOWN)
            }
        } catch (e: Exception) {
            Result.Error(DataError.Network.UNKNOWN)
        }
    }
}
