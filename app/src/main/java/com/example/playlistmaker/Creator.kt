package com.example.playlistmaker

import android.app.Application
import android.content.Context
import com.example.playlistmaker.data.network.RetrofitNetworkClient
import com.example.playlistmaker.data.repository.PlayerRepositoryImpl
import com.example.playlistmaker.data.repository.SettingsRepositoryImpl
import com.example.playlistmaker.data.repository.TracksRepositoryImpl
import com.example.playlistmaker.domain.api.TracksInteractor
import com.example.playlistmaker.domain.impl.TracksInteractImpl
import com.example.playlistmaker.domain.repository.PlayerRepository
import com.example.playlistmaker.domain.repository.SettingsRepository
import com.example.playlistmaker.domain.repository.TracksRepository
import com.example.playlistmaker.domain.usecases.IClearSearchHistoryUseCase
import com.example.playlistmaker.domain.usecases.IFindTracksUseCase
import com.example.playlistmaker.domain.usecases.ISaveSearchHistoryUseCase
import com.example.playlistmaker.domain.usecases.impl.ControlPlayerUseCase
import com.example.playlistmaker.domain.usecases.impl.GetDarkModeUseCase
import com.example.playlistmaker.domain.usecases.IGetDarkModeUseCase
import com.example.playlistmaker.domain.usecases.IGetSearchHistoryUseCase
import com.example.playlistmaker.domain.usecases.ISetDarkModeUseCase
import com.example.playlistmaker.domain.usecases.impl.SaveSearchHistoryUseCase
import com.example.playlistmaker.domain.usecases.impl.ClearSearchHistoryUseCase
import com.example.playlistmaker.domain.usecases.impl.FindTracksUseCase
import com.example.playlistmaker.domain.usecases.impl.GetSearchHistoryUseCase
import com.example.playlistmaker.domain.usecases.impl.SetDarkModeUseCase
import com.example.playlistmaker.presentation.App

object Creator {

    private lateinit var app: Application
    fun setApplication(app: App) {
        this.app = app
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

    fun provideFindTracksUseCase(context: Context): IFindTracksUseCase {
        return FindTracksUseCase(getTracksRepository(context))
    }

    fun provideTracksInteractor(context: Context): TracksInteractor {
        return TracksInteractImpl(getTracksRepository(context))
    }

    private fun getTracksRepository(context: Context): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(context))
    }

}