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
import com.example.playlistmaker.data.sharing.ExternalNavigator
import com.example.playlistmaker.data.sharing.impl.ExternalNavigatorImpl
import com.example.playlistmaker.domain.player.PlayerInteractor
import com.example.playlistmaker.domain.search.usecase.IClearSearchHistoryUseCase
import com.example.playlistmaker.domain.search.usecase.ISaveSearchHistoryUseCase
import com.example.playlistmaker.domain.player.usecase.impl.ControlPlayerUseCase
import com.example.playlistmaker.domain.player.usecase.impl.PlayerInteractorImpl
import com.example.playlistmaker.domain.settings.usecase.impl.GetDarkModeUseCase
import com.example.playlistmaker.domain.settings.usecase.IGetDarkModeUseCase
import com.example.playlistmaker.domain.search.usecase.IGetSearchHistoryUseCase
import com.example.playlistmaker.domain.settings.usecase.ISetDarkModeUseCase
import com.example.playlistmaker.domain.sharing.usecase.IShareAppUseCase
import com.example.playlistmaker.domain.search.usecase.impl.SaveSearchHistoryUseCase
import com.example.playlistmaker.domain.search.usecase.impl.ClearSearchHistoryUseCase
import com.example.playlistmaker.domain.search.usecase.impl.GetSearchHistoryUseCase
import com.example.playlistmaker.domain.settings.usecase.impl.SetDarkModeUseCase
import com.example.playlistmaker.domain.sharing.usecase.IMessageSupportUseCase
import com.example.playlistmaker.domain.sharing.usecase.IUserAgreementUseCase
import com.example.playlistmaker.domain.sharing.usecase.impl.MessageSupportUseCase
import com.example.playlistmaker.domain.sharing.usecase.impl.ShareAppUseCase
import com.example.playlistmaker.domain.sharing.usecase.impl.UserAgreementUseCase

object Creator {

    private lateinit var app: Application
    fun setApplication(app: App) {
        Creator.app = app
    }

    fun provideUserAgreementUseCase(): IUserAgreementUseCase {
        return UserAgreementUseCase(getExternalNavigator())
    }
    fun provideMessageSupportUseCase(): IMessageSupportUseCase {
        return MessageSupportUseCase(getExternalNavigator())
    }
    fun provideShareAppUseCase(): IShareAppUseCase {
        return ShareAppUseCase(getExternalNavigator())
    }
    private fun getExternalNavigator() : ExternalNavigator {
        return ExternalNavigatorImpl(app)
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

    fun providePlayerInteractor(context: Context): PlayerInteractor {
        return PlayerInteractorImpl(providePlayerControl())
    }

    fun provideTracksInteractor(context: Context): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(context))
    }

    private fun getTracksRepository(context: Context): TracksRepository {
        return TracksRepositoryImpl(RetrofitNetworkClient(context))
    }

}