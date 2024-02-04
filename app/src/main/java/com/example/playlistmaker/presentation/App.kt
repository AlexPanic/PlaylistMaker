package com.example.playlistmaker.presentation

import android.app.Application
import com.example.playlistmaker.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.data.repository.SharedPreferencesRepositoryImpl
import com.example.playlistmaker.domain.usecases.DarkModeUseCase

class App : Application() {

    private val sharedPreferencesProvider = SharedPreferencesRepositoryImpl(this)
    private val themeRepository = SettingsRepositoryImpl(sharedPreferencesProvider)
    private val darkModeUseCase = DarkModeUseCase(themeRepository)
    companion object {
        var appDarkMode: Boolean = false
    }

    override fun onCreate() {
        super.onCreate()
        // темный режим включен для всего устройства
        val deviceDarkMode = darkModeUseCase.isDeviceDarkModeOn()
        // темный режим, сохраненный в приложении
        appDarkMode = darkModeUseCase.isAppDarkModeOn()
        switchTheme(deviceDarkMode || appDarkMode)
    }

    fun switchTheme(darkModeOn: Boolean) {
        darkModeUseCase.setDarkMode(darkModeOn)
        appDarkMode = darkModeOn
    }

}