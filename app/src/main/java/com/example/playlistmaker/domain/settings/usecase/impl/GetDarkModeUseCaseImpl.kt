package com.example.playlistmaker.domain.settings.usecase.impl

import com.example.playlistmaker.domain.settings.SettingsRepository
import com.example.playlistmaker.domain.settings.usecase.GetDarkModeUseCase

class GetDarkModeUseCaseImpl(private val settingsRepository: SettingsRepository) : GetDarkModeUseCase {
    override fun execute(darkModeOn: Boolean): Boolean =
        settingsRepository.getSavedDarkMode(darkModeOn)

}
