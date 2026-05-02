package com.knotworking.whereami.domain.settings.usecase

import com.knotworking.whereami.domain.settings.model.AppTheme
import com.knotworking.whereami.domain.settings.repository.SettingsRepository
import javax.inject.Inject

class SetThemeUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(theme: AppTheme) = settingsRepository.setTheme(theme)
}
