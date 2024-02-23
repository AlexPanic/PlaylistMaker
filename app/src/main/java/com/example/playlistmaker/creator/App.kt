package com.example.playlistmaker.creator

import android.app.Application
import android.content.res.Configuration

class App : Application() {
    companion object {
        var appDarkMode: Boolean = false
    }

    private val getDarkModeUseCase by lazy { Creator.provideGetDarkModeUseCase() }
    private val setDarkModeUseCase by lazy { Creator.provideSetDarkModeUseCase() }
    override fun onCreate() {
        super.onCreate()
        Creator.setApplication(this)
        // темная тема включена на устройстве
        val deviceDarkModeOn =
            resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        // текущая темная тема нашего приложения
        appDarkMode = getDarkModeUseCase.execute(deviceDarkModeOn)
        setDarkModeUseCase.execute(appDarkMode)
    }

}