package com.example.playlistmaker

import android.app.Application
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.repository.PlayerRepositoryImpl
import com.example.playlistmaker.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.data.repository.TracksRepositoryImpl
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.impl.TracksInteractorImpl
import com.example.playlistmaker.domain.repository.PlayerRepository
import com.example.playlistmaker.domain.repository.SettingsRepository
import com.example.playlistmaker.domain.repository.TracksRepository
import com.example.playlistmaker.domain.usecases.ControlPlayerUseCase
import com.example.playlistmaker.domain.usecases.GetDarkModeUseCase
import com.example.playlistmaker.domain.usecases.IDarkModeUseCase
import com.example.playlistmaker.domain.usecases.SetDarkModeUseCase
import com.example.playlistmaker.presentation.App

object Creator {

    private lateinit var app: Application
    fun setApplication(app: App) {
        this.app = app
    }

    fun provideSetDarkModeUseCase(): IDarkModeUseCase {
        return SetDarkModeUseCase(provideSettingsRepository())
    }

    fun provideGetDarkModeUseCase(): IDarkModeUseCase {
        return GetDarkModeUseCase(provideSettingsRepository())
    }

    private fun provideSettingsRepository(): SettingsRepository {
        return SettingsRepositoryImpl(app)
    }

    fun provideControlPlayerUseCase(): ControlPlayerUseCase {
        return ControlPlayerUseCase(providePlayerControl())
    }

    private fun providePlayerControl(): PlayerRepository {
        return PlayerRepositoryImpl()
    }

    fun provideTracksInteractor(): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository())
    }

    private fun getTracksRepository(): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient())
    }

}