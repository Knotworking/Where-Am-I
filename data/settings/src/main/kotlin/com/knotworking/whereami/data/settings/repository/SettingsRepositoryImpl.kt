package com.knotworking.whereami.data.settings.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.knotworking.whereami.data.settings.di.SettingsDataStore
import com.knotworking.whereami.domain.settings.model.AppTheme
import com.knotworking.whereami.domain.settings.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    @SettingsDataStore private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    private object PreferencesKeys {
        val APP_THEME = stringPreferencesKey("app_theme")
    }

    override fun getTheme(): Flow<AppTheme> = dataStore.data.map { prefs ->
        val name = prefs[PreferencesKeys.APP_THEME] ?: AppTheme.AUTO.name
        AppTheme.entries.find { it.name == name } ?: AppTheme.AUTO
    }

    override suspend fun setTheme(theme: AppTheme) {
        dataStore.edit { prefs -> prefs[PreferencesKeys.APP_THEME] = theme.name }
    }
}
