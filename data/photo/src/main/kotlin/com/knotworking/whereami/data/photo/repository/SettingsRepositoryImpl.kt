package com.knotworking.whereami.data.photo.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.knotworking.whereami.domain.photo.model.PhotoSource
import com.knotworking.whereami.domain.photo.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    private object PreferencesKeys {
        val PHOTO_SOURCE = stringPreferencesKey("photo_source")
    }

    override fun getPhotoSource(): Flow<PhotoSource> = dataStore.data.map { preferences ->
        val sourceName = preferences[PreferencesKeys.PHOTO_SOURCE] ?: PhotoSource.FLICKR.name
        PhotoSource.valueOf(sourceName)
    }

    override suspend fun setPhotoSource(source: PhotoSource) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.PHOTO_SOURCE] = source.name
        }
    }
}
