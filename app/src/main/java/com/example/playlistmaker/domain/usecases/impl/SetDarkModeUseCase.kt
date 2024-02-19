package com.example.playlistmaker.domain.usecases.impl

import com.example.playlistmaker.data.settings.SettingsRepository
import com.example.playlistmaker.domain.usecases.ISetDarkModeUseCase

class SetDarkModeUseCase(private val settingsRepository: SettingsRepository) : ISetDarkModeUseCase {
    override fun execute(darkModeOn: Boolean) {
        settingsRepository.setDarkMode(darkModeOn)
    }

}
