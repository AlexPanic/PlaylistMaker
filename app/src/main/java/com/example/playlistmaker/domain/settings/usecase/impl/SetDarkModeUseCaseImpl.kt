package com.example.playlistmaker.domain.settings.usecase.impl

import com.example.playlistmaker.domain.settings.SettingsRepository
import com.example.playlistmaker.domain.settings.usecase.SetDarkModeUseCase

class SetDarkModeUseCaseImpl(private val settingsRepository: SettingsRepository) : SetDarkModeUseCase {
    override fun execute(darkModeOn: Boolean) {
        settingsRepository.setDarkMode(darkModeOn)
    }

}
