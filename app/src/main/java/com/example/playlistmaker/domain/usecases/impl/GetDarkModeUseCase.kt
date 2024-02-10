package com.example.playlistmaker.domain.usecases.impl

import com.example.playlistmaker.domain.repository.SettingsRepository
import com.example.playlistmaker.domain.usecases.IGetDarkModeUseCase

class GetDarkModeUseCase(private val settingsRepository: SettingsRepository) : IGetDarkModeUseCase {
    override fun execute(darkModeOn: Boolean): Boolean =
        settingsRepository.getSavedDarkMode(darkModeOn)

}
