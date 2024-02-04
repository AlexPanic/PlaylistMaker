package com.example.playlistmaker.domain.usecases

import com.example.playlistmaker.domain.repository.SettingsRepository

class SetDarkModeUseCase(private val settingsRepository: SettingsRepository) : IDarkModeUseCase {
    override fun execute(darkModeOn: Boolean): Boolean {
        return settingsRepository.setDarkMode(darkModeOn)
    }

}
