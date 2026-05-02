package com.knotworking.whereami.domain.settings.repository

import com.knotworking.whereami.domain.settings.model.AppTheme
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getTheme(): Flow<AppTheme>
    suspend fun setTheme(theme: AppTheme)
}
