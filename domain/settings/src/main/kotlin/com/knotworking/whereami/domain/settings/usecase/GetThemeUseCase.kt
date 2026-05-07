package com.knotworking.whereami.domain.settings.usecase

import com.knotworking.whereami.domain.settings.model.AppTheme
import com.knotworking.whereami.domain.settings.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetThemeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke(): Flow<AppTheme> = settingsRepository.getTheme()
}
