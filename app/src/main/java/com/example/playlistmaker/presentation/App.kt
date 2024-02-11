package com.example.playlistmaker.presentation

import android.app.Application
import android.content.res.Configuration
import com.example.playlistmaker.util.Creator

class App : Application() {

    private val getDarkModeUseCase by lazy { Creator.provideGetDarkModeUseCase()}
    private val setDarkModeUseCase by lazy { Creator.provideSetDarkModeUseCase()}
    override fun onCreate() {
        super.onCreate()
        Creator.setApplication(this)
        // темная тема включена на устройстве
        val deviceDarkModeOn = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        // темная тема нашего приложения
        switchTheme(getDarkModeUseCase.execute(deviceDarkModeOn))
    }

    // переключаем темную тему в приложении
    fun switchTheme(darkModeOn: Boolean) {
        setDarkModeUseCase.execute(darkModeOn)
        appDarkMode = darkModeOn
    }

    companion object {
        var appDarkMode: Boolean = false
    }

}