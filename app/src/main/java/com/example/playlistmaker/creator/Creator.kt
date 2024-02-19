package com.example.playlistmaker.creator

import android.app.Application
import android.content.Context
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.player.impl.PlayerRepositoryImpl
import com.example.playlistmaker.data.settings.impl.SettingsRepositoryImpl
import com.example.playlistmaker.data.search.impl.TracksRepositoryImpl
import com.example.playlistmaker.domain.search.TracksInteractor
import com.example.playlistmaker.domain.search.impl.TracksInteractorImpl
import com.example.playlistmaker.data.player.PlayerRepository
import com.example.playlistmaker.data.settings.SettingsRepository
import com.example.playlistmaker.data.search.TracksRepository
import com.example.playlistmaker.domain.usecases.IClearSearchHistoryUseCase
import com.example.playlistmaker.domain.usecases.ISaveSearchHistoryUseCase
import com.example.playlistmaker.domain.usecases.impl.ControlPlayerUseCase
import com.example.playlistmaker.domain.usecases.impl.GetDarkModeUseCase
import com.example.playlistmaker.domain.usecases.IGetDarkModeUseCase
import com.example.playlistmaker.domain.usecases.IGetSearchHistoryUseCase
import com.example.playlistmaker.domain.usecases.ISetDarkModeUseCase
import com.example.playlistmaker.domain.usecases.impl.SaveSearchHistoryUseCase
import com.example.playlistmaker.domain.usecases.impl.ClearSearchHistoryUseCase
import com.example.playlistmaker.domain.usecases.impl.GetSearchHistoryUseCase
import com.example.playlistmaker.domain.usecases.impl.SetDarkModeUseCase
import com.example.playlistmaker.ui.App

object Creator {

    private lateinit var app: Application
    fun setApplication(app: App) {
        Creator.app = app
    }

    fun provideSetDarkModeUseCase(): ISetDarkModeUseCase {
        return SetDarkModeUseCase(provideSettingsRepository())
    }

    fun provideGetDarkModeUseCase(): IGetDarkModeUseCase {
        return GetDarkModeUseCase(provideSettingsRepository())
    }

    fun provideGetSearchHistoryUseCase(): IGetSearchHistoryUseCase {
        return GetSearchHistoryUseCase(provideSettingsRepository())
    }
    fun provideClearSearchHistoryUseCase(): IClearSearchHistoryUseCase {
        return ClearSearchHistoryUseCase(provideSettingsRepository())
    }

    fun provideSaveSearchHistoryUseCase(): ISaveSearchHistoryUseCase {
        return SaveSearchHistoryUseCase(provideSettingsRepository())
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

    fun provideTracksInteractor(context: Context): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(context))
    }

    private fun getTracksRepository(context: Context): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(context))
    }

}