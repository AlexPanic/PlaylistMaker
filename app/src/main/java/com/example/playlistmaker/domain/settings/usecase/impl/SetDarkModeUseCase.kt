package com.example.playlistmaker.domain.settings.usecase.impl

import com.example.playlistmaker.data.settings.SettingsRepository
import com.example.playlistmaker.domain.settings.usecase.ISetDarkModeUseCase

class SetDarkModeUseCase(private val settingsRepository: SettingsRepository) : ISetDarkModeUseCase {
    override fun execute(darkModeOn: Boolean) {
        settingsRepository.setDarkMode(darkModeOn)
    }

}
