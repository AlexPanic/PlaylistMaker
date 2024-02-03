package com.example.playlistmaker.domain.usecases

import com.example.playlistmaker.domain.repository.AppThemeRepository

class DarkModeUseCase(private val themeRepository: AppThemeRepository) {
    fun isDeviceDarkModeOn(): Boolean {
        return themeRepository.isDeviceDarkModeOn()
    }

    fun isAppDarkModeOn(): Boolean {
        return themeRepository.isAppDarkModeOn()
    }

    fun setDarkMode(darkModeOn: Boolean) {
        themeRepository.setDarkMode(darkModeOn)
    }

}
