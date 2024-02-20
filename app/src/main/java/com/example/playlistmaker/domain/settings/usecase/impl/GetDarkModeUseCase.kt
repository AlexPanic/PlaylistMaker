package com.example.playlistmaker.domain.settings.usecase.impl

import com.example.playlistmaker.data.settings.SettingsRepository
import com.example.playlistmaker.domain.settings.usecase.IGetDarkModeUseCase

class GetDarkModeUseCase(private val settingsRepository: SettingsRepository) : IGetDarkModeUseCase {
    override fun execute(darkModeOn: Boolean): Boolean =
        settingsRepository.getSavedDarkMode(darkModeOn)

}
