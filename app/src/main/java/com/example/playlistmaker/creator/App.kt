package com.example.playlistmaker.creator

import android.app.Application
import android.content.res.Configuration
import com.example.playlistmaker.di.dataModule
import com.example.playlistmaker.di.interactorModule
import com.example.playlistmaker.di.mediaPlayerModule
import com.example.playlistmaker.di.repositoryModule
import com.example.playlistmaker.di.useCaseModule
import com.example.playlistmaker.di.viewModelModule
import com.example.playlistmaker.domain.settings.usecase.GetDarkModeUseCase
import com.example.playlistmaker.domain.settings.usecase.SetDarkModeUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.java.KoinJavaComponent.inject

class App : Application() {
    companion object {
        var appDarkMode: Boolean = false
    }

    private val getDarkModeUseCase: GetDarkModeUseCase by inject(GetDarkModeUseCase::class.java)
    private val setDarkModeUseCase: SetDarkModeUseCase by inject(SetDarkModeUseCase::class.java)
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(dataModule, interactorModule, mediaPlayerModule, repositoryModule, useCaseModule, viewModelModule)
        }


        // темная тема включена на устройстве
        val deviceDarkModeOn =
            resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
        // текущая темная тема нашего приложения
        appDarkMode = getDarkModeUseCase.execute(deviceDarkModeOn)
        setDarkModeUseCase.execute(appDarkMode)

    }

}